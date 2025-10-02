package com.teo.servicecare.tickets.ticketcomment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketCommentCreateRequest {
  @NotNull private Long ticketId;
  @NotBlank private String body;

  private Boolean internalOnly;

  public Long getTicketId() { return ticketId; }
  public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
  public String getBody() { return body; }
  public void setBody(String body) { this.body = body; }
  public Boolean getInternalOnly() { return internalOnly; }
  public void setInternalOnly(Boolean internalOnly) { this.internalOnly = internalOnly; }
}
