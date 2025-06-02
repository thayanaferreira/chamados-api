package br.com.nomeempresa.chamados_api.service;

import br.com.nomeempresa.chamados_api.dto.TicketDTO;
import br.com.nomeempresa.chamados_api.model.Categoria;
import br.com.nomeempresa.chamados_api.model.Sentimento;
import br.com.nomeempresa.chamados_api.model.Ticket;
import br.com.nomeempresa.chamados_api.repository.TicketRepository;
import br.com.nomeempresa.chamados_api.service.implementation.TicketServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TicketServiceIntegrationTest {

    @Autowired
    private TicketRepository repository;

    @Autowired
    private TicketServiceImpl service;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void integraSalvarERecuperarTicket() {
        TicketDTO dto = new TicketDTO();
        dto.setTitulo("Chamado Int");
        dto.setDescricao("Descrição Int");
        dto.setCategoria(Categoria.SUGESTAO);
        dto.setSentimento(Sentimento.POSITIVO);

        Ticket salvo = service.salvar(dto);

        assertNotNull(salvo.getId());
        Optional<Ticket> doBanco = repository.findById(salvo.getId());
        assertTrue(doBanco.isPresent());
        assertEquals("Chamado Int", doBanco.get().getTitulo());
    }

    @Test
    void deveListarTicketsDoBanco() {
        Ticket t1 = Ticket.builder()
                .titulo("A")
                .descricao("Desc A")
                .categoria(Categoria.REQUISICAO)
                .sentimento(Sentimento.NEGATIVO)
                .build();
        Ticket t2 = Ticket.builder()
                .titulo("B")
                .descricao("Desc B")
                .categoria(Categoria.ERRO)
                .sentimento(Sentimento.NEUTRO)
                .build();
        repository.save(t1);
        repository.save(t2);

        List<Ticket> lista = service.listar();

        assertEquals(2, lista.size());
    }

    @Test
    void deveRetornarErroAoAtualizarQuandoNaoExistir() {
        TicketDTO dto = new TicketDTO();
        dto.setTitulo("Inexistente");
        dto.setDescricao("Desc");

        assertThrows(EntityNotFoundException.class, () -> service.atualizar(12345L, dto));
    }
}

