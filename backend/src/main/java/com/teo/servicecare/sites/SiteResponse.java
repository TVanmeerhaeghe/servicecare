package com.teo.servicecare.sites;

import com.teo.servicecare.sites.Site.SiteCms;
import com.teo.servicecare.sites.Site.SiteEnvironment;
import com.teo.servicecare.sites.Site.SiteStatus;
import com.teo.servicecare.sites.Site.SiteType;

public class SiteResponse {
  public Long id;
  public String name;
  public String url;
  public SiteEnvironment environment;
  public SiteType type;
  public SiteCms cms;
  public SiteStatus status;
  public String hostingProvider;
  public Long clientId;

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
    r.clientId = (s.getClient() != null ? s.getClient().getId() : null);
    return r;
  }
}
