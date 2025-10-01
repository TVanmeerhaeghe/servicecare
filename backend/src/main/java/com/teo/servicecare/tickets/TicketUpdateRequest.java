package com.teo.servicecare.tickets;

public class TicketUpdateRequest {
  private String title;
  private String description;
  private Ticket.TicketPriority priority;
  private Long assigneeUserId;
  private Ticket.TicketStatus status;
  private String waitingReason;

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Ticket.TicketPriority getPriority() { return priority; }
  public void setPriority(Ticket.TicketPriority priority) { this.priority = priority; }
  public Long getAssigneeUserId() { return assigneeUserId; }
  public void setAssigneeUserId(Long assigneeUserId) { this.assigneeUserId = assigneeUserId; }
  public Ticket.TicketStatus getStatus() { return status; }
  public void setStatus(Ticket.TicketStatus status) { this.status = status; }
  public String getWaitingReason() { return waitingReason; }
  public void setWaitingReason(String waitingReason) { this.waitingReason = waitingReason; }
}
