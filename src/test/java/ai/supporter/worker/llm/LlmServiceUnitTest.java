package ai.supporter.worker.llm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.assertj.core.api.Assertions.assertThat;

class LlmServiceUnitTest {
    private LlmService llmService;
    @BeforeEach
    void setUp() {
        llmService = new LlmService(Mockito.mock(com.theokanning.openai.service.OpenAiService.class));
    }
    @Test
    void testParseResultValidJson() {
        String json = "{\"clientName\":\"A\",\"ticketClassification\":\"payment-related\",\"ticketTimestamp\":\"2024-01-01T00:00:00Z\",\"questionClassification\":\"status-related\"}";
        TicketAnalysisResult result = llmService.parseResult(json);
        assertThat(result.getClientName()).isEqualTo("A");
        assertThat(result.getTicketClassification()).isEqualTo("payment-related");
        assertThat(result.getTicketTimestamp()).isNotNull();
        assertThat(result.getQuestionClassification()).isEqualTo("status-related");
    }
    @Test
    void testParseResultCodeBlock() {
        String json = "```json\n{\"clientName\":\"B\",\"ticketClassification\":\"other\",\"ticketTimestamp\":\"2024-01-01T00:00:00Z\",\"questionClassification\":\"tat-related\"}\n```";
        TicketAnalysisResult result = llmService.parseResult(json);
        assertThat(result.getClientName()).isEqualTo("B");
        assertThat(result.getTicketClassification()).isEqualTo("other");
    }
    @Test
    void testParseResultExtraText() {
        String json = "Here is your result:\n{\"clientName\":\"C\",\"ticketClassification\":\"payment-related\",\"ticketTimestamp\":\"2024-01-01T00:00:00Z\",\"questionClassification\":\"status-related\"}\nThank you!";
        TicketAnalysisResult result = llmService.parseResult(json);
        assertThat(result.getClientName()).isEqualTo("C");
    }
    @Test
    void testParseResultMalformedJson() {
        String json = "not a json";
        TicketAnalysisResult result = llmService.parseResult(json);
        assertThat(result.getTicketClassification()).isEqualTo("unknown");
        assertThat(result.getQuestionClassification()).isEqualTo("unknown");
    }
    @Test
    void testParseResultMissingFields() {
        String json = "{}";
        TicketAnalysisResult result = llmService.parseResult(json);
        assertThat(result.getClientName()).isNull();
        assertThat(result.getTicketClassification()).isNull();
        assertThat(result.getTicketTimestamp()).isNull();
        assertThat(result.getQuestionClassification()).isNull();
    }
    @Test
    void testParseResultNullOrEmpty() {
        TicketAnalysisResult result1 = llmService.parseResult(null);
        TicketAnalysisResult result2 = llmService.parseResult("");
        assertThat(result1.getTicketClassification()).isEqualTo("unknown");
        assertThat(result2.getTicketClassification()).isEqualTo("unknown");
    }
} 