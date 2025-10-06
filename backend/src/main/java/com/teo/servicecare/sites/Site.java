package com.teo.servicecare.sites;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teo.servicecare.clients.Client;
import com.teo.servicecare.users.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "sites")
public class Site {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String url;

  public enum SiteEnvironment {
    PROD, STAGING, DEV
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "environment", columnDefinition = "enum('PROD','STAGING','DEV')")
  private SiteEnvironment environment;

  public enum SiteType {
    WEBSITE, SHOP, API, APP
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "type", columnDefinition = "enum('WEBSITE','SHOP','API','APP')")
  private SiteType type;

  public enum SiteCms {
    WORDPRESS,
    SHOPIFY,
    PRESTASHOP,
    DRUPAL,
    JOOMLA,
    MAGENTO,
    WIX,
    CUSTOM,
    OTHER
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "cms", columnDefinition = "enum('WORDPRESS','SHOPIFY','PRESTASHOP','DRUPAL','JOOMLA','MAGENTO','WIX','CUSTOM','OTHER')")
  private SiteCms cms;

  public enum SiteStatus {
    ACTIVE, INACTIVE
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "enum('ACTIVE','INACTIVE')")
  private SiteStatus status;

  @Column(name = "repo_url")
  private String repoUrl;

  @Column(name = "prod_url")
  private String prodUrl;

  @Column(name = "staging_url")
  private String stagingUrl;

  @Column(name = "hosting_provider")
  private String hostingProvider;

  @Column(name = "hosting_plan")
  private String hostingPlan;

  @Column(name = "server_ip")
  private String serverIp;

  @Column(name = "php_version")
  private String phpVersion;

  @Column(name = "node_version")
  private String nodeVersion;

  @Column(name = "mysql_version")
  private String mysqlVersion;

  public enum SiteSslStatus {
    UNKNOWN, VALID, EXPIRED, NOT_INSTALLED
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "ssl_status", columnDefinition = "enum('UNKNOWN','VALID','EXPIRED','NOT_INSTALLED')")
  private SiteSslStatus sslStatus;

  @Column(name = "analytics_id")
  private String analyticsId;

  @Column(name = "gt_id")
  private String gtId;

  @Column(name = "sentry_dsn")
  private String sentryDsn;

  @Column(name = "maintenance_enabled")
  private Boolean maintenanceEnabled;

  @Column(name = "maintenance_email")
  private String maintenanceEmail;

  @Column(name = "last_maintenance_at")
  private Instant lastMaintenanceAt;

  @Column(name = "next_maintenance_at")
  private Instant nextMaintenanceAt;

  @Column(name = "last_backup_at")
  private Instant lastBackupAt;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @Column(name = "created_at", updatable = false, insertable = false)
  private Instant createdAt;

  @Column(name = "updated_at", insertable = false)
  private Instant updatedAt;

  @ManyToOne
  @JoinColumn(name = "client_id")
  @JsonBackReference
  @JsonIgnore
  private Client client;

  @ManyToOne
  @JoinColumn(name = "created_by")
  @JsonIgnore
  private User createdBy;

  @ManyToOne
  @JoinColumn(name = "updated_by")
  @JsonIgnore
  private User updatedBy;

  // --- Getters & Setters ---

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public String getHostingProvider() {
    return hostingProvider;
  }

  public void setHostingProvider(String hostingProvider) {
    this.hostingProvider = hostingProvider;
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

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public User getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  @JsonProperty("clientId")
  public Long getClientId() {
    return client != null ? client.getId() : null;
  }

  @JsonProperty("createdById")
  public Long getCreatedById() {
    return createdBy != null ? createdBy.getId() : null;
  }

  @JsonProperty("updatedById")
  public Long getUpdatedById() {
    return updatedBy != null ? updatedBy.getId() : null;
  }
}
