package com.teo.servicecare.tickets.intervention.dto;

import com.teo.servicecare.tickets.intervention.Intervention;
import java.time.LocalDateTime;

public class InterventionUpdateRequest {
  private Intervention.Type type;
  private Long technicianUserId;
  private String title;
  private String notes;
  private String report;

  private LocalDateTime scheduledStart;
  private LocalDateTime scheduledEnd;

  private Integer travelMinutes;
  private Integer workMinutes;

  private LocalDateTime actualStart;
  private LocalDateTime actualEnd;

  public Intervention.Type getType() {
    return type;
  }

  public void setType(Intervention.Type type) {
    this.type = type;
  }

  public Long getTechnicianUserId() {
    return technicianUserId;
  }

  public void setTechnicianUserId(Long technicianUserId) {
    this.technicianUserId = technicianUserId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getReport() {
    return report;
  }

  public void setReport(String report) {
    this.report = report;
  }

  public LocalDateTime getScheduledStart() {
    return scheduledStart;
  }

  public void setScheduledStart(LocalDateTime scheduledStart) {
    this.scheduledStart = scheduledStart;
  }

  public LocalDateTime getScheduledEnd() {
    return scheduledEnd;
  }

  public void setScheduledEnd(LocalDateTime scheduledEnd) {
    this.scheduledEnd = scheduledEnd;
  }

  public Integer getTravelMinutes() {
    return travelMinutes;
  }

  public void setTravelMinutes(Integer travelMinutes) {
    this.travelMinutes = travelMinutes;
  }

  public Integer getWorkMinutes() {
    return workMinutes;
  }

  public void setWorkMinutes(Integer workMinutes) {
    this.workMinutes = workMinutes;
  }

  public LocalDateTime getActualStart() {
    return actualStart;
  }

  public void setActualStart(LocalDateTime actualStart) {
    this.actualStart = actualStart;
  }

  public LocalDateTime getActualEnd() {
    return actualEnd;
  }

  public void setActualEnd(LocalDateTime actualEnd) {
    this.actualEnd = actualEnd;
  }
}
