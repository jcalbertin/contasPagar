package com.lyncas.contas.contaspagar.infrastructure.config;

import com.lyncas.contas.contaspagar.exception.AccountNotFoundException;
import com.lyncas.contas.contaspagar.exception.SituacaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;

@RestControllerAdvice(basePackages = "com.lyncas.contas.contaspagar.resource.controller")
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(
            AccountNotFoundException ex, WebRequest request) {
        logger.error("Conta não encontrada: {}", ex.getMessage());
        ProblemDetail details = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        details.setProperty("path", request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(details);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ProblemDetail> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        logger.error("Erros de validacao: {}", ex.getMessage());

       var errors = new HashMap<>();
       for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
       }

       ProblemDetail details = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Falhas Validacao");
       details.setProperty("path", request.getDescription(false).replace("uri=", ""));
       details.setProperty("errors", errors);

       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        logger.error("Erro de leitura do JSON: {}", ex.getMessage());

        ProblemDetail details = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
        details.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {

        ProblemDetail details = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        details.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(details);
    }

    @ExceptionHandler(SituacaoException.class)
    public ResponseEntity<ProblemDetail> handleInvalidFormatException(
            SituacaoException ex, WebRequest request) {
        logger.error("Formato inválido: {}", ex.getMessage());

        String mensagemErro = String.format("Valor inválido para o campo Situacao. Use um dos valores permitidos: %s",
               ex.getLocalizedMessage());

        ProblemDetail details = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, mensagemErro);
        details.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(details);
    }


}
