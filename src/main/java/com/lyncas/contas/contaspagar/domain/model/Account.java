package com.lyncas.contas.contaspagar.domain.model;

import com.lyncas.contas.contaspagar.domain.enums.AccountStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "conta")
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @Column(name="data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name="data_pagamento")
    private LocalDate dataPagamento;

    @Column(name="valor", nullable = false)
    private BigDecimal valor;

    @Column(name="descricao", nullable = false)
    private String descricao;

    @Column(name="situacao", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Status da Conta", example = "PENDENTE")
    private AccountStatusEnum situacao;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Account() {}

    private Account(Builder builder) {
        this.id = builder.id;
        this.dataVencimento = builder.dataVencimento;
        this.dataPagamento = builder.dataPagamento;
        this.valor = builder.valor;
        this.descricao = builder.descricao;
        this.situacao = builder.situacao;
    }

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

        public Account build() {
            return new Account(this);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
