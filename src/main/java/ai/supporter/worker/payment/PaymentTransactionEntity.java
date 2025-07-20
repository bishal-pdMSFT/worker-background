package ai.supporter.worker.payment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment_transaction")
public class PaymentTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "customer_id")
    private String customerId;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "source_amount")
    private BigDecimal sourceAmount;
    @Column(name = "source_currency")
    private String sourceCurrency;
    @Column(name = "destination_amount")
    private BigDecimal destinationAmount;
    @Column(name = "destination_currency")
    private String destinationCurrency;
    @Column(name = "payment_mode")
    private String paymentMode;
    @Column(name = "payment_gateway")
    private String paymentGateway;
    @Column(name = "status")
    private String status;
    @Column(name = "expected_tat")
    private String expectedTat;
    @Column(name = "transaction_timestamp")
    private Instant transactionTimestamp;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getExpectedTat() { return expectedTat; }
    public void setExpectedTat(String expectedTat) { this.expectedTat = expectedTat; }
    public Instant getTransactionTimestamp() { return transactionTimestamp; }
    public void setTransactionTimestamp(Instant transactionTimestamp) { this.transactionTimestamp = transactionTimestamp; }
} 