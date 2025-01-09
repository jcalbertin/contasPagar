package com.lyncas.contas.contaspagar.resource.dto.v1;

import com.lyncas.contas.contaspagar.domain.enums.AccountStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class AlterStatusDTO {

    @NotNull(message = "Situacao é obrigatória")
    @Schema(description = "Situação da conta", example = "PENDENTE", allowableValues = {"PENDENTE", "PAGA", "CANCELADA"})
    private AccountStatusEnum situacao;

    public AlterStatusDTO() {}

    public AccountStatusEnum getSituacao() {
        return situacao;
    }

    public void setSituacao(AccountStatusEnum situacao) {
        this.situacao = situacao;
    }
}
