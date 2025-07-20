package ai.supporter.worker.llm;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

class TicketAnalysisResultTest {
    @Test
    void testGettersSetters() {
        TicketAnalysisResult result = new TicketAnalysisResult();
        result.setClientName("Client");
        result.setTicketClassification("payment-related");
        result.setQuestionClassification("status-related");
        Instant now = Instant.now();
        result.setTicketTimestamp(now);
        assertThat(result.getClientName()).isEqualTo("Client");
        assertThat(result.getTicketClassification()).isEqualTo("payment-related");
        assertThat(result.getQuestionClassification()).isEqualTo("status-related");
        assertThat(result.getTicketTimestamp()).isEqualTo(now);
    }
    @Test
    void testNulls() {
        TicketAnalysisResult result = new TicketAnalysisResult();
        assertThat(result.getClientName()).isNull();
        assertThat(result.getTicketClassification()).isNull();
        assertThat(result.getQuestionClassification()).isNull();
        assertThat(result.getTicketTimestamp()).isNull();
    }
} 