package com.teo.servicecare.tickets;

import com.teo.servicecare.common.dto.PageResponse;
import com.teo.servicecare.tickets.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

  private final TicketService service;

  public TicketController(TicketService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public PageResponse<TicketResponse> all(
      @AuthenticationPrincipal UserDetails principal,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    var page = service.listVisible(principal.getUsername(), pageable).map(TicketResponse::from);
    return PageResponse.from(page);
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public TicketResponse get(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
    return TicketResponse.from(service.getVisible(principal.getUsername(), id));
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','CLIENT')")
  public TicketResponse create(@RequestBody @jakarta.validation.Valid TicketCreateRequest in,
      @AuthenticationPrincipal UserDetails principal) {
    return TicketResponse.from(service.create(principal.getUsername(), in));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','CLIENT')")
  public TicketResponse update(@PathVariable Long id,
      @RequestBody TicketUpdateRequest in,
      @AuthenticationPrincipal UserDetails principal) {
    return TicketResponse.from(service.update(principal.getUsername(), id, in));
  }

  @PostMapping("/{id}/transition")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public TicketResponse transition(@PathVariable Long id, @RequestParam String action) {
    return TicketResponse.from(service.transition(id, action));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    service.softDelete(id);
  }

  @PostMapping("/{id}/restore")
  @PreAuthorize("hasRole('ADMIN')")
  public TicketResponse restore(@PathVariable Long id) {
    return TicketResponse.from(service.restore(id));
  }

  @GetMapping("/search")
  @PreAuthorize("isAuthenticated()")
  public PageResponse<TicketResponse> search(
      @RequestParam(required = false) Long clientId,
      @RequestParam(required = false) Long siteId,
      @RequestParam(required = false) Long contractId,
      @RequestParam(required = false) Long assigneeUserId,
      @RequestParam(required = false) Ticket.TicketStatus status,
      @RequestParam(required = false) Ticket.TicketPriority priority,
      @RequestParam(required = false) Boolean slaBreached,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String createdFrom,
      @RequestParam(required = false) String createdTo,
      @RequestParam(required = false) String updatedFrom,
      @RequestParam(required = false) String updatedTo,
      @RequestParam(required = false) String respondByBefore,
      @RequestParam(required = false) String resolveByBefore,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    var page = service.search(
        principal.getUsername(),
        clientId, siteId, contractId, assigneeUserId,
        status, priority, slaBreached,
        q, createdFrom, createdTo, updatedFrom, updatedTo, respondByBefore, resolveByBefore,
        pageable).map(TicketResponse::from);
    return PageResponse.from(page);
  }

  @GetMapping("/{id}/sla")
  public com.teo.servicecare.tickets.dto.TicketSlaResponse getSla(@PathVariable Long id) {
    return service.getSla(id);
  }
}
