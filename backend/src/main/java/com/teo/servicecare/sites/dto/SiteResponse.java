package com.teo.servicecare.sites.dto;

import com.teo.servicecare.sites.Site;
import com.teo.servicecare.sites.Site.SiteCms;
import com.teo.servicecare.sites.Site.SiteEnvironment;
import com.teo.servicecare.sites.Site.SiteSslStatus;
import com.teo.servicecare.sites.Site.SiteStatus;
import com.teo.servicecare.sites.Site.SiteType;

import java.time.Instant;

public class SiteResponse {
  public Long id;
  public String name;
  public String url;
  public SiteEnvironment environment;
  public SiteType type;
  public SiteCms cms;
  public SiteStatus status;
  public String hostingProvider;
  public String hostingPlan;
  public String repoUrl;
  public String prodUrl;
  public String stagingUrl;
  public String serverIp;
  public String phpVersion;
  public String nodeVersion;
  public String mysqlVersion;
  public SiteSslStatus sslStatus;
  public String analyticsId;
  public String gtId;
  public String sentryDsn;
  public Boolean maintenanceEnabled;
  public String maintenanceEmail;
  public Instant lastMaintenanceAt;
  public Instant nextMaintenanceAt;
  public Instant lastBackupAt;
  public String notes;
  public Long clientId;
  public Instant createdAt;
  public Instant updatedAt;
  public Long createdById;
  public Long updatedById;

  public static SiteResponse from(Site s) {
    var r = new SiteResponse();
    r.id = s.getId();
    r.name = s.getName();
    r.url = s.getUrl();
    r.environment = s.getEnvironment();
    r.type = s.getType();
    r.cms = s.getCms();
    r.status = s.getStatus();
    r.hostingProvider = s.getHostingProvider();
    r.hostingPlan = s.getHostingPlan();
    r.repoUrl = s.getRepoUrl();
    r.prodUrl = s.getProdUrl();
    r.stagingUrl = s.getStagingUrl();
    r.serverIp = s.getServerIp();
    r.phpVersion = s.getPhpVersion();
    r.nodeVersion = s.getNodeVersion();
    r.mysqlVersion = s.getMysqlVersion();
    r.sslStatus = s.getSslStatus();
    r.analyticsId = s.getAnalyticsId();
    r.gtId = s.getGtId();
    r.sentryDsn = s.getSentryDsn();
    r.maintenanceEnabled = s.getMaintenanceEnabled();
    r.maintenanceEmail = s.getMaintenanceEmail();
    r.lastMaintenanceAt = s.getLastMaintenanceAt();
    r.nextMaintenanceAt = s.getNextMaintenanceAt();
    r.lastBackupAt = s.getLastBackupAt();
    r.notes = s.getNotes();
    r.clientId = (s.getClient() != null ? s.getClient().getId() : null);
    r.createdAt = s.getCreatedAt();
    r.updatedAt = s.getUpdatedAt();
    r.createdById = s.getCreatedBy() != null ? s.getCreatedBy().getId() : null;
    r.updatedById = s.getUpdatedBy() != null ? s.getUpdatedBy().getId() : null;
    return r;
  }
}
