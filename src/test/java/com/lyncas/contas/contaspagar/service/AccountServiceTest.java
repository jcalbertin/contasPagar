package com.lyncas.contas.contaspagar.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lyncas.contas.contaspagar.domain.enums.AccountStatusEnum;
import com.lyncas.contas.contaspagar.domain.model.Account;
import com.lyncas.contas.contaspagar.domain.repository.AccountRepository;
import com.lyncas.contas.contaspagar.exception.AccountNotFoundException;
import com.lyncas.contas.contaspagar.resource.dto.v1.AccountDTO;
import com.lyncas.contas.contaspagar.resource.dto.v1.AlterStatusDTO;
import com.lyncas.contas.contaspagar.resource.mapper.v1.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TaskExecutor taskExecutor;

    private AccountMapper accountMapper;
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        accountMapper = new AccountMapper();
        accountService = new AccountService(accountRepository, accountMapper, taskExecutor);

        account = new Account();
        account.setSituacao(AccountStatusEnum.PENDENTE);
        account.setDataPagamento(LocalDate.of(2025, 1, 1));
        account.setDescricao("Vivo");
        account.setDataVencimento(LocalDate.of(2025, 9, 1));
    }

    @Test
    void createAccountSuccessfully() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setDataPagamento(account.getDataPagamento());
        accountDTO.setSituacao(account.getSituacao());
        accountDTO.setValor(account.getValor());
        accountDTO.setDescricao(account.getDescricao());
        accountDTO.setDataVencimento(account.getDataVencimento());

        when(accountRepository.save(account)).thenReturn(account);

        AccountDTO result = accountService.create(accountDTO);

        assertEquals(accountDTO.getDescricao(), result.getDescricao());
        verify(accountRepository).save(account);
    }

    @Test
    void updateAccountSuccessfully() {
        Long id = 1L;
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setDataPagamento(account.getDataPagamento());
        accountDTO.setSituacao(account.getSituacao());
        accountDTO.setValor(account.getValor());
        accountDTO.setDescricao(account.getDescricao());
        accountDTO.setDataVencimento(account.getDataVencimento());

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        AccountDTO result = accountService.update(id, accountDTO);

        assertEquals(accountDTO.getValor(), result.getValor());
        assertEquals(accountDTO.getDescricao(), result.getDescricao());
        verify(accountRepository).save(account);
    }

    @Test
    void updateAccountNotFound() {
        Long id = 1L;
        AccountDTO accountDTO = new AccountDTO();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.update(id, accountDTO));
    }

    @Test
    void changeStatusSuccessfully() {
        Long id = 1L;
        AlterStatusDTO alterStatusDTO = new AlterStatusDTO();
        alterStatusDTO.setSituacao(AccountStatusEnum.PAGA);

        when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        AccountDTO result = accountService.changeStatus(id, alterStatusDTO);

        assertEquals(AccountStatusEnum.PAGA, result.getSituacao());
        verify(accountRepository).save(account);
    }

    @Test
    void changeStatusAccountNotFound() {
        Long id = 1L;
        AlterStatusDTO alterStatusDTO = new AlterStatusDTO();
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.changeStatus(id, alterStatusDTO));
    }

    @Test
    void listByDataVencimentoAndDescricao() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        String descricao = "Servico";
        Pageable pageable = PageRequest.of(0, 10);

        Account account = new Account();
        account.setDataVencimento(LocalDate.now().plusDays(3));
        account.setDataPagamento(LocalDate.now().minusDays(1));
        account.setValor(BigDecimal.TEN);
        account.setDescricao("Conta de luz");
        account.setSituacao(AccountStatusEnum.PENDENTE);

        Page<Account> accounts = new PageImpl<>(Collections.singletonList(account));
        when(accountRepository.findByDataVencimentoBetweenAndDescricaoContainingIgnoreCase(startDate, endDate, descricao, pageable)).thenReturn(accounts);
        //when(accountMapper.toDTO(any(Account.class))).thenReturn(new AccountDTO());

        Page<AccountDTO> result = accountService.listByDataVencimentoAndDescricao(startDate, endDate, descricao, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAccountByIdSuccessfully() {
        Long id = 1L;
        //Account account = new Account();
        when(accountRepository.findById(id)).thenReturn(Optional.of(account));
        //when(accountMapper.toDTO(account)).thenReturn(new AccountDTO());

        AccountDTO result = accountService.getAccountById(id);

        assertNotNull(result);
    }

    @Test
    void getAccountByIdNotFound() {
        Long id = 1L;
        when(accountRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.getAccountById(id));
    }

    @Test
    void getTotalPaidSuccessfully() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        BigDecimal totalPaid = BigDecimal.TEN;
        when(accountRepository.sumValorByDataPagamento(startDate, endDate)).thenReturn(totalPaid);

        BigDecimal result = accountService.getTotalPaid(startDate, endDate);

        assertEquals(totalPaid, result);
    }

    @Test
    void importAccountsSuccessfully() {
        String csvData = "dataVencimento,dataPagamento,valor,descricao,situacao\n2023-01-01,2023-01-02,100.00,Servico,PAGA";
        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes());
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        accountService.importAccounts(inputStream);

        verify(accountRepository).saveAllAndFlush(anyList());
    }

    @Test
    void importAccountsDoesThrowsExceptionWhenNoValidDataToProcess() {
        InputStream inputStream = new ByteArrayInputStream("dados invalidos".getBytes());
        assertDoesNotThrow( () -> accountService.importAccounts(inputStream));
    }
}
