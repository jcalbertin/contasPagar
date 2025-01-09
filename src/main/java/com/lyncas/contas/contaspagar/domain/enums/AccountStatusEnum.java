package com.lyncas.contas.contaspagar.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.lyncas.contas.contaspagar.exception.SituacaoException;

public enum AccountStatusEnum {
    PENDENTE,
    PAGA,
    CANCELADA;

    @JsonCreator
    public static AccountStatusEnum fromValue(String value) {
        for (AccountStatusEnum status : AccountStatusEnum.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new SituacaoException("Status inválido: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
