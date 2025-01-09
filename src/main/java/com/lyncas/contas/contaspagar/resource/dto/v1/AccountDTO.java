package com.lyncas.contas.contaspagar.resource.dto.v1;

import com.lyncas.contas.contaspagar.domain.enums.AccountStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountDTO {

    private Long id;

    @NotNull(message = "Data de vencimento obrigatória")
    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    @NotNull(message = "Valor é obrigatório")
    private BigDecimal valor;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull(message = "Situação é obrigatória")
    @Schema(description = "Situação da conta", example = "PENDENTE", allowableValues = {"PENDENTE", "PAGA", "CANCELADA"})
    private AccountStatusEnum situacao;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AccountDTO() {}

    private AccountDTO(Builder builder) {
        this.id = builder.id;
        this.dataVencimento = builder.dataVencimento;
        this.dataPagamento = builder.dataPagamento;
        this.valor = builder.valor;
        this.descricao = builder.descricao;
        this.situacao = builder.situacao;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public AccountStatusEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(AccountStatusEnum situacao) {
        this.situacao = situacao;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Builder

    public static class Builder {
        private Long id;
        private LocalDate dataVencimento;
        private LocalDate dataPagamento;
        private BigDecimal valor;
        private String descricao;
        private AccountStatusEnum situacao;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder dataVencimento(LocalDate dataVencimento) {
            this.dataVencimento = dataVencimento;
            return this;
        }

        public Builder dataPagamento(LocalDate dataPagamento) {
            this.dataPagamento = dataPagamento;
            return this;
        }

        public Builder valor(BigDecimal valor) {
            this.valor = valor;
            return this;
        }

        public Builder descricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public Builder situacao(AccountStatusEnum situacao) {
            this.situacao = situacao;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AccountDTO build() {
            return new AccountDTO(this);
        }
    }
}
