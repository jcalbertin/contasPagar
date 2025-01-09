package com.lyncas.contas.contaspagar.resource.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthRequestDTO {

    @NotBlank(message = "Username é obrigatório")
    private String username;

    public AuthRequestDTO() {}

    public AuthRequestDTO(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
