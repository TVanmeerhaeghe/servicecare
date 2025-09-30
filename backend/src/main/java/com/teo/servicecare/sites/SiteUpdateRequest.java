package com.teo.servicecare.sites;

import com.teo.servicecare.sites.Site.SiteCms;
import com.teo.servicecare.sites.Site.SiteEnvironment;
import com.teo.servicecare.sites.Site.SiteStatus;
import com.teo.servicecare.sites.Site.SiteType;

public class SiteUpdateRequest {
  private String name;
  private String url;
  private SiteEnvironment environment;
  private SiteType type;
  private SiteCms cms;
  private SiteStatus status;
  private String hostingProvider;
  private Long clientId;

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
  public SiteEnvironment getEnvironment() { return environment; }
  public void setEnvironment(SiteEnvironment environment) { this.environment = environment; }
  public SiteType getType() { return type; }
  public void setType(SiteType type) { this.type = type; }
  public SiteCms getCms() { return cms; }
  public void setCms(SiteCms cms) { this.cms = cms; }
  public SiteStatus getStatus() { return status; }
  public void setStatus(SiteStatus status) { this.status = status; }
  public String getHostingProvider() { return hostingProvider; }
  public void setHostingProvider(String hostingProvider) { this.hostingProvider = hostingProvider; }
  public Long getClientId() { return clientId; }
  public void setClientId(Long clientId) { this.clientId = clientId; }
}
