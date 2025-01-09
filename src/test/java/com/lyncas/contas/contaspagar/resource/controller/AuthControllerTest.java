package com.lyncas.contas.contaspagar.resource.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lyncas.contas.contaspagar.infrastructure.security.JwtUtil;
import com.lyncas.contas.contaspagar.resource.dto.AuthRequestDTO;
import com.lyncas.contas.contaspagar.resource.dto.AuthResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateTokenSuccessfully() {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setUsername("usuario");
        String token = "usuario";
        when(jwtUtil.generateToken(authRequest.getUsername())).thenReturn(token);

        ResponseEntity<AuthResponseDTO> response = authController.generateToken(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(token, response.getBody().getToken());
    }

    @Test
    void generateTokenWithInvalidUsername() {
        AuthRequestDTO authRequest = new AuthRequestDTO();
        authRequest.setUsername("");
        when(jwtUtil.generateToken(authRequest.getUsername())).thenThrow(new IllegalArgumentException("Username invalido"));

        assertThrows(IllegalArgumentException.class, () -> authController.generateToken(authRequest));
    }
}
