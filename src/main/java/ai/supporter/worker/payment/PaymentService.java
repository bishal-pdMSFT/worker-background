package ai.supporter.worker.payment;

import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PaymentService {
  private final TransactionSource transactionSource;
  private final PaymentTransactionRepository repository;

  @Autowired
  public PaymentService(TransactionSource transactionSource, PaymentTransactionRepository repository) {
    this.transactionSource = transactionSource;
    this.repository = repository;
  }

  public List<PaymentTransaction> getAllTransactions() {
    return transactionSource.fetchTransactions();
  }

  public void saveTransaction(PaymentTransaction tx) {
    PaymentTransactionEntity entity = new PaymentTransactionEntity();
    entity.setTransactionId(tx.getTransactionId());
    entity.setCustomerId(tx.getCustomerId());
    entity.setCustomerName(tx.getCustomerName());
    entity.setSourceAmount(tx.getSourceAmount());
    entity.setSourceCurrency(tx.getSourceCurrency());
    entity.setDestinationAmount(tx.getDestinationAmount());
    entity.setDestinationCurrency(tx.getDestinationCurrency());
    entity.setPaymentMode(tx.getPaymentMode());
    entity.setPaymentGateway(tx.getPaymentGateway());
    entity.setStatus(tx.getStatus().name());
    entity.setExpectedTat(tx.getExpectedTat());
    entity.setTransactionTimestamp(tx.getTransactionTimestamp());
    repository.save(entity);
  }
} 