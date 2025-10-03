package com.teo.servicecare.tickets.dto;

import com.teo.servicecare.tickets.Ticket;
import java.time.LocalDateTime;

public class TicketResponse {
  public Long id;
  public Long clientId;
  public Long siteId;
  public Long contractId;
  public String title;
  public String description;
  public Ticket.TicketPriority priority;
  public Ticket.TicketStatus status;
  public String waitingReason;
  public Long assigneeUserId;
  public LocalDateTime respondBy;
  public LocalDateTime resolveBy;
  public LocalDateTime respondedAt;
  public LocalDateTime resolvedAt;
  public boolean slaBreached;
  public int pausedSeconds;

  public static TicketResponse from(Ticket t) {
    var r = new TicketResponse();
    r.id = t.getId();
    r.clientId = t.getClientId();
    r.siteId = t.getSiteId();
    r.contractId = t.getContractId();
    r.title = t.getTitle();
    r.description = t.getDescription();
    r.priority = t.getPriority();
    r.status = t.getStatus();
    r.waitingReason = t.getWaitingReason();
    r.assigneeUserId = t.getAssigneeUserId();
    r.respondBy = t.getRespondBy();
    r.resolveBy = t.getResolveBy();
    r.respondedAt = t.getRespondedAt();
    r.resolvedAt = t.getResolvedAt();
    r.slaBreached = t.isSlaBreached();
    r.pausedSeconds = t.getPausedSeconds();
    return r;
  }
}
