package com.teo.servicecare.tickets;

import com.teo.servicecare.contracts.Contract;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tickets")
public class Ticket {
  public enum TicketPriority {
    CRITICAL, HIGH, MEDIUM, LOW
  }

  public enum TicketStatus {
    OPEN, ASSIGNED, IN_PROGRESS, WAITING, CLOSED, CANCELED
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long clientId;
  private Long siteId;
  private Long contractId;

  @Column(nullable = false, length = 190)
  private String title;
  @Lob
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TicketPriority priority = TicketPriority.MEDIUM;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TicketStatus status = TicketStatus.OPEN;

  @Column(length = 190)
  private String waitingReason;

  private Long assigneeUserId;

  @Column(nullable = false)
  private LocalDateTime respondBy;
  @Column(nullable = false)
  private LocalDateTime resolveBy;

  private LocalDateTime respondedAt;
  private LocalDateTime resolvedAt;

  @Column(nullable = false)
  private boolean slaBreached = false;

  @Column(nullable = false)
  private int pausedSeconds = 0;
  private LocalDateTime pauseStartedAt;

  private String slaTimezone;

  @Enumerated(EnumType.STRING)
  private Contract.SupportDays slaSupportDays;

  private LocalTime slaSupportHoursStart;
  private LocalTime slaSupportHoursEnd;

  @Enumerated(EnumType.STRING)
  private Contract.MeasureWindow slaMeasureWindow;

  private Boolean slaPauseOnWaiting;

  private Integer slaRespCritHours;
  private Integer slaRespHighHours;
  private Integer slaRespMediumHours;
  private Integer slaRespLowHours;

  private Integer slaResoCritHours;
  private Integer slaResoHighHours;
  private Integer slaResoMediumHours;
  private Integer slaResoLowHours;

  private Long createdBy;
  private Long updatedBy;

  @Column(insertable = false, updatable = false)
  private LocalDateTime createdAt;
  @Column(insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;

  public Long getId() {
    return id;
  }

  public Long getClientId() {
    return clientId;
  }

  public void setClientId(Long clientId) {
    this.clientId = clientId;
  }

  public Long getSiteId() {
    return siteId;
  }

  public void setSiteId(Long siteId) {
    this.siteId = siteId;
  }

  public Long getContractId() {
    return contractId;
  }

  public void setContractId(Long contractId) {
    this.contractId = contractId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public TicketPriority getPriority() {
    return priority;
  }

  public void setPriority(TicketPriority priority) {
    this.priority = priority;
  }

  public TicketStatus getStatus() {
    return status;
  }

  public void setStatus(TicketStatus status) {
    this.status = status;
  }

  public String getWaitingReason() {
    return waitingReason;
  }

  public void setWaitingReason(String waitingReason) {
    this.waitingReason = waitingReason;
  }

  public Long getAssigneeUserId() {
    return assigneeUserId;
  }

  public void setAssigneeUserId(Long assigneeUserId) {
    this.assigneeUserId = assigneeUserId;
  }

  public LocalDateTime getRespondBy() {
    return respondBy;
  }

  public void setRespondBy(LocalDateTime respondBy) {
    this.respondBy = respondBy;
  }

  public LocalDateTime getResolveBy() {
    return resolveBy;
  }

  public void setResolveBy(LocalDateTime resolveBy) {
    this.resolveBy = resolveBy;
  }

  public LocalDateTime getRespondedAt() {
    return respondedAt;
  }

  public void setRespondedAt(LocalDateTime respondedAt) {
    this.respondedAt = respondedAt;
  }

  public LocalDateTime getResolvedAt() {
    return resolvedAt;
  }

  public void setResolvedAt(LocalDateTime resolvedAt) {
    this.resolvedAt = resolvedAt;
  }

  public boolean isSlaBreached() {
    return slaBreached;
  }

  public void setSlaBreached(boolean slaBreached) {
    this.slaBreached = slaBreached;
  }

  public int getPausedSeconds() {
    return pausedSeconds;
  }

  public void setPausedSeconds(int pausedSeconds) {
    this.pausedSeconds = pausedSeconds;
  }

  public LocalDateTime getPauseStartedAt() {
    return pauseStartedAt;
  }

  public void setPauseStartedAt(LocalDateTime pauseStartedAt) {
    this.pauseStartedAt = pauseStartedAt;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  public Long getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(Long updatedBy) {
    this.updatedBy = updatedBy;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public String getSlaTimezone() {
    return slaTimezone;
  }

  public void setSlaTimezone(String slaTimezone) {
    this.slaTimezone = slaTimezone;
  }

  public Contract.SupportDays getSlaSupportDays() {
    return slaSupportDays;
  }

  public void setSlaSupportDays(Contract.SupportDays slaSupportDays) {
    this.slaSupportDays = slaSupportDays;
  }

  public LocalTime getSlaSupportHoursStart() {
    return slaSupportHoursStart;
  }

  public void setSlaSupportHoursStart(LocalTime slaSupportHoursStart) {
    this.slaSupportHoursStart = slaSupportHoursStart;
  }

  public LocalTime getSlaSupportHoursEnd() {
    return slaSupportHoursEnd;
  }

  public void setSlaSupportHoursEnd(LocalTime slaSupportHoursEnd) {
    this.slaSupportHoursEnd = slaSupportHoursEnd;
  }

  public Contract.MeasureWindow getSlaMeasureWindow() {
    return slaMeasureWindow;
  }

  public void setSlaMeasureWindow(Contract.MeasureWindow slaMeasureWindow) {
    this.slaMeasureWindow = slaMeasureWindow;
  }

  public Boolean getSlaPauseOnWaiting() {
    return slaPauseOnWaiting;
  }

  public void setSlaPauseOnWaiting(Boolean slaPauseOnWaiting) {
    this.slaPauseOnWaiting = slaPauseOnWaiting;
  }

  public Integer getSlaRespCritHours() {
    return slaRespCritHours;
  }

  public void setSlaRespCritHours(Integer v) {
    this.slaRespCritHours = v;
  }

  public Integer getSlaRespHighHours() {
    return slaRespHighHours;
  }

  public void setSlaRespHighHours(Integer v) {
    this.slaRespHighHours = v;
  }

  public Integer getSlaRespMediumHours() {
    return slaRespMediumHours;
  }

  public void setSlaRespMediumHours(Integer v) {
    this.slaRespMediumHours = v;
  }

  public Integer getSlaRespLowHours() {
    return slaRespLowHours;
  }

  public void setSlaRespLowHours(Integer v) {
    this.slaRespLowHours = v;
  }

  public Integer getSlaResoCritHours() {
    return slaResoCritHours;
  }

  public void setSlaResoCritHours(Integer v) {
    this.slaResoCritHours = v;
  }

  public Integer getSlaResoHighHours() {
    return slaResoHighHours;
  }

  public void setSlaResoHighHours(Integer v) {
    this.slaResoHighHours = v;
  }

  public Integer getSlaResoMediumHours() {
    return slaResoMediumHours;
  }

  public void setSlaResoMediumHours(Integer v) {
    this.slaResoMediumHours = v;
  }

  public Integer getSlaResoLowHours() {
    return slaResoLowHours;
  }

  public void setSlaResoLowHours(Integer v) {
    this.slaResoLowHours = v;
  }
  // --- fin getters/setters SLA snapshot ---
}
