package ai.supporter.worker.ticket;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileTicketSource implements TicketSource {
  private final String filePath;
  private final ResourceLoader resourceLoader;

  public FileTicketSource(@Value("${ticket.file.path}") String filePath, ResourceLoader resourceLoader) {
    this.filePath = filePath;
    this.resourceLoader = resourceLoader;
  }

  @Override
  public List<SupportTicket> fetchTickets() {
    List<SupportTicket> tickets = new ArrayList<>();
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
        if (parts.length < 6) continue;
        try {
          SupportTicket ticket = new SupportTicket();
          ticket.setTicketId(parts[0]);
          ticket.setTicketTimestamp(parseTimestamp(parts[1]));
          ticket.setStatus(SupportTicket.Status.valueOf(parts[2]));
          ticket.setDescription(parts[3]);
          ticket.setCustomerName(parts[4]);
          ticket.setLastUpdatedAt(parseTimestamp(parts[5]));
          tickets.add(ticket);
          System.out.println("Added ticket: " + ticket.getTicketId());
        } catch (Exception e) {
          System.out.println("Error parsing ticket: " + e.getMessage());
        }
      }
      reader.close();
    } catch (Exception e) {
      throw new RuntimeException("Failed to read tickets from file or classpath: " + filePath, e);
    }
    return tickets;
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