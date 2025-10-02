package com.teo.servicecare.tickets.worklog;

import java.time.LocalDateTime;

public class TicketWorkLogUpdateRequest {
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private Integer minutes;
  private String note;

  public LocalDateTime getStartedAt() { return startedAt; }
  public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
  public LocalDateTime getEndedAt() { return endedAt; }
  public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
  public Integer getMinutes() { return minutes; }
  public void setMinutes(Integer minutes) { this.minutes = minutes; }
  public String getNote() { return note; }
  public void setNote(String note) { this.note = note; }
}
