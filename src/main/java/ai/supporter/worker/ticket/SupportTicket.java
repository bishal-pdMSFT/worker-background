package ai.supporter.worker.ticket;

import java.time.Instant;

public class SupportTicket {
  private String ticketId;
  private Instant ticketTimestamp;
  private Status status;
  private String description;
  private String customerName;
  private Instant lastUpdatedAt;

  public enum Status {
    OPEN,
    IN_PROGRESS,
    WAITING_FOR_CUSTOMER,
    WAITING_FOR_PAYMENT,
    RESOLVED,
    CLOSED,
    REJECTED
  }

  // Getters and setters
  public String getTicketId() { return ticketId; }
  public void setTicketId(String ticketId) { this.ticketId = ticketId; }

  public Instant getTicketTimestamp() { return ticketTimestamp; }
  public void setTicketTimestamp(Instant ticketTimestamp) { this.ticketTimestamp = ticketTimestamp; }

  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public String getCustomerName() { return customerName; }
  public void setCustomerName(String customerName) { this.customerName = customerName; }

  public Instant getLastUpdatedAt() { return lastUpdatedAt; }
  public void setLastUpdatedAt(Instant lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
} 