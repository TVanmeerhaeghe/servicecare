package com.teo.servicecare.contracts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class ContractUpdateRequest {
  private String name;
  private String description;

  private Long clientId;       
  private Set<Long> siteIds;   

  private LocalDate startDate;
  private LocalDate endDate;
  private Boolean autoRenew;
  private Integer noticeDays;

  private String timezone;
  private String supportDays;
  private LocalTime supportHoursStart;
  private LocalTime supportHoursEnd;

  private Integer respCritHours;
  private Integer respHighHours;
  private Integer respMediumHours;
  private Integer respLowHours;

  private Integer resoCritHours;
  private Integer resoHighHours;
  private Integer resoMediumHours;
  private Integer resoLowHours;

  private Integer includedHoursMonth;
  private Integer maxTicketsMonth;
  private BigDecimal overtimeRate;
  private BigDecimal emergencyRate;

  private Contract.Status status;

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
  public Boolean getAutoRenew() { return autoRenew; }
  public void setAutoRenew(Boolean autoRenew) { this.autoRenew = autoRenew; }
  public Integer getNoticeDays() { return noticeDays; }
  public void setNoticeDays(Integer noticeDays) { this.noticeDays = noticeDays; }
  public String getTimezone() { return timezone; }
  public void setTimezone(String timezone) { this.timezone = timezone; }
  public String getSupportDays() { return supportDays; }
  public void setSupportDays(String supportDays) { this.supportDays = supportDays; }
  public LocalTime getSupportHoursStart() { return supportHoursStart; }
  public void setSupportHoursStart(LocalTime supportHoursStart) { this.supportHoursStart = supportHoursStart; }
  public LocalTime getSupportHoursEnd() { return supportHoursEnd; }
  public void setSupportHoursEnd(LocalTime supportHoursEnd) { this.supportHoursEnd = supportHoursEnd; }
  public Integer getRespCritHours() { return respCritHours; }
  public void setRespCritHours(Integer respCritHours) { this.respCritHours = respCritHours; }
  public Integer getRespHighHours() { return respHighHours; }
  public void setRespHighHours(Integer respHighHours) { this.respHighHours = respHighHours; }
  public Integer getRespMediumHours() { return respMediumHours; }
  public void setRespMediumHours(Integer respMediumHours) { this.respMediumHours = respMediumHours; }
  public Integer getRespLowHours() { return respLowHours; }
  public void setRespLowHours(Integer respLowHours) { this.respLowHours = respLowHours; }
  public Integer getResoCritHours() { return resoCritHours; }
  public void setResoCritHours(Integer resoCritHours) { this.resoCritHours = resoCritHours; }
  public Integer getResoHighHours() { return resoHighHours; }
  public void setResoHighHours(Integer resoHighHours) { this.resoHighHours = resoHighHours; }
  public Integer getResoMediumHours() { return resoMediumHours; }
  public void setResoMediumHours(Integer resoMediumHours) { this.resoMediumHours = resoMediumHours; }
  public Integer getResoLowHours() { return resoLowHours; }
  public void setResoLowHours(Integer resoLowHours) { this.resoLowHours = resoLowHours; }
  public Integer getIncludedHoursMonth() { return includedHoursMonth; }
  public void setIncludedHoursMonth(Integer includedHoursMonth) { this.includedHoursMonth = includedHoursMonth; }
  public Integer getMaxTicketsMonth() { return maxTicketsMonth; }
  public void setMaxTicketsMonth(Integer maxTicketsMonth) { this.maxTicketsMonth = maxTicketsMonth; }
  public BigDecimal getOvertimeRate() { return overtimeRate; }
  public void setOvertimeRate(BigDecimal overtimeRate) { this.overtimeRate = overtimeRate; }
  public BigDecimal getEmergencyRate() { return emergencyRate; }
  public void setEmergencyRate(BigDecimal emergencyRate) { this.emergencyRate = emergencyRate; }
  public Contract.Status getStatus() { return status; }
  public void setStatus(Contract.Status status) { this.status = status; }
}
