package ai.supporter.worker.queue;

import ai.supporter.worker.ticket.SupportTicket;
import ai.supporter.worker.llm.TicketAnalysisResult;

public class AnalyzedTicketMessage {
    private final SupportTicket ticket;
    private final TicketAnalysisResult analysis;
    public AnalyzedTicketMessage(SupportTicket ticket, TicketAnalysisResult analysis) {
        this.ticket = ticket;
        this.analysis = analysis;
    }
    public SupportTicket getTicket() { return ticket; }
    public TicketAnalysisResult getAnalysis() { return analysis; }
} 