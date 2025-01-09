package com.lyncas.contas.contaspagar.service;

import com.lyncas.contas.contaspagar.domain.enums.AccountStatusEnum;
import com.lyncas.contas.contaspagar.domain.model.Account;
import com.lyncas.contas.contaspagar.domain.repository.AccountRepository;
import com.lyncas.contas.contaspagar.exception.AccountNotFoundException;
import com.lyncas.contas.contaspagar.exception.ImportAccountException;
import com.lyncas.contas.contaspagar.resource.dto.v1.AccountDTO;
import com.lyncas.contas.contaspagar.resource.dto.v1.AlterStatusDTO;
import com.lyncas.contas.contaspagar.resource.mapper.v1.AccountMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public static final int SKIP_FIRST_LINE_HEADER = 1;
    public static final String ACCOUNT_NOT_FOUND_WITH_ID = "Conta a pagar nao encontrada com o ID: ";
    public static final int MAX_ELEMENTS_OF_SUBLIST = 5000;

    private static final int MAX_CONCURRENT_TASKS = 10;

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TaskExecutor taskExecutor;

    public AccountService(AccountRepository accountRepository,
                          AccountMapper accountMapper,
                          @Qualifier("importExecutor") TaskExecutor taskExecutor) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.taskExecutor = taskExecutor;
    }

    @Transactional
    public AccountDTO create(AccountDTO accountDTO) {
        logger.debug("Iniciando criação de nova conta: {}", accountDTO.getDescricao());
        var account = accountMapper.toEntity(accountDTO);
        var savedAccount = accountRepository.save(account);
        logger.info("Conta criada com ID: {}", savedAccount.getId());
        return accountMapper.toDTO(savedAccount);
    }

    @Transactional
    public AccountDTO update(Long id, AccountDTO accountDTO) {
        logger.debug("Iniciando update de conta ID: {}", id);
        var existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_WITH_ID + id));

        existingAccount.setDataVencimento(accountDTO.getDataVencimento());
        existingAccount.setDataPagamento(accountDTO.getDataPagamento());
        existingAccount.setValor(accountDTO.getValor());
        existingAccount.setDescricao(accountDTO.getDescricao());
        existingAccount.setSituacao(accountDTO.getSituacao());

        var updatedAccount = accountRepository.save(existingAccount);
        logger.info("Conta com ID: {} atualizada", updatedAccount.getId());
        return accountMapper.toDTO(updatedAccount);
    }

    @Transactional
    public AccountDTO changeStatus(Long id, AlterStatusDTO alterStatusDTO) {
        logger.debug("Mudando situacao da conta ID: {} para {}", id, alterStatusDTO.getSituacao());
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_WITH_ID + id));

        account.setSituacao(alterStatusDTO.getSituacao());
        accountRepository.save(account);

        logger.info("Conta ID: {} situacao alterada para {}", id, alterStatusDTO.getSituacao());
        return accountMapper.toDTO(account);
    }

    public Page<AccountDTO> listByDataVencimentoAndDescricao(LocalDate startDate, LocalDate endDate, String descricao, Pageable pageable) {
        logger.debug("Listando contas a pagar entre datas {} ate {}, descricao '{}'", startDate, endDate, descricao);
        Page<Account> accounts = accountRepository.findByDataVencimentoBetweenAndDescricaoContainingIgnoreCase(
                startDate, endDate, descricao, pageable
        );
        return accounts.map(accountMapper::toDTO);
    }

    public AccountDTO getAccountById(Long id) {
        logger.debug("Obtendo conta por id: {}", id);
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND_WITH_ID + id));
        return accountMapper.toDTO(account);
    }

    public BigDecimal getTotalPaid(LocalDate startDate, LocalDate endDate) {
        logger.debug("Calcula valor pago no periodo {} ate {}", startDate, endDate);
        BigDecimal totalPaid = accountRepository.sumValorByDataPagamento(startDate, endDate);
        logger.info("Total pago periodo {} a {}: {}", startDate, endDate, totalPaid);
        return totalPaid;
    }

    @Transactional
    public void importAccounts(InputStream inputStream) {
        Semaphore semaphore = new Semaphore(MAX_CONCURRENT_TASKS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicInteger totalInsertedAccounts = new AtomicInteger(0);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = reader.readLine();

            List<String> chunk = new ArrayList<>(MAX_ELEMENTS_OF_SUBLIST);
            while ((line = reader.readLine()) != null) {
                chunk.add(line);
                if (chunk.size() == MAX_ELEMENTS_OF_SUBLIST) {
                    semaphore.acquire();

                    List<String> chunkToProcess = new ArrayList<>(chunk);
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            logger.info("Processando sublista de tamanho {}", chunkToProcess.size());
                            processChunk(chunkToProcess);
                            totalInsertedAccounts.addAndGet(chunkToProcess.size());
                        } finally {
                            semaphore.release();
                        }
                    }, taskExecutor);
                    futures.add(future);
                    chunk.clear();
                }
            }

            if (!chunk.isEmpty()) {
                semaphore.acquire();
                List<String> chunkToProcess = new ArrayList<>(chunk);
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        logger.info("Processando sublista de tamanho {}", chunkToProcess.size());
                        processChunk(chunkToProcess);
                        totalInsertedAccounts.addAndGet(chunkToProcess.size());
                    } finally {
                        semaphore.release();
                    }
                }, taskExecutor);
                futures.add(future);
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            logger.info("Total de {} contas a pagar importadas do CSV", totalInsertedAccounts.get());

        } catch (Exception e) {
            throw new ImportAccountException("Erro ao importar contas a pagar: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void processChunk(List<String> chunk) {
        List<Account> accounts = chunk.stream()
                .map(this::mapLineToAccount)
                .collect(Collectors.toList());
        accountRepository.saveAllAndFlush(accounts);
    }

    private Account mapLineToAccount(String line) {
        var fields = line.split(",");
        return new Account.Builder()
                .dataVencimento(LocalDate.parse(fields[0]))
                .dataPagamento(fields[1].isEmpty() ? null : LocalDate.parse(fields[1]))
                .valor(new BigDecimal(fields[2]))
                .descricao(fields[3])
                .situacao(AccountStatusEnum.valueOf(fields[4]))
                .build();
    }

}
