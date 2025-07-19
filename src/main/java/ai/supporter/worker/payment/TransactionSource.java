package ai.supporter.worker.payment;

import java.util.List;

public interface TransactionSource {
  List<PaymentTransaction> fetchTransactions();
} 