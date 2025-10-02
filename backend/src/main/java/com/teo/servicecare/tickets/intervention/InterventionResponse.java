package com.teo.servicecare.tickets.intervention;

import java.time.LocalDateTime;

public class InterventionResponse {
  private Long id;
  private Long ticketId;
  private Intervention.Type type;
  private Intervention.Status status;
  private Long technicianUserId;
  private LocalDateTime scheduledStart;
  private LocalDateTime scheduledEnd;
  private LocalDateTime actualStart;
  private LocalDateTime actualEnd;
  private String title;
  private String notes;
  private String report;
  private Integer travelMinutes;
  private Integer workMinutes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static InterventionResponse from(Intervention i) {
    var r = new InterventionResponse();
    r.id = i.getId();
    r.ticketId = i.getTicketId();
    r.type = i.getType();
    r.status = i.getStatus();
    r.technicianUserId = i.getTechnicianUserId();
    r.scheduledStart = i.getScheduledStart();
    r.scheduledEnd = i.getScheduledEnd();
    r.actualStart = i.getActualStart();
    r.actualEnd = i.getActualEnd();
    r.title = i.getTitle();
    r.notes = i.getNotes();
    r.report = i.getReport();
    r.travelMinutes = i.getTravelMinutes();
    r.workMinutes = i.getWorkMinutes();
    r.createdAt = i.getCreatedAt();
    r.updatedAt = i.getUpdatedAt();
    return r;
  }

  public Long getId() { return id; }
  public Long getTicketId() { return ticketId; }
  public Intervention.Type getType() { return type; }
  public Intervention.Status getStatus() { return status; }
  public Long getTechnicianUserId() { return technicianUserId; }
  public LocalDateTime getScheduledStart() { return scheduledStart; }
  public LocalDateTime getScheduledEnd() { return scheduledEnd; }
  public LocalDateTime getActualStart() { return actualStart; }
  public LocalDateTime getActualEnd() { return actualEnd; }
  public String getTitle() { return title; }
  public String getNotes() { return notes; }
  public String getReport() { return report; }
  public Integer getTravelMinutes() { return travelMinutes; }
  public Integer getWorkMinutes() { return workMinutes; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
}
