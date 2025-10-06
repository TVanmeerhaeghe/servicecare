package com.teo.servicecare.sites;

import com.teo.servicecare.clients.Client;
import com.teo.servicecare.clients.ClientRepository;
import com.teo.servicecare.sites.dto.SiteCreateRequest;
import com.teo.servicecare.sites.dto.SiteResponse;
import com.teo.servicecare.sites.dto.SiteUpdateRequest;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Map;

@Service
public class SiteService {

  private final SiteRepository repo;
  private final ClientRepository clientRepo;
  private final UserRepository userRepo;

  public SiteService(SiteRepository repo, ClientRepository clientRepo, UserRepository userRepo) {
    this.repo = repo;
    this.clientRepo = clientRepo;
    this.userRepo = userRepo;
  }

  public Page<Site> list(Pageable pageable) {
    return repo.findAll(pageable);
  }

  public SiteResponse getVisibleTo(String email, Long id) {
    var site = repo.findById(id).orElseThrow();
    var current = userRepo.findByEmail(email).orElseThrow();

    if (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT) {
      return SiteResponse.from(site);
    }

    Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
    Long siteClientId = site.getClient() != null ? site.getClient().getId() : null;

    if (current.getRole() == User.Role.CLIENT && userClientId != null && userClientId.equals(siteClientId)) {
      return SiteResponse.from(site);
    }

    throw new org.springframework.security.access.AccessDeniedException("forbidden");
  }

  public SiteResponse create(SiteCreateRequest in) {
    Client client = clientRepo.findById(in.getClientId()).orElseThrow();

    var s = new Site();
    s.setName(in.getName());
    s.setUrl(in.getUrl());
    s.setEnvironment(in.getEnvironment() != null ? in.getEnvironment() : Site.SiteEnvironment.PROD);
    s.setType(in.getType() != null ? in.getType() : Site.SiteType.WEBSITE);
    s.setCms(in.getCms() != null ? in.getCms() : Site.SiteCms.CUSTOM);
    s.setStatus(in.getStatus() != null ? in.getStatus() : Site.SiteStatus.ACTIVE);
    s.setHostingProvider(in.getHostingProvider());
    s.setHostingPlan(in.getHostingPlan());
    s.setRepoUrl(in.getRepoUrl());
    s.setProdUrl(in.getProdUrl());
    s.setStagingUrl(in.getStagingUrl());
    s.setServerIp(in.getServerIp());
    s.setPhpVersion(in.getPhpVersion());
    s.setNodeVersion(in.getNodeVersion());
    s.setMysqlVersion(in.getMysqlVersion());
    s.setSslStatus(in.getSslStatus() != null ? in.getSslStatus() : Site.SiteSslStatus.UNKNOWN);
    s.setAnalyticsId(in.getAnalyticsId());
    s.setGtId(in.getGtId());
    s.setSentryDsn(in.getSentryDsn());
    s.setMaintenanceEnabled(in.getMaintenanceEnabled() != null ? in.getMaintenanceEnabled() : Boolean.TRUE);
    s.setMaintenanceEmail(in.getMaintenanceEmail());
    s.setLastMaintenanceAt(in.getLastMaintenanceAt());
    s.setNextMaintenanceAt(in.getNextMaintenanceAt());
    s.setLastBackupAt(in.getLastBackupAt());
    s.setNotes(in.getNotes());

    s.setClient(client);

    return SiteResponse.from(repo.save(s));
  }

