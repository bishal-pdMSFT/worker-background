package ai.supporter.worker;

import ai.supporter.worker.llm.LlmService;
import ai.supporter.worker.llm.TicketAnalysisResult;
import ai.supporter.worker.llm.TicketResponseService;
import ai.supporter.worker.llm.TicketResponse;
import ai.supporter.worker.payment.PaymentService;
import ai.supporter.worker.payment.PaymentTransaction;
import ai.supporter.worker.ticket.SupportTicket;
import ai.supporter.worker.ticket.TicketService;
import ai.supporter.worker.queue.TicketMessage;
import ai.supporter.worker.queue.AnalyzedTicketMessage;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BusinessOrchestrator {
    private final TicketService ticketService;
    private final PaymentService paymentService;
    private final LlmService llmService;
    private final TicketResponseService ticketResponseService;

    public BusinessOrchestrator(TicketService ticketService, PaymentService paymentService, LlmService llmService, TicketResponseService ticketResponseService) {
        this.ticketService = ticketService;
        this.paymentService = paymentService;
        this.llmService = llmService;
        this.ticketResponseService = ticketResponseService;
    }

    public void runOrchestration() {
        LinkedBlockingQueue<TicketMessage> incomingTicketsQueue = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<AnalyzedTicketMessage> analyzedTicketsQueue = new LinkedBlockingQueue<>();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // Producer: reads open tickets and puts on queue
        executor.submit(() -> {
            List<SupportTicket> openTickets = ticketService.getAllTickets().stream()
                    .filter(t -> t.getStatus() == SupportTicket.Status.OPEN)
                    .collect(Collectors.toList());
            for (SupportTicket ticket : openTickets) {
                try { incomingTicketsQueue.put(new TicketMessage(ticket)); } catch (InterruptedException ignored) {}
            }
        });
        // Analyzer: reads from incomingTicketsQueue, analyzes, puts on analyzedTicketsQueue
        executor.submit(() -> {
            while (true) {
                try {
                    TicketMessage msg = incomingTicketsQueue.take();
                    TicketAnalysisResult result = llmService.analyzeTicket(msg.getTicket());
                    analyzedTicketsQueue.put(new AnalyzedTicketMessage(msg.getTicket(), result));
                } catch (InterruptedException e) { break; }
            }
        });
        // Responder: reads from analyzedTicketsQueue, generates response, prints
        executor.submit(() -> {
            while (true) {
                try {
                    AnalyzedTicketMessage msg = analyzedTicketsQueue.take();
                    System.out.println(prettyPrintResult(msg.getTicket(), msg.getAnalysis()));
                    TicketResponse response = ticketResponseService.generateResponse(msg.getAnalysis(), msg.getTicket());
                    if (response != null) {
                        System.out.println(prettyPrintTicketResponse(response));
                    }
                } catch (InterruptedException e) { break; }
            }
        });
        // For demo: let threads run for a while then shutdown
        try { Thread.sleep(50000); } catch (InterruptedException ignored) {}
        executor.shutdownNow();
    }

    private String prettyPrintResult(SupportTicket ticket, TicketAnalysisResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket ID: ").append(ticket.getTicketId()).append("\n");
        sb.append("Customer: ").append(result.getClientName()).append("\n");
        sb.append("Description: ").append(ticket.getDescription()).append("\n");
        sb.append("Ticket Classification: ").append(result.getTicketClassification()).append("\n");
        sb.append("Question Classification: ").append(result.getQuestionClassification()).append("\n");
        sb.append("Ticket Timestamp: ");
        if (result.getTicketTimestamp() != null) {
            sb.append(result.getTicketTimestamp()).append("\n");
        } else {
            sb.append("None\n");
        }
        sb.append("-----------------------------\n");
        return sb.toString();
    }

    private String prettyPrintTicketResponse(TicketResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ticket Response for Ticket ID: ").append(response.getTicketId()).append("\n");
        sb.append("Updated At: ").append(response.getUpdatedAt()).append("\n");
        sb.append("Response Comment: ").append(response.getResponseComment()).append("\n");
        sb.append("All Comments: ");
        if (response.getComments() != null && !response.getComments().isEmpty()) {
            sb.append(String.join(" | ", response.getComments())).append("\n");
        } else {
            sb.append("None\n");
        }
        sb.append("============================\n");
        return sb.toString();
    }
} 