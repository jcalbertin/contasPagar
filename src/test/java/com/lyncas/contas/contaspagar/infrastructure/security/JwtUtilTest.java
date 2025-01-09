package com.lyncas.contas.contaspagar.infrastructure.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

class JwtUtilTest {

    public static final int UM_SEGUNDO_PARA_EXPIRAR_TOKEN = 1;
    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil.setJwtSecret("chaveJWT_segura_Com_Array_De_216_Bites_HMCA_SHA_AlgorithmXxxxxxxxxxxxxxxxxxxxxxxxx");
        jwtUtil.setJwtExpirationMs(3600000);

    }

    @Test
    void generateTokenSuccessfully() {
        String username = "NomeDoUsuario";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void getUsernameFromValidToken() {
        String username = "NomeDoUsuario";
        String token = jwtUtil.generateToken(username);

        String extractedUsername = jwtUtil.getUsernameFromJWT(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void validateTokenSuccessfully() {
        String username = "testUser";
        String token = jwtUtil.generateToken(username);

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void validateTokenWithInvalidToken() {
        String invalidToken = "tokenInvalido";

        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    @Test
    void validateTokenWithExpiredToken() throws InterruptedException {
        jwtUtil.setJwtExpirationMs(UM_SEGUNDO_PARA_EXPIRAR_TOKEN);
        String username = "usuarioTeste";
        String token = jwtUtil.generateToken(username);

        Thread.sleep(2);

        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    void getUsernameFromInvalidToken() {
        String invalidToken = "token_invalido";

        assertThrows(JwtException.class, () -> jwtUtil.getUsernameFromJWT(invalidToken));
    }
}

