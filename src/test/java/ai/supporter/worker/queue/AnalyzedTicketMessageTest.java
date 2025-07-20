package ai.supporter.worker.queue;

import ai.supporter.worker.llm.TicketAnalysisResult;
import ai.supporter.worker.ticket.SupportTicket;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AnalyzedTicketMessageTest {
    @Test
    void testGetters() {
        SupportTicket ticket = new SupportTicket();
        TicketAnalysisResult analysis = new TicketAnalysisResult();
        AnalyzedTicketMessage msg = new AnalyzedTicketMessage(ticket, analysis);
        assertThat(msg.getTicket()).isSameAs(ticket);
        assertThat(msg.getAnalysis()).isSameAs(analysis);
    }
    @Test
    void testNulls() {
        AnalyzedTicketMessage msg = new AnalyzedTicketMessage(null, null);
        assertThat(msg.getTicket()).isNull();
        assertThat(msg.getAnalysis()).isNull();
    }
} 