package ai.supporter.worker.payment;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileTransactionSource implements TransactionSource {
  private final String filePath;
  private final ResourceLoader resourceLoader;

  public FileTransactionSource(@Value("${payment.file.path}") String filePath, ResourceLoader resourceLoader) {
    this.filePath = filePath;
    this.resourceLoader = resourceLoader;
  }

  @Override
  public List<PaymentTransaction> fetchTransactions() {
    List<PaymentTransaction> transactions = new ArrayList<>();
    try {
      CSVReader reader;
      File file = new File(filePath);
      if (file.exists()) {
        reader = new CSVReader(new FileReader(file));
      } else {
        Resource resource = resourceLoader.getResource("classpath:" + filePath.replace("classpath:", ""));
        reader = new CSVReader(new InputStreamReader(resource.getInputStream()));
      }
      String[] parts;
      reader.readNext(); // header
      while ((parts = reader.readNext()) != null) {
        if (parts.length < 12) continue;
        try {
          PaymentTransaction tx = new PaymentTransaction();
          tx.setTransactionId(parts[0]);
          tx.setCustomerId(parts[1]);
          tx.setCustomerName(parts[2]);
          tx.setSourceAmount(new BigDecimal(parts[3]));
          tx.setSourceCurrency(parts[4]);
          tx.setDestinationAmount(new BigDecimal(parts[5]));
          tx.setDestinationCurrency(parts[6]);
          tx.setPaymentMode(parts[7]);
          tx.setPaymentGateway(parts[8]);
          tx.setStatus(PaymentTransaction.Status.valueOf(parts[9]));
          tx.setExpectedTat(parts[10]);
          tx.setTransactionTimestamp(parseTimestamp(parts[11]));
          transactions.add(tx);
        } catch (Exception e) {
          // skip malformed line
        }
      }
      reader.close();
    } catch (Exception e) {
      throw new RuntimeException("Failed to read transactions from file or classpath: " + filePath, e);
    }
    return transactions;
  }

  private static Instant parseTimestamp(String text) {
    try {
      return Instant.parse(text);
    } catch (Exception ignored) {}
    try {
      return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
          .atZone(ZoneId.systemDefault()).toInstant();
    } catch (Exception ignored) {}
    try {
      int dot = text.indexOf('.');
      if (dot > 0) {
        int z = text.indexOf('Z', dot);
        String ms = text.substring(0, dot+4) + (z > 0 ? text.substring(z) : "");
        return Instant.parse(ms);
      }
    } catch (Exception ignored) {}
    throw new RuntimeException("Unparseable timestamp: " + text);
  }
} 