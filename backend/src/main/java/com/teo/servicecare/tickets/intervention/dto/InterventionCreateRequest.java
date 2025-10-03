package com.teo.servicecare.tickets.intervention.dto;

import com.teo.servicecare.tickets.intervention.Intervention;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class InterventionCreateRequest {
  @NotNull private Long ticketId;
  private Intervention.Type type;
  private Long technicianUserId;
  private String title;
  private String notes;

  private LocalDateTime scheduledStart;
  private LocalDateTime scheduledEnd;

  public Long getTicketId() { return ticketId; }
  public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
  public Intervention.Type getType() { return type; }
  public void setType(Intervention.Type type) { this.type = type; }
  public Long getTechnicianUserId() { return technicianUserId; }
  public void setTechnicianUserId(Long technicianUserId) { this.technicianUserId = technicianUserId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getNotes() { return notes; }
  public void setNotes(String notes) { this.notes = notes; }
  public LocalDateTime getScheduledStart() { return scheduledStart; }
  public void setScheduledStart(LocalDateTime scheduledStart) { this.scheduledStart = scheduledStart; }
  public LocalDateTime getScheduledEnd() { return scheduledEnd; }
  public void setScheduledEnd(LocalDateTime scheduledEnd) { this.scheduledEnd = scheduledEnd; }
}
