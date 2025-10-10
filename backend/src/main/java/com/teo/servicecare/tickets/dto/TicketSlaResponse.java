package com.teo.servicecare.tickets.dto;

import com.teo.servicecare.contracts.Contract;
import com.teo.servicecare.tickets.Ticket;
import java.time.LocalDateTime;

public class TicketSlaResponse {
    public Long ticketId;
    public Long contractId;
    public Ticket.TicketPriority priority;
    public String timezone;
    public Contract.SupportDays supportDays;
    public Contract.MeasureWindow measureWindow;
    public boolean pauseOnWaiting;

    public LocalDateTime responseDueAt;
    public LocalDateTime resolutionDueAt;
    public LocalDateTime responseMetAt;
    public LocalDateTime resolutionMetAt;

    public boolean paused;
    public long secondsToResponseDeadline;
    public long secondsToResolutionDeadline;
    public boolean responseBreached;
    public boolean resolutionBreached;
}