package com.teo.servicecare.tickets;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {
  public enum TicketPriority { CRITICAL, HIGH, MEDIUM, LOW }
  public enum TicketStatus { OPEN, ASSIGNED, IN_PROGRESS, WAITING, CLOSED, CANCELED }

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false) private Long clientId;
  private Long siteId;
  private Long contractId;

  @Column(nullable = false, length = 190) private String title;
  @Lob private String description;

  @Enumerated(EnumType.STRING) @Column(nullable = false)
  private TicketPriority priority = TicketPriority.MEDIUM;

  @Enumerated(EnumType.STRING) @Column(nullable = false)
  private TicketStatus status = TicketStatus.OPEN;

  @Column(length = 190) private String waitingReason;

  private Long assigneeUserId;

  @Column(nullable = false) private LocalDateTime respondBy;
  @Column(nullable = false) private LocalDateTime resolveBy;

  private LocalDateTime respondedAt;
  private LocalDateTime resolvedAt;

  @Column(nullable = false) private boolean slaBreached = false;

  @Column(nullable = false) private int pausedSeconds = 0;
  private LocalDateTime pauseStartedAt;

  private Long createdBy;
  private Long updatedBy;

  @Column(insertable = false, updatable = false) private LocalDateTime createdAt;
  @Column(insertable = false, updatable = false) private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;

  public Long getId() { return id; }
  public Long getClientId() { return clientId; }
  public void setClientId(Long clientId) { this.clientId = clientId; }
  public Long getSiteId() { return siteId; }
  public void setSiteId(Long siteId) { this.siteId = siteId; }
  public Long getContractId() { return contractId; }
  public void setContractId(Long contractId) { this.contractId = contractId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public TicketPriority getPriority() { return priority; }
  public void setPriority(TicketPriority priority) { this.priority = priority; }
  public TicketStatus getStatus() { return status; }
  public void setStatus(TicketStatus status) { this.status = status; }
  public String getWaitingReason() { return waitingReason; }
  public void setWaitingReason(String waitingReason) { this.waitingReason = waitingReason; }
  public Long getAssigneeUserId() { return assigneeUserId; }
  public void setAssigneeUserId(Long assigneeUserId) { this.assigneeUserId = assigneeUserId; }
  public LocalDateTime getRespondBy() { return respondBy; }
  public void setRespondBy(LocalDateTime respondBy) { this.respondBy = respondBy; }
  public LocalDateTime getResolveBy() { return resolveBy; }
  public void setResolveBy(LocalDateTime resolveBy) { this.resolveBy = resolveBy; }
  public LocalDateTime getRespondedAt() { return respondedAt; }
  public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
  public LocalDateTime getResolvedAt() { return resolvedAt; }
  public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
  public boolean isSlaBreached() { return slaBreached; }
  public void setSlaBreached(boolean slaBreached) { this.slaBreached = slaBreached; }
  public int getPausedSeconds() { return pausedSeconds; }
  public void setPausedSeconds(int pausedSeconds) { this.pausedSeconds = pausedSeconds; }
  public LocalDateTime getPauseStartedAt() { return pauseStartedAt; }
  public void setPauseStartedAt(LocalDateTime pauseStartedAt) { this.pauseStartedAt = pauseStartedAt; }
  public Long getCreatedBy() { return createdBy; }
  public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
  public Long getUpdatedBy() { return updatedBy; }
  public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public LocalDateTime getDeletedAt() { return deletedAt; }
  public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}

