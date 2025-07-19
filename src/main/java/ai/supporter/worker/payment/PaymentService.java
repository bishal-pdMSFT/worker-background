package ai.supporter.worker.payment;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {
  private final TransactionSource transactionSource;

  public PaymentService(TransactionSource transactionSource) {
    this.transactionSource = transactionSource;
  }

  public List<PaymentTransaction> getAllTransactions() {
    return transactionSource.fetchTransactions();
  }
} 