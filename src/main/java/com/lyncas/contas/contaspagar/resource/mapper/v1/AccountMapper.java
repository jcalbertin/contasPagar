package com.lyncas.contas.contaspagar.resource.mapper.v1;

import com.lyncas.contas.contaspagar.domain.model.Account;
import com.lyncas.contas.contaspagar.resource.dto.v1.AccountDTO;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }

        return new AccountDTO.Builder()
                .id(account.getId())
                .dataVencimento(account.getDataVencimento())
                .dataPagamento(account.getDataPagamento())
                .valor(account.getValor())
                .descricao(account.getDescricao())
                .situacao(account.getSituacao())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    public Account toEntity(AccountDTO accountDTO) {
        if (accountDTO == null) {
            return null;
        }

        return new Account.Builder()
                .dataVencimento(accountDTO.getDataVencimento())
                .dataPagamento(accountDTO.getDataPagamento())
                .valor(accountDTO.getValor())
                .descricao(accountDTO.getDescricao())
                .situacao(accountDTO.getSituacao())
                .build();
    }
}
