package com.teo.servicecare.sites;

import com.teo.servicecare.clients.Client;
import com.teo.servicecare.clients.ClientRepository;
import com.teo.servicecare.sites.Site.SiteCms;
import com.teo.servicecare.sites.Site.SiteEnvironment;
import com.teo.servicecare.sites.Site.SiteStatus;
import com.teo.servicecare.sites.Site.SiteType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sites")
public class SiteController {
  private final SiteRepository repo;
  private final ClientRepository clientRepo;

  public SiteController(SiteRepository repo, ClientRepository clientRepo) {
    this.repo = repo;
    this.clientRepo = clientRepo;
  }

  @GetMapping
  public List<Site> all() { return repo.findAll(); }

  @GetMapping("/{id}")
  public Site one(@PathVariable Long id) { return repo.findById(id).orElseThrow(); }

  @PostMapping
  public Site create(@RequestBody @Valid SiteCreateRequest in) {
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
    return repo.save(s);
  }
}
