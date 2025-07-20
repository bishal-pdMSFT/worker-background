package ai.supporter.worker.llm;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class TicketResponseTest {
    @Test
    void testGettersSetters() {
        TicketResponse resp = new TicketResponse();
        resp.setTicketId("T1");
        resp.setResponseComment("comment");
        Instant now = Instant.now();
        resp.setUpdatedAt(now);
        resp.setComments(List.of("a", "b"));
        assertThat(resp.getTicketId()).isEqualTo("T1");
        assertThat(resp.getResponseComment()).isEqualTo("comment");
        assertThat(resp.getUpdatedAt()).isEqualTo(now);
        assertThat(resp.getComments()).containsExactly("a", "b");
    }
    @Test
    void testNulls() {
        TicketResponse resp = new TicketResponse();
        assertThat(resp.getTicketId()).isNull();
        assertThat(resp.getResponseComment()).isNull();
        assertThat(resp.getUpdatedAt()).isNull();
        assertThat(resp.getComments()).isNull();
    }
} 