  public SiteResponse update(Long id, SiteUpdateRequest in) {
    var s = repo.findById(id).orElseThrow();

    if (in.getName() != null)
      s.setName(in.getName());
    if (in.getUrl() != null)
      s.setUrl(in.getUrl());
    if (in.getEnvironment() != null)
      s.setEnvironment(in.getEnvironment());
    if (in.getType() != null)
      s.setType(in.getType());
    if (in.getCms() != null)
      s.setCms(in.getCms());
    if (in.getStatus() != null)
      s.setStatus(in.getStatus());
    if (in.getHostingProvider() != null)
      s.setHostingProvider(in.getHostingProvider());
    if (in.getHostingPlan() != null)
      s.setHostingPlan(in.getHostingPlan());
    if (in.getRepoUrl() != null)
      s.setRepoUrl(in.getRepoUrl());
    if (in.getProdUrl() != null)
      s.setProdUrl(in.getProdUrl());
    if (in.getStagingUrl() != null)
      s.setStagingUrl(in.getStagingUrl());
    if (in.getServerIp() != null)
      s.setServerIp(in.getServerIp());
    if (in.getPhpVersion() != null)
      s.setPhpVersion(in.getPhpVersion());
    if (in.getNodeVersion() != null)
      s.setNodeVersion(in.getNodeVersion());
    if (in.getMysqlVersion() != null)
      s.setMysqlVersion(in.getMysqlVersion());
    if (in.getSslStatus() != null)
      s.setSslStatus(in.getSslStatus());
    if (in.getAnalyticsId() != null)
      s.setAnalyticsId(in.getAnalyticsId());
    if (in.getGtId() != null)
      s.setGtId(in.getGtId());
    if (in.getSentryDsn() != null)
      s.setSentryDsn(in.getSentryDsn());
    if (in.getMaintenanceEnabled() != null)
      s.setMaintenanceEnabled(in.getMaintenanceEnabled());
    if (in.getMaintenanceEmail() != null)
      s.setMaintenanceEmail(in.getMaintenanceEmail());
    if (in.getLastMaintenanceAt() != null)
      s.setLastMaintenanceAt(in.getLastMaintenanceAt());
    if (in.getNextMaintenanceAt() != null)
      s.setNextMaintenanceAt(in.getNextMaintenanceAt());
    if (in.getLastBackupAt() != null)
      s.setLastBackupAt(in.getLastBackupAt());
    if (in.getNotes() != null)
      s.setNotes(in.getNotes());

    if (in.getClientId() != null) {
      Client newClient = clientRepo.findById(in.getClientId()).orElseThrow();
      s.setClient(newClient);
    }

    return SiteResponse.from(repo.save(s));
  }

  public void delete(Long id) {
    if (!repo.existsById(id))
      throw new IllegalArgumentException("site_not_found");
    repo.deleteById(id);
  }

  public Page<Map<String, Object>> lookup(String email, Long clientId, String q, Pageable pageable) {
    var current = userRepo.findByEmail(email).orElseThrow();

    if (current.getRole() == User.Role.CLIENT) {
      Long cid = (current.getClient() != null) ? current.getClient().getId() : -1L;
      if (!cid.equals(clientId)) {
        throw new org.springframework.security.access.AccessDeniedException("forbidden");
      }
    }

    Specification<Site> spec = (r, qb, cb) -> cb.equal(r.get("client").get("id"), clientId);

    if (q != null && !q.isBlank()) {
      String like = "%" + q.trim().toLowerCase() + "%";
      spec = spec.and((r, qb, cb) -> cb.like(cb.lower(r.get("name")), like));
    }

    return repo.findAll(spec, pageable).map(s -> Map.of("id", s.getId(), "name", s.getName(), "url", s.getUrl()));
  }

  public Page<Site> search(String email,
      Long clientId,
      Site.SiteEnvironment environment,
      Site.SiteType type,
      Site.SiteCms cms,
      Site.SiteStatus status,
      String q,
      String createdFrom,
      String createdTo,
      Pageable pageable) {
    var current = userRepo.findByEmail(email).orElseThrow();

    Specification<Site> spec = (r, qb, cb) -> cb.conjunction();

    if (current.getRole() == User.Role.CLIENT) {
      Long scopedClientId = (current.getClient() != null) ? current.getClient().getId() : -1L;
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("client").get("id"), scopedClientId));
    } else {
      if (clientId != null) {
        spec = spec.and((r, qb, cb) -> cb.equal(r.get("client").get("id"), clientId));
      }
    }

    if (environment != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("environment"), environment));
    if (type != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("type"), type));
    if (cms != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("cms"), cms));
    if (status != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("status"), status));

    if (q != null && !q.isBlank()) {
      String like = "%" + q.trim().toLowerCase() + "%";
      spec = spec.and((r, qb, cb) -> cb.or(
          cb.like(cb.lower(r.get("name")), like),
          cb.like(cb.lower(r.get("url")), like),
          cb.like(cb.lower(r.get("hostingProvider")), like)));
    }

    var tz = ZoneId.of("Europe/Paris");
    if (createdFrom != null && !createdFrom.isBlank()) {
      var from = LocalDate.parse(createdFrom).atStartOfDay(tz).toInstant();
      spec = spec.and((r, qb, cb) -> cb.greaterThanOrEqualTo(r.get("createdAt"), from));
    }
    if (createdTo != null && !createdTo.isBlank()) {
      var toExcl = LocalDate.parse(createdTo).plusDays(1).atStartOfDay(tz).toInstant();
      spec = spec.and((r, qb, cb) -> cb.lessThan(r.get("createdAt"), toExcl));
    }

    return repo.findAll(spec, pageable);
  }
}
