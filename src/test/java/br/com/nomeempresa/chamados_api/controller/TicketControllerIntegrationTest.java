package br.com.nomeempresa.chamados_api.controller;

import br.com.nomeempresa.chamados_api.dto.TicketDTO;
import br.com.nomeempresa.chamados_api.model.Categoria;
import br.com.nomeempresa.chamados_api.model.Sentimento;
import br.com.nomeempresa.chamados_api.model.Ticket;
import br.com.nomeempresa.chamados_api.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TicketControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TicketRepository repository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        baseUrl = "http://localhost:" + port + "/api/tickets";
    }

    @Test
    void integraListarTickets() {
        repository.save(Ticket.builder()
                .titulo("T1")
                .descricao("D1")
                .categoria(Categoria.ACESSO)
                .sentimento(Sentimento.NEGATIVO)
                .build());

        ResponseEntity<Ticket[]> response = restTemplate.getForEntity(baseUrl, Ticket[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 1);
    }

    @Test
    void integraCriarTicket() {
        TicketDTO dto = new TicketDTO();
        dto.setTitulo("Chamado Int");
        dto.setDescricao("Desc Int");
        dto.setCategoria(Categoria.SUGESTAO);
        dto.setSentimento(Sentimento.POSITIVO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TicketDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<Ticket> response = restTemplate.postForEntity(baseUrl, request, Ticket.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Ticket criado = response.getBody();
        assertNotNull(criado);
        assertEquals("Chamado Int", criado.getTitulo());
    }

    @Test
    void integraCriarTicketInvalido() {
        TicketDTO dto = new TicketDTO(); // vazio
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TicketDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Título é obrigatório"));
    }

    @Test
    void integraAtualizarTicketComSucesso() {

        Ticket existente = repository.save(Ticket.builder()
                .titulo("Old")
                .descricao("Old Desc")
                .categoria(Categoria.ACESSO)
                .sentimento(Sentimento.NEUTRO)
                .build());

        TicketDTO dto = new TicketDTO();
        dto.setTitulo("Atualizado Int");
        dto.setDescricao("Desc Atualizada");
        dto.setCategoria(Categoria.REQUISICAO);
        dto.setSentimento(Sentimento.NEGATIVO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TicketDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<Ticket> response = restTemplate.exchange(
                baseUrl + "/" + existente.getId(),
                HttpMethod.PUT,
                request,
                Ticket.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Ticket atualizado = response.getBody();
        assertNotNull(atualizado);
        assertEquals("Atualizado Int", atualizado.getTitulo());
    }

    @Test
    void integraAtualizarTicketInexistente() {
        TicketDTO dto = new TicketDTO();
        dto.setTitulo("X");
        dto.setDescricao("X");
        dto.setCategoria(Categoria.ERRO);
        dto.setSentimento(Sentimento.POSITIVO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TicketDTO> request = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/9999",
                HttpMethod.PUT,
                request,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
