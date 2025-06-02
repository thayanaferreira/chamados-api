package br.com.nomeempresa.chamados_api.service.interfaces;

import br.com.nomeempresa.chamados_api.dto.TicketDTO;
import br.com.nomeempresa.chamados_api.model.Ticket;

import java.util.List;

public interface TicketService {
    List<Ticket> listar();
    Ticket salvar(TicketDTO dto);
    Ticket atualizar(Long id, TicketDTO dto);
}
