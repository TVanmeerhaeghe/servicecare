package com.teo.servicecare.tickets.intervention;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interventions")
public class Intervention {

  public enum Type {
    ONSITE, REMOTE
  }

  public enum Status {
    PLANNED, IN_PROGRESS, DONE, CANCELED, NO_SHOW
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long ticketId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Type type = Type.REMOTE;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status = Status.PLANNED;

  private Long technicianUserId;

  private LocalDateTime scheduledStart;
  private LocalDateTime scheduledEnd;

  private LocalDateTime actualStart;
  private LocalDateTime actualEnd;

  @Column(length = 190)
  private String title;

  @Lob
  @Column(columnDefinition = "LONGTEXT")
  private String notes;

  @Lob
  @Column(columnDefinition = "LONGTEXT")
  private String report;

  private Integer travelMinutes = 0;
  private Integer workMinutes = 0;

  @Column(insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;

  public Long getId() {
    return id;
  }

  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Long getTechnicianUserId() {
    return technicianUserId;
  }

  public void setTechnicianUserId(Long technicianUserId) {
    this.technicianUserId = technicianUserId;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }
}
