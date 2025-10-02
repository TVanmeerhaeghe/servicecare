package com.teo.servicecare.tickets.worklog;

import java.time.LocalDateTime;

public class TicketWorkLogResponse {
  private Long id;
  private Long ticketId;
  private Long userId;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private Integer minutes;
  private String note;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static TicketWorkLogResponse from(TicketWorkLog w) {
    var r = new TicketWorkLogResponse();
    r.id = w.getId();
    r.ticketId = w.getTicketId();
    r.userId = w.getUserId();
    r.startedAt = w.getStartedAt();
    r.endedAt = w.getEndedAt();
    r.minutes = w.getMinutes();
    r.note = w.getNote();
    r.createdAt = w.getCreatedAt();
    r.updatedAt = w.getUpdatedAt();
    return r;
  }

  public Long getId() { return id; }
  public Long getTicketId() { return ticketId; }
  public Long getUserId() { return userId; }
  public LocalDateTime getStartedAt() { return startedAt; }
  public LocalDateTime getEndedAt() { return endedAt; }
  public Integer getMinutes() { return minutes; }
  public String getNote() { return note; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
}
