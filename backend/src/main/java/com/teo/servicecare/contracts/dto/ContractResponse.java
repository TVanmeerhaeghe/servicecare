package com.teo.servicecare.contracts.dto;

import com.teo.servicecare.contracts.Contract;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.stream.Collectors;

public class ContractResponse {
  public Long id;
  public Long clientId;
  public String clientName;

  public Set<Long> siteIds;

  public String name;
  public String description;

  public LocalDate startDate;
  public LocalDate endDate;

  public boolean autoRenew;
  public int noticeDays;

  public String timezone;

  public Contract.SupportDays supportDays;
  public LocalTime supportHoursStart;
  public LocalTime supportHoursEnd;

  public Contract.MeasureWindow measureWindow;
  public boolean pauseOnWaiting;

  public int respCritHours;
  public int respHighHours;
  public int respMediumHours;
  public int respLowHours;

  public int resoCritHours;
  public int resoHighHours;
  public int resoMediumHours;
  public int resoLowHours;

  public int includedHoursMonth;
  public int maxTicketsMonth;
  public BigDecimal overtimeRate;
  public BigDecimal emergencyRate;

  public Contract.Status status;

  public Instant createdAt;
  public Instant updatedAt;

  public static ContractResponse from(Contract c) {
    var r = new ContractResponse();
    r.id = c.getId();
    r.clientId = (c.getClient() != null ? c.getClient().getId() : null);
    r.clientName = (c.getClient() != null ? c.getClient().getName() : null);
    r.siteIds = (c.getSites() != null)
        ? c.getSites().stream().map(s -> s.getId()).collect(Collectors.toSet())
        : java.util.Set.of();

    r.name = c.getName();
    r.description = c.getDescription();
    r.startDate = c.getStartDate();
    r.endDate = c.getEndDate();
    r.autoRenew = c.isAutoRenew();
    r.noticeDays = c.getNoticeDays();
    r.timezone = c.getTimezone();
    r.supportDays = c.getSupportDays();
    r.supportHoursStart = c.getSupportHoursStart();
    r.supportHoursEnd = c.getSupportHoursEnd();
    r.measureWindow = c.getMeasureWindow();
    r.pauseOnWaiting = c.isPauseOnWaiting();

    r.respCritHours = c.getRespCritHours();
    r.respHighHours = c.getRespHighHours();
    r.respMediumHours = c.getRespMediumHours();
    r.respLowHours = c.getRespLowHours();

    r.resoCritHours = c.getResoCritHours();
    r.resoHighHours = c.getResoHighHours();
    r.resoMediumHours = c.getResoMediumHours();
    r.resoLowHours = c.getResoLowHours();

    r.includedHoursMonth = c.getIncludedHoursMonth();
    r.maxTicketsMonth = c.getMaxTicketsMonth();
    r.overtimeRate = c.getOvertimeRate();
    r.emergencyRate = c.getEmergencyRate();
    r.status = c.getStatus();

    r.createdAt = c.getCreatedAt();
    r.updatedAt = c.getUpdatedAt();
    return r;
  }
}
