package com.lyncas.contas.contaspagar.resource.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lyncas.contas.contaspagar.resource.controller.v1.AccountController;
import com.lyncas.contas.contaspagar.resource.dto.v1.AccountDTO;
import com.lyncas.contas.contaspagar.resource.dto.v1.AlterStatusDTO;
import com.lyncas.contas.contaspagar.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccountSuccessfully() {
        AccountDTO accountDTO = new AccountDTO();
        when(accountService.create(accountDTO)).thenReturn(accountDTO);

        ResponseEntity<AccountDTO> response = accountController.createAccount(accountDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(accountDTO, response.getBody());
    }

    @Test
    void updateAccountSuccessfully() {
        Long id = 1L;
        AccountDTO accountDTO = new AccountDTO();
        when(accountService.update(id, accountDTO)).thenReturn(accountDTO);

        ResponseEntity<AccountDTO> response = accountController.updateAccount(id, accountDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accountDTO, response.getBody());
    }

    @Test
    void changeStatusSuccessfully() {
        Long id = 1L;
        AlterStatusDTO alterStatusDTO = new AlterStatusDTO();
        AccountDTO accountDTO = new AccountDTO();
        when(accountService.changeStatus(id, alterStatusDTO)).thenReturn(accountDTO);

        ResponseEntity<AccountDTO> response = accountController.changeStatus(id, alterStatusDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accountDTO, response.getBody());
    }

    @Test
    void changeStatusNotSuccessfully() {
        Long id = 1L;
        AlterStatusDTO alterStatusDTO = new AlterStatusDTO();
        alterStatusDTO.setSituacao(null);
        when(accountService.changeStatus(id, alterStatusDTO)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> accountService.changeStatus(id, alterStatusDTO));
    }

    @Test
    void listAccountsSuccessfully() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        String descricao = "Servico";
        Pageable pageable = PageRequest.of(0, 10);
        Page<AccountDTO> accounts = new PageImpl<>(Collections.singletonList(new AccountDTO()));
        when(accountService.listByDataVencimentoAndDescricao(startDate, endDate, descricao, pageable)).thenReturn(accounts);

        ResponseEntity<Page<AccountDTO>> response = accountController.listAccounts(startDate, endDate, descricao, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void getAccountByIdSuccessfully() {
        Long id = 1L;
        AccountDTO accountDTO = new AccountDTO();
        when(accountService.getAccountById(id)).thenReturn(accountDTO);

        ResponseEntity<AccountDTO> response = accountController.getAccountById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accountDTO, response.getBody());
    }

    @Test
    void getTotalPaidSuccessfully() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);
        BigDecimal totalPaid = BigDecimal.TEN;
        when(accountService.getTotalPaid(startDate, endDate)).thenReturn(totalPaid);

        ResponseEntity<BigDecimal> response = accountController.getTotalPaid(startDate, endDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(totalPaid, response.getBody());
    }

    @Test
    void importAccountsSuccessfully() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("dataVencimento,dataPagamento,valor,descricao,situacao\n2023-01-01,2023-01-02,100.00,Servico,PAGA".getBytes()));

        ResponseEntity<Void> response = accountController.importAccounts(file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(accountService).importAccounts(any());
    }
}
