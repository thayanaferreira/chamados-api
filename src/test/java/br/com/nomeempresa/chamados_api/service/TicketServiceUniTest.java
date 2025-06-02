package br.com.nomeempresa.chamados_api.service;

import br.com.nomeempresa.chamados_api.dto.TicketDTO;
import br.com.nomeempresa.chamados_api.model.Categoria;
import br.com.nomeempresa.chamados_api.model.Sentimento;
import br.com.nomeempresa.chamados_api.model.Ticket;
import br.com.nomeempresa.chamados_api.repository.TicketRepository;
import br.com.nomeempresa.chamados_api.service.implementation.TicketServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceUniTest {

    @Mock
    private TicketRepository repository;

    @InjectMocks
    private TicketServiceImpl service;

    @Test
    void deveSalvarTicketComSucesso() {
        TicketDTO dto = new TicketDTO();
        dto.setTitulo("Chamado de teste");
        dto.setDescricao("Descrição de teste");
        dto.setCategoria(Categoria.INCIDENTE);
        dto.setSentimento(Sentimento.NEGATIVO);

        Ticket salvoEsperado = Ticket.builder()
                .id(1L)
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .categoria(dto.getCategoria())
                .sentimento(dto.getSentimento())
                .build();

        when(repository.save(any(Ticket.class))).thenReturn(salvoEsperado);

        Ticket resultado = service.salvar(dto);

        assertNotNull(resultado.getId());
        assertEquals("Chamado de teste", resultado.getTitulo());
        verify(repository, times(1)).save(any(Ticket.class));
    }

    @Test
    void deveListarTodosTickets() {
        Ticket t1 = Ticket.builder().id(1L).titulo("A").build();
        Ticket t2 = Ticket.builder().id(2L).titulo("B").build();

        when(repository.findAll()).thenReturn(List.of(t1, t2));

        List<Ticket> lista = service.listar();

        assertEquals(2, lista.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void deveAtualizarTicketComSucesso() {
        Long id = 42L;
        Ticket existente = Ticket.builder()
                .id(id)
                .titulo("Antigo")
                .descricao("Desc antiga")
                .categoria(Categoria.SUGESTAO)
                .sentimento(Sentimento.NEUTRO)
                .build();

        TicketDTO dto = new TicketDTO();
        dto.setTitulo("Novo Título");
        dto.setDescricao("Nova descrição");
        dto.setCategoria(Categoria.ERRO);
        dto.setSentimento(Sentimento.POSITIVO);

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket atualizado = service.atualizar(id, dto);

        assertEquals("Novo Título", atualizado.getTitulo());
        assertEquals(Categoria.ERRO, atualizado.getCategoria());
        assertEquals(Sentimento.POSITIVO, atualizado.getSentimento());
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(existente);
    }

    @Test
    void deveLancarExcecaoAoAtualizarTicketInexistente() {
        Long idInexistente = 999L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());
        TicketDTO dto = new TicketDTO();
        dto.setTitulo("Inexistente");

        EntityNotFoundException exc = assertThrows(
                EntityNotFoundException.class,
                () -> service.atualizar(idInexistente, dto)
        );
        assertTrue(exc.getMessage().contains("Ticket com ID " + idInexistente + " não encontrado"));
        verify(repository, times(1)).findById(idInexistente);
        verify(repository, never()).save(any());
    }
}
