package br.com.nomeempresa.chamados_api.service.implementation;

import br.com.nomeempresa.chamados_api.dto.TicketDTO;
import br.com.nomeempresa.chamados_api.model.Ticket;
import br.com.nomeempresa.chamados_api.repository.TicketRepository;
import br.com.nomeempresa.chamados_api.service.interfaces.TicketService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository repository;

    public TicketServiceImpl(TicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Ticket> listar() {
        return repository.findAll();
    }

    @Override
    public Ticket salvar(TicketDTO dto) {
        Ticket ticket = Ticket.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .categoria(dto.getCategoria())
                .sentimento(dto.getSentimento())
                .build();
        return repository.save(ticket);
    }

    @Override
    public Ticket atualizar(Long id, TicketDTO dto) {
        Ticket existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket com ID " + id + " não encontrado")); // substituído por exceção específica
        existente.setTitulo(dto.getTitulo());
        existente.setDescricao(dto.getDescricao());
        existente.setCategoria(dto.getCategoria());
        existente.setSentimento(dto.getSentimento());
        return repository.save(existente);
    }
}
