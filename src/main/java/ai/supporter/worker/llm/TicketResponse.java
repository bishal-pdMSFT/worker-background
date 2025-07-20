package ai.supporter.worker.llm;

import java.time.Instant;
import java.util.List;

public class TicketResponse {
    private String ticketId;
    private String responseComment;
    private Instant updatedAt;
    private List<String> comments;

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getResponseComment() { return responseComment; }
    public void setResponseComment(String responseComment) { this.responseComment = responseComment; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<String> getComments() { return comments; }
    public void setComments(List<String> comments) { this.comments = comments; }
} 