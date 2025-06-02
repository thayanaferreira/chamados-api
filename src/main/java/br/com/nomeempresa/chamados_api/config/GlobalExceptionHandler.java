package br.com.nomeempresa.chamados_api.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Campos @NotBlank, @NotNull
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidations(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        for (var error : ex.getBindingResult().getAllErrors()) {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            erros.put(campo, mensagem);
        }
        logger.warn("Erro de validação: {}", erros);
        return ResponseEntity.badRequest().body(erros);
    }

    // Enums inválidos, ou JSON malformado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleEnumParse(HttpMessageNotReadableException ex, HttpServletRequest req) {
        Throwable cause = ex.getCause();
        String mensagem = "Erro ao processar o corpo da requisição.";
        if (cause instanceof InvalidFormatException ife) {
            var target = ife.getTargetType().getSimpleName();
            mensagem = "Valor inválido para campo do tipo " + target + ". Verifique os valores permitidos.";
        }
        logger.warn("Erro ao ler JSON da requisição {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of("erro", mensagem));
    }

    // ID inexistente no banco
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        logger.warn("Recurso não encontrado em {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(404).body(Map.of("erro", "Recurso não encontrado."));
    }

    // Fallback genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOthers(Exception ex, HttpServletRequest req) {
        logger.error("Erro inesperado em {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(500).body(Map.of("erro", "Erro interno. Tente novamente mais tarde."));
    }
}
