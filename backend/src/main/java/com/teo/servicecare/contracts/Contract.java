package com.teo.servicecare.contracts;

import com.teo.servicecare.clients.Client;
import com.teo.servicecare.sites.Site;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "contracts")
public class Contract {

    public enum SupportDays {
        MON_FRI, SEVEN_DAYS
    }

    public enum MeasureWindow {
        BUSINESS_HOURS, CALENDAR
    }

    public enum Status {
        ACTIVE, INACTIVE, EXPIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToMany
    @JoinTable(name = "contract_sites", joinColumns = @JoinColumn(name = "contract_id"), inverseJoinColumns = @JoinColumn(name = "site_id"))
    private Set<Site> sites = new HashSet<>();

    @Column(nullable = false, length = 190)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "auto_renew", nullable = false)
    private boolean autoRenew;

    @Column(name = "notice_days", nullable = false)
    private int noticeDays;

    @Column(nullable = false, length = 50)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(name = "support_days", nullable = false, columnDefinition = "enum('MON_FRI','SEVEN_DAYS')")
    private SupportDays supportDays;

    @Column(name = "support_hours_start", nullable = false)
    private LocalTime supportHoursStart;

    @Column(name = "support_hours_end", nullable = false)
    private LocalTime supportHoursEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "measure_window", nullable = false, columnDefinition = "enum('BUSINESS_HOURS','CALENDAR')")
    private MeasureWindow measureWindow;

    @Column(name = "pause_on_waiting", nullable = false)
    private boolean pauseOnWaiting;

    @Column(name = "resp_crit_hours", nullable = false)
    private int respCritHours;

    @Column(name = "resp_high_hours", nullable = false)
    private int respHighHours;

    @Column(name = "resp_medium_hours", nullable = false)
    private int respMediumHours;

    @Column(name = "resp_low_hours", nullable = false)
    private int respLowHours;

    @Column(name = "reso_crit_hours", nullable = false)
    private int resoCritHours;

    @Column(name = "reso_high_hours", nullable = false)
    private int resoHighHours;

    @Column(name = "reso_medium_hours", nullable = false)
    private int resoMediumHours;

    @Column(name = "reso_low_hours", nullable = false)
    private int resoLowHours;

    @Column(name = "included_hours_month", nullable = false)
    private int includedHoursMonth;

    @Column(name = "max_tickets_month", nullable = false)
    private int maxTicketsMonth;

    @Column(name = "overtime_rate", precision = 10, scale = 2)
    private BigDecimal overtimeRate;

    @Column(name = "emergency_rate", precision = 10, scale = 2)
    private BigDecimal emergencyRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "enum('ACTIVE','INACTIVE','EXPIRED')")
    private Status status = Status.ACTIVE;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<Site> getSites() {
        return sites;
    }

    public void setSites(Set<Site> sites) {
        this.sites = sites;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
    }

    public int getNoticeDays() {
        return noticeDays;
    }

    public void setNoticeDays(int noticeDays) {
        this.noticeDays = noticeDays;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public SupportDays getSupportDays() {
        return supportDays;
    }

    public void setSupportDays(SupportDays supportDays) {
        this.supportDays = supportDays;
    }

    public LocalTime getSupportHoursStart() {
        return supportHoursStart;
    }

    public void setSupportHoursStart(LocalTime supportHoursStart) {
        this.supportHoursStart = supportHoursStart;
    }

    public LocalTime getSupportHoursEnd() {
        return supportHoursEnd;
    }

    public void setSupportHoursEnd(LocalTime supportHoursEnd) {
        this.supportHoursEnd = supportHoursEnd;
    }

    public MeasureWindow getMeasureWindow() {
        return measureWindow;
    }

    public void setMeasureWindow(MeasureWindow measureWindow) {
        this.measureWindow = measureWindow;
    }

    public boolean isPauseOnWaiting() {
        return pauseOnWaiting;
    }

    public void setPauseOnWaiting(boolean pauseOnWaiting) {
        this.pauseOnWaiting = pauseOnWaiting;
    }

    public int getRespCritHours() {
        return respCritHours;
    }

    public void setRespCritHours(int respCritHours) {
        this.respCritHours = respCritHours;
    }

    public int getRespHighHours() {
        return respHighHours;
    }

    public void setRespHighHours(int respHighHours) {
        this.respHighHours = respHighHours;
    }

    public int getRespMediumHours() {
        return respMediumHours;
    }

    public void setRespMediumHours(int respMediumHours) {
        this.respMediumHours = respMediumHours;
    }

    public int getRespLowHours() {
        return respLowHours;
    }

    public void setRespLowHours(int respLowHours) {
        this.respLowHours = respLowHours;
    }

    public int getResoCritHours() {
        return resoCritHours;
    }

    public void setResoCritHours(int resoCritHours) {
        this.resoCritHours = resoCritHours;
    }

    public int getResoHighHours() {
        return resoHighHours;
    }

    public void setResoHighHours(int resoHighHours) {
        this.resoHighHours = resoHighHours;
    }

    public int getResoMediumHours() {
        return resoMediumHours;
    }

    public void setResoMediumHours(int resoMediumHours) {
        this.resoMediumHours = resoMediumHours;
    }

    public int getResoLowHours() {
        return resoLowHours;
    }

    public void setResoLowHours(int resoLowHours) {
        this.resoLowHours = resoLowHours;
    }

    public int getIncludedHoursMonth() {
        return includedHoursMonth;
    }

    public void setIncludedHoursMonth(int includedHoursMonth) {
        this.includedHoursMonth = includedHoursMonth;
    }

    public int getMaxTicketsMonth() {
        return maxTicketsMonth;
    }

    public void setMaxTicketsMonth(int maxTicketsMonth) {
        this.maxTicketsMonth = maxTicketsMonth;
    }

    public BigDecimal getOvertimeRate() {
        return overtimeRate;
    }

    public void setOvertimeRate(BigDecimal overtimeRate) {
        this.overtimeRate = overtimeRate;
    }

    public BigDecimal getEmergencyRate() {
        return emergencyRate;
    }

    public void setEmergencyRate(BigDecimal emergencyRate) {
        this.emergencyRate = emergencyRate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
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
}
