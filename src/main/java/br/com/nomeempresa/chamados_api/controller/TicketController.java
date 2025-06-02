package br.com.nomeempresa.chamados_api.controller;

import br.com.nomeempresa.chamados_api.dto.TicketDTO;
import br.com.nomeempresa.chamados_api.model.Ticket;
import br.com.nomeempresa.chamados_api.service.interfaces.TicketService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> listar() {
        logger.info("GET /api/tickets chamado");
        List<Ticket> tickets = service.listar();
        logger.debug("Tickets retornados: {}", tickets);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping
    public ResponseEntity<Ticket> criar(@Valid @RequestBody TicketDTO dto) {
        logger.info("POST /api/tickets - payload recebido: {}", dto);
        Ticket salvo = service.salvar(dto);
        logger.debug("Ticket salvo: {}", salvo);
        return ResponseEntity.ok(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> atualizar(@PathVariable Long id, @Valid @RequestBody TicketDTO dto) {
        logger.info("PUT /api/tickets/{} - payload recebido: {}", id, dto);
        Ticket atualizado = service.atualizar(id, dto);
        logger.debug("Ticket atualizado: {}", atualizado);
        return ResponseEntity.ok(atualizado);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            erros.put(campo, mensagem);
        });
        logger.warn("Validação falhou: {}", erros);
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleEnumErrors(IllegalArgumentException ex) {
        logger.warn("Erro ao converter enum: {}", ex.getMessage());
        return ResponseEntity.badRequest().body("Valor inválido para campo do tipo enum. Verifique os valores permitidos.");
    }
}
