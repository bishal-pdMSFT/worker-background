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
        // Prepare prompt for GPT-4.1
        StringBuilder prompt = new StringBuilder();
        prompt.append("Support ticket description: \"").append(ticket.getDescription()).append("\"\n");
        prompt.append("Customer name: ").append(ticket.getCustomerName()).append("\n");
        prompt.append("Relevant transactions (timestamp, status):\n");
        for (PaymentTransaction tx : relevantTx) {
            prompt.append("- ").append(tx.getTransactionTimestamp()).append(", ").append(tx.getStatus()).append("\n");
        }
        prompt.append("\nClassify the ticket as 'payment-related' or 'other'.\n");
        prompt.append("Classify the question as 'status-related' or 'tat-related'.\n");
        prompt.append("Respond in JSON with fields: clientName, ticketClassification, transactionTimestamps (array), questionClassification.\n");

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
        TicketAnalysisResult result = new TicketAnalysisResult();
        try {
            JsonNode node = objectMapper.readTree(json);
            result.setClientName(node.path("clientName").asText(null));
            result.setTicketClassification(node.path("ticketClassification").asText(null));
            result.setQuestionClassification(node.path("questionClassification").asText(null));
            List<Instant> timestamps = new ArrayList<>();
            if (node.has("transactionTimestamps") && node.get("transactionTimestamps").isArray()) {
                for (JsonNode ts : node.get("transactionTimestamps")) {
                    try { timestamps.add(Instant.parse(ts.asText())); } catch (Exception ignored) {}
                }
            }
            result.setTransactionTimestamps(timestamps);
        } catch (Exception e) {
            result.setTicketClassification("unknown");
            result.setQuestionClassification("unknown");
        }
        return result;
    }
} 