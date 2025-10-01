package com.teo.servicecare.tickets;

import java.time.LocalDateTime;

public class TicketResponse {
  private Long id;
  private Long clientId;
  private Long siteId;
  private Long contractId;
  private String title;
  private String description;
  private Ticket.TicketPriority priority;
  private Ticket.TicketStatus status;
  private String waitingReason;
  private Long assigneeUserId;
  private LocalDateTime respondBy;
  private LocalDateTime resolveBy;
  private LocalDateTime respondedAt;
  private LocalDateTime resolvedAt;
  private boolean slaBreached;
  private int pausedSeconds;

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

  public Long getId() { return id; }
  public Long getClientId() { return clientId; }
  public Long getSiteId() { return siteId; }
  public Long getContractId() { return contractId; }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public Ticket.TicketPriority getPriority() { return priority; }
  public Ticket.TicketStatus getStatus() { return status; }
  public String getWaitingReason() { return waitingReason; }
  public Long getAssigneeUserId() { return assigneeUserId; }
  public LocalDateTime getRespondBy() { return respondBy; }
  public LocalDateTime getResolveBy() { return resolveBy; }
  public LocalDateTime getRespondedAt() { return respondedAt; }
  public LocalDateTime getResolvedAt() { return resolvedAt; }
  public boolean isSlaBreached() { return slaBreached; }
  public int getPausedSeconds() { return pausedSeconds; }
}
