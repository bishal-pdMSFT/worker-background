package ai.supporter.worker.payment;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentTransaction {
  private String transactionId;
  private String customerId;
  private String customerName;
  private BigDecimal sourceAmount;
  private String sourceCurrency;
  private BigDecimal destinationAmount;
  private String destinationCurrency;
  private String paymentMode;
  private String paymentGateway;
  private Status status;
  private String expectedTat;
  private Instant transactionTimestamp;

  public enum Status {
    CREATED,
    VALIDATED,
    COMPLIANCE_CHECKED,
    SENT_TO_GATEWAY,
    PAID,
    FAILED,
    CANCELLED
  }

  // Getters and setters
  public String getTransactionId() { return transactionId; }
  public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

  public String getCustomerId() { return customerId; }
  public void setCustomerId(String customerId) { this.customerId = customerId; }

  public String getCustomerName() { return customerName; }
  public void setCustomerName(String customerName) { this.customerName = customerName; }

  public BigDecimal getSourceAmount() { return sourceAmount; }
  public void setSourceAmount(BigDecimal sourceAmount) { this.sourceAmount = sourceAmount; }

  public String getSourceCurrency() { return sourceCurrency; }
  public void setSourceCurrency(String sourceCurrency) { this.sourceCurrency = sourceCurrency; }

  public BigDecimal getDestinationAmount() { return destinationAmount; }
  public void setDestinationAmount(BigDecimal destinationAmount) { this.destinationAmount = destinationAmount; }

  public String getDestinationCurrency() { return destinationCurrency; }
  public void setDestinationCurrency(String destinationCurrency) { this.destinationCurrency = destinationCurrency; }

  public String getPaymentMode() { return paymentMode; }
  public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

  public String getPaymentGateway() { return paymentGateway; }
  public void setPaymentGateway(String paymentGateway) { this.paymentGateway = paymentGateway; }

  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }

  public String getExpectedTat() { return expectedTat; }
  public void setExpectedTat(String expectedTat) { this.expectedTat = expectedTat; }

  public Instant getTransactionTimestamp() { return transactionTimestamp; }
  public void setTransactionTimestamp(Instant transactionTimestamp) { this.transactionTimestamp = transactionTimestamp; }
} 