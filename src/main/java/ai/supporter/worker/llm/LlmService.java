package ai.supporter.worker.llm;

import ai.supporter.worker.payment.PaymentTransaction;
import ai.supporter.worker.ticket.SupportTicket;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LlmService {
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public LlmService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public TicketAnalysisResult analyzeTicket(SupportTicket ticket, List<PaymentTransaction> transactions) {
        // Find transactions within 48h of ticket creation
        Instant ticketTime = ticket.getTicketTimestamp();
        List<PaymentTransaction> relevantTx = transactions.stream()
                .filter(tx -> {
                    Instant txTime = tx.getTransactionTimestamp();
                    return !txTime.isBefore(ticketTime.minusSeconds(48 * 3600)) && !txTime.isAfter(ticketTime.plusSeconds(48 * 3600));
                })
                .collect(Collectors.toList());
        // Improved prompt for GPT-4o (no transaction details)
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert support ticket classifier.\n");
        prompt.append("Given the following support ticket, extract the required fields. Always fill all fields. If information is missing, infer or use 'unknown'. Respond ONLY with a JSON object, and nothing else.\n");
        prompt.append("\nSupport ticket description: \"").append(ticket.getDescription()).append("\"\n");
        prompt.append("Customer name: ").append(ticket.getCustomerName()).append("\n");
        prompt.append("Ticket created at: ").append(ticket.getTicketTimestamp()).append("\n");
        prompt.append("\nClassify the ticket as 'payment-related' or 'other'.\n");
        prompt.append("Classify the question as 'status-related' or 'tat-related' or 'other'.\n");
        prompt.append("\nRespond with a JSON object with the following fields:\n");
        prompt.append("- clientName: string (the customer name, or 'unknown')\n");
        prompt.append("- ticketClassification: string ('payment-related' or 'other')\n");
        prompt.append("- ticketTimestamp: string (ISO8601 timestamp of when the ticket was created)\n");
        prompt.append("- questionClassification: string ('status-related' or 'tat-related' or 'other')\n");
        prompt.append("\nExample:\n");
        prompt.append("{\n");
        prompt.append("  \"clientName\": \"Acme Corp\",\n");
        prompt.append("  \"ticketClassification\": \"payment-related\",\n");
        prompt.append("  \"ticketTimestamp\": \"2024-06-01T12:00:00Z\",\n");
        prompt.append("  \"questionClassification\": \"status-related\"\n");
        prompt.append("}\n");
        prompt.append("\nReturn only the JSON object, with no extra text.\n");

        ChatMessage system = new ChatMessage("system", "You are a helpful support ticket classifier.");
        ChatMessage user = new ChatMessage("user", prompt.toString());
        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .model("gpt-4o")
                .messages(List.of(system, user))
                .maxTokens(256)
                .temperature(0.0)
                .build();
        String json = openAiService.createChatCompletion(req)
                .getChoices().get(0).getMessage().getContent();
        // Parse JSON using Jackson
        return parseResult(json);
    }

    private TicketAnalysisResult parseResult(String json) {
        System.out.println("json: " + json);
        TicketAnalysisResult result = new TicketAnalysisResult();
        try {
            // Remove code block markers and extract JSON object
            String cleaned = json.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3).trim();
                if (cleaned.startsWith("json")) {
                    cleaned = cleaned.substring(4).trim();
                }
                int end = cleaned.lastIndexOf("```\n");
                if (end == -1) end = cleaned.lastIndexOf("```");
                if (end > 0) cleaned = cleaned.substring(0, end).trim();
            }
            // Extract first JSON object if extra text is present
            int startObj = cleaned.indexOf('{');
            int endObj = cleaned.lastIndexOf('}');
            if (startObj >= 0 && endObj > startObj) {
                cleaned = cleaned.substring(startObj, endObj + 1);
            }
            JsonNode node = objectMapper.readTree(cleaned);
            result.setClientName(node.path("clientName").asText(null));
            result.setTicketClassification(node.path("ticketClassification").asText(null));
            if (node.has("ticketTimestamp")) {
                try { result.setTicketTimestamp(Instant.parse(node.get("ticketTimestamp").asText())); } catch (Exception ignored) {}
            }
            result.setQuestionClassification(node.path("questionClassification").asText(null));
        } catch (Exception e) {
            System.err.println("llm exception: " + e.getMessage());
            result.setTicketClassification("unknown");
            result.setQuestionClassification("unknown");
        }
        return result;
    }
} 