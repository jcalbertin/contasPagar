package com.lyncas.contas.contaspagar.resource.controller;

import com.lyncas.contas.contaspagar.domain.enums.AccountStatusEnum;
import com.lyncas.contas.contaspagar.resource.dto.v1.AccountDTO;
import com.lyncas.contas.contaspagar.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Deve criar conta com sucesso")
    void createAccountSuccessfully() throws Exception {
        String accountJson = """
                {
                    "dataVencimento": "2025-12-31",
                    "dataPagamento": "2025-12-30",
                    "valor": 1500.00,
                    "descricao": "Serviços de telefonia",
                    "situacao": "PENDENTE"
                }
                """;

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Serviços de telefonia"));
    }

    @Test
    @DisplayName("Deve emitir BadRequest quando tenta criar conta com situacao inválida")
    void deveEmitirBadRequestQuandoTentaCriaContaComSituacaoDesconhecida() throws Exception {
        String accountJson = """
                {
                    "dataVencimento": "2025-12-31",
                    "dataPagamento": "2025-12-30",
                    "valor": 1500.00,
                    "descricao": "Serviços de telefonia",
                    "situacao": "NAO_PAGOU_AINDA"
                }
                """;

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve atualizar conta com sucesso")
    void updateAccountSuccessfully() throws Exception {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setDataVencimento(LocalDate.of(2025, 12, 31));
        accountDTO.setDataPagamento(LocalDate.of(2025, 12, 30));
        accountDTO.setValor(BigDecimal.valueOf(1500.00));
        accountDTO.setDescricao("Serviços de telefonia");
        accountDTO.setSituacao(AccountStatusEnum.PENDENTE);
        AccountDTO createdAccount = accountService.create(accountDTO);

        String updatedAccountJson = """
                {
                    "dataVencimento": "2025-12-31",
                    "dataPagamento": "2025-12-30",
                    "valor": 2000.00,
                    "descricao": "Serviços de internet",
                    "situacao": "PENDENTE"
                }
                """;

        mockMvc.perform(put("/api/v1/accounts/" + createdAccount.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedAccountJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Serviços de internet"));
    }

    @Test
    @DisplayName("Deve alterar status da conta com sucesso")
    void changeStatusSuccessfully() throws Exception {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setDataVencimento(LocalDate.of(2025, 12, 31));
        accountDTO.setDataPagamento(LocalDate.of(2025, 12, 30));
        accountDTO.setValor(BigDecimal.valueOf(1500.00));
        accountDTO.setDescricao("Serviços de telefonia");
        accountDTO.setSituacao(AccountStatusEnum.PENDENTE);
        AccountDTO createdAccount = accountService.create(accountDTO);

        String alterStatusJson = """
                {
                    "situacao": "PAGA"
                }
                """;

        mockMvc.perform(patch("/api/v1/accounts/" + createdAccount.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(alterStatusJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("PAGA"));
    }

    @Test
    @DisplayName("Deve listar contas com sucesso")
    void listAccountsSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/accounts")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")
                        .param("descricao", "Serviço")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Deve retornar 404 quando nao encontrado")
    void getAccountByIdNotFoundExceptionSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Deve apresentar o total da soma das contas pagas")
    void getTotalPaidSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/total-pago")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    @DisplayName("Deve importar contas com sucesso")
    void importAccountsSuccessfully() throws Exception {
        String csvContent = "dataVencimento,dataPagamento,valor,descricao,situacao\n2023-01-01,2023-01-02,100.00,Servico,PAGA";
        MockMultipartFile file = new MockMultipartFile("file", "accounts.csv", "text/csv", csvContent.getBytes());

        mockMvc.perform(multipart("/api/v1/accounts/import")
                        .file(file))
                .andExpect(status().isCreated());
    }
}
