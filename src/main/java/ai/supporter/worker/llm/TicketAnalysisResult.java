package ai.supporter.worker.llm;

import java.time.Instant;
import java.util.List;

public class TicketAnalysisResult {
    private String clientName;
    private String ticketClassification; // payment-related or otherwise
    private List<Instant> transactionTimestamps;
    private String questionClassification; // status-related or tat-related

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getTicketClassification() { return ticketClassification; }
    public void setTicketClassification(String ticketClassification) { this.ticketClassification = ticketClassification; }

    public List<Instant> getTransactionTimestamps() { return transactionTimestamps; }
    public void setTransactionTimestamps(List<Instant> transactionTimestamps) { this.transactionTimestamps = transactionTimestamps; }

    public String getQuestionClassification() { return questionClassification; }
    public void setQuestionClassification(String questionClassification) { this.questionClassification = questionClassification; }
} 