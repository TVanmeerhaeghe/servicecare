package com.teo.servicecare.sites;

import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import com.teo.servicecare.clients.Client;
import com.teo.servicecare.clients.ClientRepository;

import com.teo.servicecare.sites.Site.SiteCms;
import com.teo.servicecare.sites.Site.SiteEnvironment;
import com.teo.servicecare.sites.Site.SiteStatus;
import com.teo.servicecare.sites.Site.SiteType;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sites")
public class SiteController {
  private final SiteRepository repo;
  private final ClientRepository clientRepo;
  private final UserRepository userRepo;

  public SiteController(SiteRepository repo, ClientRepository clientRepo, UserRepository userRepo) {
    this.repo = repo;
    this.clientRepo = clientRepo;
    this.userRepo = userRepo;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public org.springframework.data.domain.Page<Site> all(
      @org.springframework.data.web.PageableDefault(size = 20, sort = "id",
          direction = org.springframework.data.domain.Sort.Direction.DESC)
      org.springframework.data.domain.Pageable pageable) {
    return repo.findAll(pageable);
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public SiteResponse one(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
    Site site = repo.findById(id).orElseThrow();

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

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

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
  public SiteResponse create(@RequestBody @Valid SiteCreateRequest in) {
    Client client = clientRepo.findById(in.getClientId()).orElseThrow();

    var s = new Site();
    s.setName(in.getName());
    s.setUrl(in.getUrl());
    s.setEnvironment(in.getEnvironment() != null ? in.getEnvironment() : SiteEnvironment.PROD);
    s.setType(in.getType() != null ? in.getType() : SiteType.WEBSITE);
    s.setCms(in.getCms() != null ? in.getCms() : SiteCms.CUSTOM);
    s.setStatus(in.getStatus() != null ? in.getStatus() : SiteStatus.ACTIVE);
    s.setHostingProvider(in.getHostingProvider());
    s.setClient(client);

    return SiteResponse.from(repo.save(s));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
  public SiteResponse update(@PathVariable Long id, @RequestBody SiteUpdateRequest in) {
    Site s = repo.findById(id).orElseThrow();

    if (in.getName() != null) s.setName(in.getName());
    if (in.getUrl() != null) s.setUrl(in.getUrl());
    if (in.getEnvironment() != null) s.setEnvironment(in.getEnvironment());
    if (in.getType() != null) s.setType(in.getType());
    if (in.getCms() != null) s.setCms(in.getCms());
    if (in.getStatus() != null) s.setStatus(in.getStatus());
    if (in.getHostingProvider() != null) s.setHostingProvider(in.getHostingProvider());

    if (in.getClientId() != null) {
      Client newClient = clientRepo.findById(in.getClientId()).orElseThrow();
      s.setClient(newClient);
    }

    return SiteResponse.from(repo.save(s));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
  public void delete(@PathVariable Long id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("site_not_found");
    repo.deleteById(id);
  }
}
