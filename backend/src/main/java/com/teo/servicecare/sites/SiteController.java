package com.teo.servicecare.sites;

import com.teo.servicecare.common.dto.PageResponse;
import com.teo.servicecare.sites.dto.SiteCreateRequest;
import com.teo.servicecare.sites.dto.SiteResponse;
import com.teo.servicecare.sites.dto.SiteUpdateRequest;
import com.teo.servicecare.sites.dto.SiteLightResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sites")
public class SiteController {

  private final SiteService service;

  public SiteController(SiteService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public PageResponse<SiteResponse> all(
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    var page = service.list(pageable).map(SiteResponse::from);
    return PageResponse.from(page);
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public SiteResponse one(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
    return service.getVisibleTo(principal.getUsername(), id);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public SiteResponse create(@RequestBody @Valid SiteCreateRequest in) {
    return service.create(in);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public SiteResponse update(@PathVariable Long id, @RequestBody SiteUpdateRequest in) {
    return service.update(id, in);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @GetMapping("/lookup")
  @PreAuthorize("isAuthenticated()")
  public PageResponse<Map<String, Object>> lookupSites(
      @RequestParam Long clientId,
      @RequestParam(required = false) String q,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
    var page = service.lookup(principal.getUsername(), clientId, q, pageable);
    return PageResponse.from(page);
  }

  @GetMapping("/search")
  @PreAuthorize("isAuthenticated()")
  public PageResponse<SiteResponse> search(
      @RequestParam(required = false) Long clientId,
      @RequestParam(required = false) Site.SiteEnvironment environment,
      @RequestParam(required = false) Site.SiteType type,
      @RequestParam(required = false) Site.SiteCms cms,
      @RequestParam(required = false) Site.SiteStatus status,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String createdFrom,
      @RequestParam(required = false) String createdTo,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    var page = service.search(
        principal.getUsername(), clientId, environment, type, cms, status, q, createdFrom, createdTo, pageable)
        .map(SiteResponse::from);
    return PageResponse.from(page);
  }

  @GetMapping("/by-client/{clientId}")
  @PreAuthorize("isAuthenticated()")
  public java.util.List<SiteLightResponse> sitesByClient(
      @PathVariable Long clientId,
      @AuthenticationPrincipal User principal) {
    if (principal == null)
      throw new org.springframework.security.access.AccessDeniedException("unauthorized");
    return service.listForClient(principal.getUsername(), clientId);
  }
}
