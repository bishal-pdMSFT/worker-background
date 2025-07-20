package ai.supporter.worker;

import ai.supporter.worker.llm.LlmService;
import ai.supporter.worker.llm.TicketAnalysisResult;
import ai.supporter.worker.payment.PaymentService;
import ai.supporter.worker.payment.PaymentTransaction;
import ai.supporter.worker.ticket.SupportTicket;
import ai.supporter.worker.ticket.TicketService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BusinessOrchestrator {
    private final TicketService ticketService;
    private final PaymentService paymentService;
    private final LlmService llmService;

    public BusinessOrchestrator(TicketService ticketService, PaymentService paymentService, LlmService llmService) {
        this.ticketService = ticketService;
        this.paymentService = paymentService;
        this.llmService = llmService;
    }

    public void runOrchestration() {
        List<SupportTicket> openTickets = ticketService.getAllTickets().stream()
                .filter(t -> t.getStatus() == SupportTicket.Status.OPEN)
                .collect(Collectors.toList());
        Collections.shuffle(openTickets, new Random());
        List<SupportTicket> sample = openTickets.stream().limit(10).collect(Collectors.toList());
        List<PaymentTransaction> transactions = paymentService.getAllTransactions();
        System.out.println("\n--- LLM Analysis for 10 Random Open Tickets ---\n");
        for (SupportTicket ticket : sample) {
            TicketAnalysisResult result = llmService.analyzeTicket(ticket, transactions);
            System.out.println(prettyPrintResult(ticket, result));
        }
    }

    private String prettyPrintResult(SupportTicket ticket, TicketAnalysisResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket ID: ").append(ticket.getTicketId()).append("\n");
        sb.append("Customer: ").append(result.getClientName()).append("\n");
        sb.append("Description: ").append(ticket.getDescription()).append("\n");
        sb.append("Ticket Classification: ").append(result.getTicketClassification()).append("\n");
        sb.append("Question Classification: ").append(result.getQuestionClassification()).append("\n");
        sb.append("Relevant Transaction Timestamps: ");
        if (result.getTransactionTimestamps() != null && !result.getTransactionTimestamps().isEmpty()) {
            sb.append(result.getTransactionTimestamps()).append("\n");
        } else {
            sb.append("None\n");
        }
        sb.append("-----------------------------\n");
        return sb.toString();
    }
} 