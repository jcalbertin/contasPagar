package com.lyncas.contas.contaspagar.resource.controller.v1;

import com.lyncas.contas.contaspagar.resource.dto.v1.AccountDTO;
import com.lyncas.contas.contaspagar.resource.dto.v1.AlterStatusDTO;
import com.lyncas.contas.contaspagar.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Cria uma nova conta a pagar",
            security = @SecurityRequirement(name = "BearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Conta a ser criada",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AccountDTO.class),
                                    examples = @ExampleObject(name = "Conta de exemplo",
                                    value = """
                {
                    "id": 1,
                    "dataVencimento": "2025-12-31",
                    "dataPagamento": "2025-12-30",
                    "valor": 1500.00,
                    "descricao": "Serviços de telefonia",
                    "situacao": "PENDENTE"
                }
                """
                            ))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conta a pagar criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountDTO.class),
                            examples = @ExampleObject(
                                    name = "Conta criada",
                                    value = """
                {
                    "id": 1,
                    "dataVencimento": "2025-12-31",
                    "dataPagamento": "2025-12-30",
                    "valor": 1500.00,
                    "descricao": "Serviços de telefonia",
                    "situacao": "PENDENTE"
                }
                """
                            ))),
            @ApiResponse(responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500",
                    description = "Erro interno servidor",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        AccountDTO createdAccount = accountService.create(accountDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @Operation(summary = "Atualiza uma conta a pagar", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Não autorizado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountDTO accountDTO) {
        AccountDTO updatedAccount = accountService.update(id, accountDTO);
        return ResponseEntity.ok(updatedAccount);
    }

    @Operation(summary = "Atualiza o status de uma conta a pagar", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Não autorizado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor"
            )
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<AccountDTO> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody AlterStatusDTO alterStatusDTO) {
        AccountDTO updatedAccount = accountService.changeStatus(id, alterStatusDTO);
        return ResponseEntity.ok(updatedAccount);
    }

    @Operation(summary = "Lista paginada de contas a pagar de acordo com um critério de busca", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Não autorizado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Retorno com sucesso"
            )
    })
    @GetMapping
    public ResponseEntity<Page<AccountDTO>> listAccounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "") String descricao,
            Pageable pageable) {
        Page<AccountDTO> accounts = accountService.listByDataVencimentoAndDescricao(
                startDate != null ? startDate : LocalDate.MIN,
                endDate != null ? endDate : LocalDate.MAX,
                descricao,
                pageable
        );
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Obtem uma conta a pagar pelo seu id", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Não autorizado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conta a pagar não encontrada"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Retorno com sucesso"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        AccountDTO accountDTO = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDTO);
    }

    @Operation(summary = "Soma os valores a pagos conforme um periodo de data de pagamentos", security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Não autorizado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Retorno com sucesso"
            )
    })
    @GetMapping("/total-pago")
    public ResponseEntity<BigDecimal> getTotalPaid(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal totalPaid = accountService.getTotalPaid(startDate, endDate);
        return ResponseEntity.ok(totalPaid);
    }

    @Operation(summary = "Importa dados de contas a partir de um arquivo CSV",
            description = "Aceita arquivos .csv (somente) com a seguinte sintaxe: data_vencimento, data_pagamento, valor, descricao, situacao, com este cabeçalho na primeira linha",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Não autorizado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "Executado com sucesso"
            )
    })
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> importAccounts(
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        logger.info("Importando contas a partir de arquvivo: {}", file.getOriginalFilename());
        accountService.importAccounts(file.getInputStream());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
