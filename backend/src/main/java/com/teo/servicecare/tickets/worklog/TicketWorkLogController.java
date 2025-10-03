package com.teo.servicecare.tickets.worklog;

import com.teo.servicecare.common.dto.PageResponse;
import com.teo.servicecare.tickets.worklog.dto.TicketWorkLogCreateRequest;
import com.teo.servicecare.tickets.worklog.dto.TicketWorkLogResponse;
import com.teo.servicecare.tickets.worklog.dto.TicketWorkLogUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ticket-work-logs")
public class TicketWorkLogController {

  private final TicketWorkLogService service;

  public TicketWorkLogController(TicketWorkLogService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public PageResponse<TicketWorkLogResponse> list(@RequestParam Long ticketId,
                                                  @AuthenticationPrincipal UserDetails principal,
                                                  @PageableDefault(size = 20, sort = "startedAt", direction = Sort.Direction.DESC)
                                                  Pageable pageable) {
    var page = service.list(ticketId, principal.getUsername(), pageable);
    return PageResponse.from(page);
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public TicketWorkLogResponse get(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails principal) {
    return service.get(id, principal.getUsername());
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public TicketWorkLogResponse create(@RequestBody @Valid TicketWorkLogCreateRequest in,
                                      @AuthenticationPrincipal UserDetails principal) {
    return service.create(in, principal.getUsername());
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public TicketWorkLogResponse update(@PathVariable Long id,
                                      @RequestBody TicketWorkLogUpdateRequest in,
                                      @AuthenticationPrincipal UserDetails principal) {
    return service.update(id, in, principal.getUsername());
  }

  @PostMapping("/start")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public TicketWorkLogResponse start(@RequestParam Long ticketId,
                                     @RequestParam(required = false) String note,
                                     @AuthenticationPrincipal UserDetails principal) {
    return service.start(ticketId, note, principal.getUsername());
  }

  @PostMapping("/stop")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public TicketWorkLogResponse stop(@RequestParam(required = false) Long ticketId,
                                    @AuthenticationPrincipal UserDetails principal) {
    return service.stop(ticketId, principal.getUsername());
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public void delete(@PathVariable Long id,
                     @AuthenticationPrincipal UserDetails principal) {
    service.delete(id, principal.getUsername());
  }
}
