package ai.supporter.worker.llm;

import ai.supporter.worker.payment.PaymentService;
import ai.supporter.worker.payment.PaymentTransaction;
import ai.supporter.worker.ticket.SupportTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketResponseService {
    private final LlmService llmService;
    private final PaymentService paymentService;

    @Autowired
    public TicketResponseService(LlmService llmService, PaymentService paymentService) {
        this.llmService = llmService;
        this.paymentService = paymentService;
    }

    public TicketResponse generateResponse(TicketAnalysisResult analysis, SupportTicket ticket) {
        if (ticket.getStatus() != SupportTicket.Status.OPEN) {
            return null;
        }
        if (!"payment-related".equalsIgnoreCase(analysis.getTicketClassification())) {
            return null;
        }
        String qc = analysis.getQuestionClassification();
        if (!"status-related".equalsIgnoreCase(qc) && !"tat-related".equalsIgnoreCase(qc)) {
            return null;
        }
        Instant ticketTime = ticket.getTicketTimestamp();
        String clientName = analysis.getClientName();
        // Find relevant transactions for the same client in last 48h
        List<PaymentTransaction> relevantTx = new ArrayList<>();
        try {
            relevantTx = paymentService.getAllTransactions().stream()
                .filter(tx -> tx.getCustomerName() != null && tx.getCustomerName().equalsIgnoreCase(clientName))
                .filter(tx -> {
                    Instant txTime = tx.getTransactionTimestamp();
                    return !txTime.isBefore(ticketTime.minusSeconds(48 * 3600)) && !txTime.isAfter(ticketTime.plusSeconds(48 * 3600));
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> comments = new ArrayList<>();
        for (PaymentTransaction tx : relevantTx) {
            if (tx.getStatus() == PaymentTransaction.Status.PAID) {
                continue;
            }
            String comment = generateTransactionComment(tx, ticket, true);
            comments.add(comment);
        }
        // Build formatted response
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("Hello! Thanks for reaching out.\n");
        for (String c : comments) {
            responseBuilder.append(c.trim()).append("\n");
        }
        responseBuilder.append("If you have any more questions or need further assistance, feel free to reach out. Thank you!\n");
        TicketResponse response = new TicketResponse();
        response.setTicketId(ticket.getTicketId());
        response.setComments(comments);
        response.setUpdatedAt(Instant.now());
        response.setResponseComment(comments.isEmpty() ? "No recent transactions found." : responseBuilder.toString().trim());
        // Mark ticket as closed (if you want to update the ticket, do it here)
        ticket.setStatus(SupportTicket.Status.CLOSED);
        return response;
    }

    private String generateTransactionComment(PaymentTransaction tx, SupportTicket ticket, boolean includeTxId) {
        // Use LLM to generate a concise response based on transaction status and tat
        String prompt = String.format(
                "Given the following payment transaction and support ticket, generate a concise to-the-point response for the customer without adding any niceties.\n" +
                "Transaction id: %s\nTransaction status: %s\nExpected TAT: %s\nTicket description: %s\nRespond with only the comment, do not include any follow-up instructions.",
                tx.getTransactionId(), tx.getStatus(), tx.getExpectedTat(), ticket.getDescription()
        );
        String comment = llmService.simpleCompletion(prompt);
        if (includeTxId) {
            return String.format("Transaction %s: %s", tx.getTransactionId(), comment != null ? comment : "Unable to generate response.");
        } else {
            return comment != null ? comment : "Unable to generate response.";
        }
    }
} 