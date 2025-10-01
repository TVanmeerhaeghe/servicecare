package com.teo.servicecare.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class ContractCreateRequest {
  @NotBlank
  private String name;
  private String description;

  @NotNull
  private Long clientId;
  private Set<Long> siteIds;

  @NotNull
  private LocalDate startDate;
  private LocalDate endDate;
  private boolean autoRenew = false;
  private int noticeDays = 30;

  private String timezone = "Europe/Paris";
  private String supportDays = "MON_FRI";
  private LocalTime supportHoursStart = LocalTime.of(9, 0);
  private LocalTime supportHoursEnd = LocalTime.of(18, 0);

  private int respCritHours = 1;
  private int respHighHours = 4;
  private int respMediumHours = 8;
  private int respLowHours = 24;

  private int resoCritHours = 4;
  private int resoHighHours = 16;
  private int resoMediumHours = 40;
  private int resoLowHours = 120;

  private int includedHoursMonth = 0;
  private int maxTicketsMonth = 0;
  private BigDecimal overtimeRate;
  private BigDecimal emergencyRate;

  private Contract.Status status = Contract.Status.ACTIVE;

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Long getClientId() { return clientId; }
  public void setClientId(Long clientId) { this.clientId = clientId; }
  public Set<Long> getSiteIds() { return siteIds; }
  public void setSiteIds(Set<Long> siteIds) { this.siteIds = siteIds; }
  public LocalDate getStartDate() { return startDate; }
  public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
  public LocalDate getEndDate() { return endDate; }
  public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
  public boolean isAutoRenew() { return autoRenew; }
  public void setAutoRenew(boolean autoRenew) { this.autoRenew = autoRenew; }
  public int getNoticeDays() { return noticeDays; }
  public void setNoticeDays(int noticeDays) { this.noticeDays = noticeDays; }
  public String getTimezone() { return timezone; }
  public void setTimezone(String timezone) { this.timezone = timezone; }
  public String getSupportDays() { return supportDays; }
  public void setSupportDays(String supportDays) { this.supportDays = supportDays; }
  public LocalTime getSupportHoursStart() { return supportHoursStart; }
  public void setSupportHoursStart(LocalTime supportHoursStart) { this.supportHoursStart = supportHoursStart; }
  public LocalTime getSupportHoursEnd() { return supportHoursEnd; }
  public void setSupportHoursEnd(LocalTime supportHoursEnd) { this.supportHoursEnd = supportHoursEnd; }
  public int getRespCritHours() { return respCritHours; }
  public void setRespCritHours(int respCritHours) { this.respCritHours = respCritHours; }
  public int getRespHighHours() { return respHighHours; }
  public void setRespHighHours(int respHighHours) { this.respHighHours = respHighHours; }
  public int getRespMediumHours() { return respMediumHours; }
  public void setRespMediumHours(int respMediumHours) { this.respMediumHours = respMediumHours; }
  public int getRespLowHours() { return respLowHours; }
  public void setRespLowHours(int respLowHours) { this.respLowHours = respLowHours; }
  public int getResoCritHours() { return resoCritHours; }
  public void setResoCritHours(int resoCritHours) { this.resoCritHours = resoCritHours; }
  public int getResoHighHours() { return resoHighHours; }
  public void setResoHighHours(int resoHighHours) { this.resoHighHours = resoHighHours; }
  public int getResoMediumHours() { return resoMediumHours; }
  public void setResoMediumHours(int resoMediumHours) { this.resoMediumHours = resoMediumHours; }
  public int getResoLowHours() { return resoLowHours; }
  public void setResoLowHours(int resoLowHours) { this.resoLowHours = resoLowHours; }
  public int getIncludedHoursMonth() { return includedHoursMonth; }
  public void setIncludedHoursMonth(int includedHoursMonth) { this.includedHoursMonth = includedHoursMonth; }
  public int getMaxTicketsMonth() { return maxTicketsMonth; }
  public void setMaxTicketsMonth(int maxTicketsMonth) { this.maxTicketsMonth = maxTicketsMonth; }
  public BigDecimal getOvertimeRate() { return overtimeRate; }
  public void setOvertimeRate(BigDecimal overtimeRate) { this.overtimeRate = overtimeRate; }
  public BigDecimal getEmergencyRate() { return emergencyRate; }
  public void setEmergencyRate(BigDecimal emergencyRate) { this.emergencyRate = emergencyRate; }
  public Contract.Status getStatus() { return status; }
  public void setStatus(Contract.Status status) { this.status = status; }
}
