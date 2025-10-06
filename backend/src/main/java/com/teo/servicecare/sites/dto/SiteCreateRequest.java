package com.teo.servicecare.sites.dto;

import com.teo.servicecare.sites.Site.SiteCms;
import com.teo.servicecare.sites.Site.SiteEnvironment;
import com.teo.servicecare.sites.Site.SiteSslStatus;
import com.teo.servicecare.sites.Site.SiteStatus;
import com.teo.servicecare.sites.Site.SiteType;
import jakarta.validation.constraints.*;

import java.time.Instant;

public class SiteCreateRequest {
  @NotBlank
  private String name;
  @NotBlank
  private String url;

  private SiteEnvironment environment;
  private SiteType type;
  private SiteCms cms;
  private SiteStatus status;

  private String hostingProvider;
  private String hostingPlan;
  private String repoUrl;
  private String prodUrl;
  private String stagingUrl;
  private String serverIp;
  private String phpVersion;
  private String nodeVersion;
  private String mysqlVersion;
  private SiteSslStatus sslStatus;
  private String analyticsId;
  private String gtId;
  private String sentryDsn;
  private Boolean maintenanceEnabled;
  private String maintenanceEmail;
  private Instant lastMaintenanceAt;
  private Instant nextMaintenanceAt;
  private Instant lastBackupAt;
  private String notes;

  @NotNull
  private Long clientId;

  // Getters / Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public SiteEnvironment getEnvironment() {
    return environment;
  }

  public void setEnvironment(SiteEnvironment environment) {
    this.environment = environment;
  }

  public SiteType getType() {
    return type;
  }

  public void setType(SiteType type) {
    this.type = type;
  }

  public SiteCms getCms() {
    return cms;
  }

  public void setCms(SiteCms cms) {
    this.cms = cms;
  }

  public SiteStatus getStatus() {
    return status;
  }

  public void setStatus(SiteStatus status) {
    this.status = status;
  }

  public String getHostingProvider() {
    return hostingProvider;
  }

  public void setHostingProvider(String hostingProvider) {
    this.hostingProvider = hostingProvider;
  }

  public Long getClientId() {
    return clientId;
  }

  public void setClientId(Long clientId) {
    this.clientId = clientId;
  }

  public String getRepoUrl() {
    return repoUrl;
  }

  public void setRepoUrl(String repoUrl) {
    this.repoUrl = repoUrl;
  }

  public String getProdUrl() {
    return prodUrl;
  }

  public void setProdUrl(String prodUrl) {
    this.prodUrl = prodUrl;
  }

  public String getStagingUrl() {
    return stagingUrl;
  }

  public void setStagingUrl(String stagingUrl) {
    this.stagingUrl = stagingUrl;
  }

  public String getHostingPlan() {
    return hostingPlan;
  }

  public void setHostingPlan(String hostingPlan) {
    this.hostingPlan = hostingPlan;
  }

  public String getServerIp() {
    return serverIp;
  }

  public void setServerIp(String serverIp) {
    this.serverIp = serverIp;
  }

  public String getPhpVersion() {
    return phpVersion;
  }

  public void setPhpVersion(String phpVersion) {
    this.phpVersion = phpVersion;
  }

  public String getNodeVersion() {
    return nodeVersion;
  }

  public void setNodeVersion(String nodeVersion) {
    this.nodeVersion = nodeVersion;
  }

  public String getMysqlVersion() {
    return mysqlVersion;
  }

  public void setMysqlVersion(String mysqlVersion) {
    this.mysqlVersion = mysqlVersion;
  }

  public SiteSslStatus getSslStatus() {
    return sslStatus;
  }

  public void setSslStatus(SiteSslStatus sslStatus) {
    this.sslStatus = sslStatus;
  }

  public String getAnalyticsId() {
    return analyticsId;
  }

  public void setAnalyticsId(String analyticsId) {
    this.analyticsId = analyticsId;
  }

  public String getGtId() {
    return gtId;
  }

  public void setGtId(String gtId) {
    this.gtId = gtId;
  }

  public String getSentryDsn() {
    return sentryDsn;
  }

  public void setSentryDsn(String sentryDsn) {
    this.sentryDsn = sentryDsn;
  }

  public Boolean getMaintenanceEnabled() {
    return maintenanceEnabled;
  }

  public void setMaintenanceEnabled(Boolean maintenanceEnabled) {
    this.maintenanceEnabled = maintenanceEnabled;
  }

  public String getMaintenanceEmail() {
    return maintenanceEmail;
  }

  public void setMaintenanceEmail(String maintenanceEmail) {
    this.maintenanceEmail = maintenanceEmail;
  }

  public Instant getLastMaintenanceAt() {
    return lastMaintenanceAt;
  }

  public void setLastMaintenanceAt(Instant lastMaintenanceAt) {
    this.lastMaintenanceAt = lastMaintenanceAt;
  }

  public Instant getNextMaintenanceAt() {
    return nextMaintenanceAt;
  }

  public void setNextMaintenanceAt(Instant nextMaintenanceAt) {
    this.nextMaintenanceAt = nextMaintenanceAt;
  }

  public Instant getLastBackupAt() {
    return lastBackupAt;
  }

  public void setLastBackupAt(Instant lastBackupAt) {
    this.lastBackupAt = lastBackupAt;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
