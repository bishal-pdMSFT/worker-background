package ai.supporter.worker.llm;

import java.time.Instant;
import java.util.List;

public class TicketAnalysisResult {
    private String clientName;
    private String ticketClassification; // payment-related or otherwise
    private String questionClassification; // status-related or tat-related
    private Instant ticketTimestamp;

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getTicketClassification() { return ticketClassification; }
    public void setTicketClassification(String ticketClassification) { this.ticketClassification = ticketClassification; }

    public String getQuestionClassification() { return questionClassification; }
    public void setQuestionClassification(String questionClassification) { this.questionClassification = questionClassification; }

    public Instant getTicketTimestamp() { return ticketTimestamp; }
    public void setTicketTimestamp(Instant ticketTimestamp) { this.ticketTimestamp = ticketTimestamp; }
} 