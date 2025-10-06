package com.teo.servicecare.tickets.intervention;

import com.teo.servicecare.common.dto.PageResponse;
import com.teo.servicecare.tickets.intervention.dto.InterventionCreateRequest;
import com.teo.servicecare.tickets.intervention.dto.InterventionResponse;
import com.teo.servicecare.tickets.intervention.dto.InterventionUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interventions")
public class InterventionController {

  private final InterventionService service;

  public InterventionController(InterventionService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public PageResponse<InterventionResponse> list(
      @RequestParam Long ticketId,
      @AuthenticationPrincipal UserDetails principal,
      @PageableDefault(size = 20, sort = "scheduledStart", direction = Sort.Direction.ASC) Pageable pageable) {
    var page = service.list(ticketId, principal.getUsername(), pageable);
    return PageResponse.from(page);
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public InterventionResponse get(@PathVariable Long id,
      @AuthenticationPrincipal UserDetails principal) {
    return service.get(id, principal.getUsername());
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public InterventionResponse create(@RequestBody @Valid InterventionCreateRequest in,
      @AuthenticationPrincipal UserDetails principal) {
    return service.create(in, principal.getUsername());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public InterventionResponse update(@PathVariable Long id,
      @RequestBody InterventionUpdateRequest in,
      @AuthenticationPrincipal UserDetails principal) {
    return service.update(id, in, principal.getUsername());
  }

  @PostMapping("/{id}/transition")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public InterventionResponse transition(@PathVariable Long id,
      @RequestParam String action,
      @AuthenticationPrincipal UserDetails principal) {
    return service.transition(id, action, principal.getUsername());
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id,
      @AuthenticationPrincipal UserDetails principal) {
    service.delete(id, principal.getUsername());
  }
}
