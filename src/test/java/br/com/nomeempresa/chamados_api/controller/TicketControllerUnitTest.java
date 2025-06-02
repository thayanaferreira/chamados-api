package br.com.nomeempresa.chamados_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.nomeempresa.chamados_api.dto.TicketDTO;
import br.com.nomeempresa.chamados_api.model.Categoria;
import br.com.nomeempresa.chamados_api.model.Sentimento;
import br.com.nomeempresa.chamados_api.model.Ticket;
import br.com.nomeempresa.chamados_api.service.interfaces.TicketService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void deveListarTicketsComSucesso() throws Exception {
        when(service.listar()).thenReturn(List.of(
                Ticket.builder().id(1L).build(),
                Ticket.builder().id(2L).build()
        ));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void deveRetornarErro500QuandoServiceFalhaNoListar() throws Exception {
        when(service.listar()).thenThrow(new RuntimeException("Erro interno"));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deveCriarTicketComSucesso() throws Exception {
        TicketDTO dto = new TicketDTO();
        dto.setTitulo("Teste");
        dto.setDescricao("desc");
        dto.setCategoria(Categoria.SUGESTAO);
        dto.setSentimento(Sentimento.NEGATIVO);

        Ticket ticketSalvo = Ticket.builder()
                .id(1L)
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .categoria(dto.getCategoria())
                .sentimento(dto.getSentimento())
                .build();

        when(service.salvar(any())).thenReturn(ticketSalvo);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Teste"));
    }

    @Test
    void deveRetornarErroBadRequestAoCriarTicketInvalido() throws Exception {
        TicketDTO dto = new TicketDTO();

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.titulo").exists())
                .andExpect(jsonPath("$.descricao").exists())
                .andExpect(jsonPath("$.categoria").exists())
                .andExpect(jsonPath("$.sentimento").exists());
    }

    @Test
    void deveAtualizarTicketComSucesso() throws Exception {
        TicketDTO dto = new TicketDTO();
        dto.setTitulo("Atualizado");
        dto.setDescricao("desc");
        dto.setCategoria(Categoria.OUTROS);
        dto.setSentimento(Sentimento.MISTO);

        Ticket atualizado = Ticket.builder()
                .id(2L)
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .categoria(dto.getCategoria())
                .sentimento(dto.getSentimento())
                .build();

        when(service.atualizar(eq(2L), any())).thenReturn(atualizado);

        mockMvc.perform(put("/api/tickets/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.titulo").value("Atualizado"));
    }

    @Test
    void deveRetornarNotFoundAoAtualizarIDInexistente() throws Exception {
        TicketDTO dto = new TicketDTO();
        dto.setTitulo("Teste");
        dto.setDescricao("desc");
        dto.setCategoria(Categoria.ERRO);
        dto.setSentimento(Sentimento.POSITIVO);

        when(service.atualizar(eq(999L), any()))
                .thenThrow(new EntityNotFoundException("Ticket não encontrado"));

        mockMvc.perform(put("/api/tickets/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}
