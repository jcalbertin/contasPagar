package com.lyncas.contas.contaspagar.resource.controller;

import com.lyncas.contas.contaspagar.infrastructure.security.JwtUtil;
import com.lyncas.contas.contaspagar.resource.dto.AuthRequestDTO;
import com.lyncas.contas.contaspagar.resource.dto.AuthResponseDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/token")
    public ResponseEntity<AuthResponseDTO> generateToken(@Valid @RequestBody AuthRequestDTO authRequest) {
        logger.info("gerando um token valido para username: {}", authRequest.getUsername());
        String token = jwtUtil.generateToken(authRequest.getUsername());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

}
