package ai.supporter.worker.llm;

import ai.supporter.worker.payment.PaymentService;
import ai.supporter.worker.payment.PaymentTransaction;
import ai.supporter.worker.ticket.SupportTicket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TicketResponseServiceTest {
    private LlmService llmService;
    private PaymentService paymentService;
    private TicketResponseService ticketResponseService;
    private SupportTicket ticket;
    private TicketAnalysisResult analysis;

    @BeforeEach
    void setUp() {
        llmService = Mockito.mock(LlmService.class);
        paymentService = Mockito.mock(PaymentService.class);
        ticketResponseService = new TicketResponseService(llmService, paymentService);
        ticket = new SupportTicket();
        ticket.setTicketId("T1");
        ticket.setTicketTimestamp(Instant.now());
        ticket.setStatus(SupportTicket.Status.OPEN);
        ticket.setDescription("Why is my payment delayed?");
        ticket.setCustomerName("Customer 001");
        ticket.setLastUpdatedAt(Instant.now());
        analysis = new TicketAnalysisResult();
        analysis.setClientName("Customer 001");
        analysis.setTicketClassification("payment-related");
        analysis.setQuestionClassification("status-related");
        analysis.setTicketTimestamp(ticket.getTicketTimestamp());
    }

    @Test
    void testResponseExcludesPaidTransactions() {
        PaymentTransaction paid = new PaymentTransaction();
        paid.setTransactionId("TX1");
        paid.setCustomerName("Customer 001");
        paid.setStatus(PaymentTransaction.Status.PAID);
        paid.setExpectedTat("2 days");
        paid.setTransactionTimestamp(Instant.now());
        PaymentTransaction failed = new PaymentTransaction();
        failed.setTransactionId("TX2");
        failed.setCustomerName("Customer 001");
        failed.setStatus(PaymentTransaction.Status.FAILED);
        failed.setExpectedTat("2 days");
        failed.setTransactionTimestamp(Instant.now());
        when(paymentService.getAllTransactions()).thenReturn(List.of(paid, failed));
        when(llmService.simpleCompletion(Mockito.anyString())).thenReturn("Transaction failed. Please try again.");
        TicketResponse response = ticketResponseService.generateResponse(analysis, ticket);
        assertThat(response).isNotNull();
        assertThat(response.getComments()).hasSize(1);
        assertThat(response.getResponseComment()).contains("Transaction TX2:");
        assertThat(response.getResponseComment()).doesNotContain("Transaction TX1:");
    }

    @Test
    void testResponseFormatting() {
        PaymentTransaction tx = new PaymentTransaction();
        tx.setTransactionId("TX3");
        tx.setCustomerName("Customer 001");
        tx.setStatus(PaymentTransaction.Status.FAILED);
        tx.setExpectedTat("2 days");
        tx.setTransactionTimestamp(Instant.now());
        when(paymentService.getAllTransactions()).thenReturn(List.of(tx));
        when(llmService.simpleCompletion(Mockito.anyString())).thenReturn("Transaction failed. Please try again.");
        TicketResponse response = ticketResponseService.generateResponse(analysis, ticket);
        assertThat(response.getResponseComment()).startsWith("Hello! Thanks for reaching out.");
        assertThat(response.getResponseComment()).contains("Transaction TX3:");
        assertThat(response.getResponseComment()).endsWith("Thank you!");
    }

    @Test
    void testNoResponseForNonOpenOrNonPaymentRelated() {
        ticket.setStatus(SupportTicket.Status.CLOSED);
        TicketResponse closed = ticketResponseService.generateResponse(analysis, ticket);
        assertThat(closed).isNull();
        analysis.setTicketClassification("other");
        ticket.setStatus(SupportTicket.Status.OPEN);
        TicketResponse notPayment = ticketResponseService.generateResponse(analysis, ticket);
        assertThat(notPayment).isNull();
        analysis.setTicketClassification("payment-related");
        analysis.setQuestionClassification("other");
        TicketResponse notStatusTat = ticketResponseService.generateResponse(analysis, ticket);
        assertThat(notStatusTat).isNull();
    }
} 