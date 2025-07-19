package ai.supporter.worker.llm;

import ai.supporter.worker.payment.PaymentTransaction;
import ai.supporter.worker.ticket.SupportTicket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {"openai.api.key=dummy-key"})
class LlmServiceTest {
    @MockBean
    private com.theokanning.openai.service.OpenAiService openAiService;

    @Autowired
    private LlmService llmService;

    private SupportTicket ticket;
    private List<PaymentTransaction> transactions;

    @BeforeEach
    void setUp() {
        ticket = new SupportTicket();
        ticket.setTicketId("T1");
        ticket.setTicketTimestamp(Instant.now());
        ticket.setStatus(SupportTicket.Status.OPEN);
        ticket.setDescription("What is the status of my payment?");
        ticket.setCustomerName("Customer 001");
        ticket.setLastUpdatedAt(Instant.now());

        PaymentTransaction tx = new PaymentTransaction();
        tx.setTransactionId("TX1");
        tx.setCustomerId("cust001");
        tx.setCustomerName("Customer 001");
        tx.setSourceAmount(java.math.BigDecimal.valueOf(100));
        tx.setSourceCurrency("USD");
        tx.setDestinationAmount(java.math.BigDecimal.valueOf(99));
        tx.setDestinationCurrency("USD");
        tx.setPaymentMode("CARD");
        tx.setPaymentGateway("Stripe");
        tx.setStatus(PaymentTransaction.Status.PAID);
        tx.setExpectedTat("2 days");
        tx.setTransactionTimestamp(Instant.now());
        transactions = List.of(tx);
    }

    @Test
    void testAnalyzeTicketWithMockedLlm() {
        // Mock the OpenAI response
        com.theokanning.openai.completion.chat.ChatCompletionChoice choice = new com.theokanning.openai.completion.chat.ChatCompletionChoice();
        com.theokanning.openai.completion.chat.ChatMessage message = new com.theokanning.openai.completion.chat.ChatMessage();
        message.setContent("{\"clientName\":\"Customer 001\",\"ticketClassification\":\"payment-related\",\"transactionTimestamps\":[\"2025-07-19T15:14:45.318Z\"],\"questionClassification\":\"status-related\"}");
        choice.setMessage(message);
        com.theokanning.openai.completion.chat.ChatCompletionResult result = new com.theokanning.openai.completion.chat.ChatCompletionResult();
        result.setChoices(List.of(choice));
        when(openAiService.createChatCompletion(any())).thenReturn(result);

        TicketAnalysisResult analysis = llmService.analyzeTicket(ticket, transactions);
        assertThat(analysis.getClientName()).isEqualTo("Customer 001");
        assertThat(analysis.getTicketClassification()).isEqualTo("payment-related");
        assertThat(analysis.getTransactionTimestamps()).isNotEmpty();
        assertThat(analysis.getQuestionClassification()).isEqualTo("status-related");
    }
} 