package ai.supporter.worker.ticket;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TicketServiceTest {
  @Autowired
  private TicketService ticketService;

  @Test
  void testReadTicketsFromCsv() {
    List<SupportTicket> tickets = ticketService.getAllTickets();
    assertThat(tickets).isNotNull();
    assertThat(tickets.size()).isGreaterThan(0);
    // Print a few tickets for demonstration
    tickets.stream().limit(3).forEach(ticket ->
      System.out.println(ticket.getTicketId() + ": " + ticket.getDescription())
    );
  }
} 