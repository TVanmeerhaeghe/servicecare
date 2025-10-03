package com.teo.servicecare.tickets.dto;

import com.teo.servicecare.tickets.Ticket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketCreateRequest {
  @NotNull private Long clientId;
  @NotBlank private String title;
  private String description;
  private Long siteId;
  private Long contractId;
  private Ticket.TicketPriority priority;
  private Long assigneeUserId;

  public Long getClientId() { return clientId; }
  public void setClientId(Long clientId) { this.clientId = clientId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Long getSiteId() { return siteId; }
  public void setSiteId(Long siteId) { this.siteId = siteId; }
  public Long getContractId() { return contractId; }
  public void setContractId(Long contractId) { this.contractId = contractId; }
  public Ticket.TicketPriority getPriority() { return priority; }
  public void setPriority(Ticket.TicketPriority priority) { this.priority = priority; }
  public Long getAssigneeUserId() { return assigneeUserId; }
  public void setAssigneeUserId(Long assigneeUserId) { this.assigneeUserId = assigneeUserId; }
}
