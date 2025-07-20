package ai.supporter.worker.queue;

import ai.supporter.worker.ticket.SupportTicket;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TicketMessageTest {
    @Test
    void testGetTicket() {
        SupportTicket ticket = new SupportTicket();
        TicketMessage msg = new TicketMessage(ticket);
        assertThat(msg.getTicket()).isSameAs(ticket);
    }
    @Test
    void testNullTicket() {
        TicketMessage msg = new TicketMessage(null);
        assertThat(msg.getTicket()).isNull();
    }
} 