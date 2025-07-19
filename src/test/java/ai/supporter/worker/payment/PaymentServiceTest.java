package ai.supporter.worker.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentServiceTest {
  @Autowired
  private PaymentService paymentService;

  @Test
  void testReadTransactionsFromCsv() {
    List<PaymentTransaction> transactions = paymentService.getAllTransactions();
    assertThat(transactions).isNotNull();
    assertThat(transactions.size()).isGreaterThan(0);
    // Print a few transactions for demonstration
    transactions.stream().limit(3).forEach(tx ->
      System.out.println(tx.getTransactionId() + ": " + tx.getStatus() + ", " + tx.getCustomerName())
    );
  }
} 