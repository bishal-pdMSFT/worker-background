package ai.supporter.worker;

import ai.supporter.worker.ticket.TicketService;
import ai.supporter.worker.payment.PaymentService;
import ai.supporter.worker.llm.LlmService;
import org.springframework.stereotype.Service;

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

  // TODO: Add orchestration logic to coordinate the business modules
} 