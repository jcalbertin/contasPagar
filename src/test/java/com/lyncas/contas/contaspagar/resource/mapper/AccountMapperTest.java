package com.lyncas.contas.contaspagar.resource.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.lyncas.contas.contaspagar.domain.enums.AccountStatusEnum;
import com.lyncas.contas.contaspagar.domain.model.Account;
import com.lyncas.contas.contaspagar.resource.dto.v1.AccountDTO;
import com.lyncas.contas.contaspagar.resource.mapper.v1.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

class AccountMapperTest {

    private AccountMapper accountMapper;

    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapper();
    }

    @Test
    void toDTOWithValidAccount() {
        Account account = new Account.Builder()
                .id(1L)
                .dataVencimento(LocalDate.now())
                .dataPagamento(LocalDate.now().plusDays(1))
                .valor(BigDecimal.TEN)
                .descricao("Test Description")
                .situacao(AccountStatusEnum.CANCELADA)
                .build();

        AccountDTO accountDTO = accountMapper.toDTO(account);

        assertNotNull(accountDTO);
        assertEquals(account.getId(), accountDTO.getId());
        assertEquals(account.getDataVencimento(), accountDTO.getDataVencimento());
        assertEquals(account.getDataPagamento(), accountDTO.getDataPagamento());
        assertEquals(account.getValor(), accountDTO.getValor());
        assertEquals(account.getDescricao(), accountDTO.getDescricao());
        assertEquals(account.getSituacao(), accountDTO.getSituacao());
        assertEquals(account.getCreatedAt(), accountDTO.getCreatedAt());
        assertEquals(account.getUpdatedAt(), accountDTO.getUpdatedAt());
    }

    @Test
    void toDTOWithNullAccount() {
        AccountDTO accountDTO = accountMapper.toDTO(null);

        assertNull(accountDTO);
    }

    @Test
    void toEntityWithValidAccountDTO() {
        AccountDTO accountDTO = new AccountDTO.Builder()
                .dataVencimento(LocalDate.now())
                .dataPagamento(LocalDate.now().plusDays(1))
                .valor(BigDecimal.TEN)
                .descricao("Test Description")
                .situacao(AccountStatusEnum.PENDENTE)
                .build();

        Account account = accountMapper.toEntity(accountDTO);

        assertNotNull(account);
        assertEquals(accountDTO.getDataVencimento(), account.getDataVencimento());
        assertEquals(accountDTO.getDataPagamento(), account.getDataPagamento());
        assertEquals(accountDTO.getValor(), account.getValor());
        assertEquals(accountDTO.getDescricao(), account.getDescricao());
        assertEquals(accountDTO.getSituacao(), account.getSituacao());
    }

    @Test
    void toEntityWithNullAccountDTO() {
        Account account = accountMapper.toEntity(null);

        assertNull(account);
    }
}
