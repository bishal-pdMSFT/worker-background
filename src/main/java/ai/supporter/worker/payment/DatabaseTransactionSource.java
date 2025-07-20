package ai.supporter.worker.payment;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DatabaseTransactionSource implements TransactionSource {
    private final PaymentTransactionRepository repository;

    public DatabaseTransactionSource(PaymentTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PaymentTransaction> fetchTransactions() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private PaymentTransaction toDto(PaymentTransactionEntity entity) {
        PaymentTransaction tx = new PaymentTransaction();
        tx.setTransactionId(entity.getTransactionId());
        tx.setCustomerId(entity.getCustomerId());
        tx.setCustomerName(entity.getCustomerName());
        tx.setSourceAmount(entity.getSourceAmount());
        tx.setSourceCurrency(entity.getSourceCurrency());
        tx.setDestinationAmount(entity.getDestinationAmount());
        tx.setDestinationCurrency(entity.getDestinationCurrency());
        tx.setPaymentMode(entity.getPaymentMode());
        tx.setPaymentGateway(entity.getPaymentGateway());
        tx.setStatus(PaymentTransaction.Status.valueOf(entity.getStatus()));
        tx.setExpectedTat(entity.getExpectedTat());
        tx.setTransactionTimestamp(entity.getTransactionTimestamp());
        return tx;
    }
} 