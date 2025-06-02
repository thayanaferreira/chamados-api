package br.com.nomeempresa.chamados_api.repository;

import br.com.nomeempresa.chamados_api.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
