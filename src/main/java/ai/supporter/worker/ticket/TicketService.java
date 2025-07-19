package ai.supporter.worker.ticket;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TicketService {
  private final TicketSource ticketSource;

  public TicketService(TicketSource ticketSource) {
    this.ticketSource = ticketSource;
  }

  public List<SupportTicket> getAllTickets() {
    return ticketSource.fetchTickets();
  }
} 