package com.teo.servicecare.tickets.thread;

import com.teo.servicecare.tickets.thread.dto.ThreadEventResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketThreadController {

  private final TicketThreadService service;

  public TicketThreadController(TicketThreadService service) {
    this.service = service;
  }

  @GetMapping("/{ticketId}/thread")
  @PreAuthorize("isAuthenticated()")
  public List<ThreadEventResponse> timeline(@PathVariable Long ticketId,
      @AuthenticationPrincipal UserDetails principal) {
    return service.getTimeline(principal.getUsername(), ticketId);
  }
}
