package ai.supporter.worker.ticket;

import java.util.List;

public interface TicketSource {
  List<SupportTicket> fetchTickets();
} 