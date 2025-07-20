package ai.supporter.worker.queue;

import ai.supporter.worker.ticket.SupportTicket;

public class TicketMessage {
    private final SupportTicket ticket;
    public TicketMessage(SupportTicket ticket) { this.ticket = ticket; }
    public SupportTicket getTicket() { return ticket; }
} 