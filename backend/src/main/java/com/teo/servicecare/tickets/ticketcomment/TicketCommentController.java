package com.teo.servicecare.tickets.ticketcomment;

import com.teo.servicecare.common.dto.PageResponse;
import com.teo.servicecare.tickets.ticketcomment.dto.TicketCommentCreateRequest;
import com.teo.servicecare.tickets.ticketcomment.dto.TicketCommentResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ticket-comments")
public class TicketCommentController {

  private final TicketCommentService service;

  public TicketCommentController(TicketCommentService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public PageResponse<TicketCommentResponse> list(
      @RequestParam Long ticketId,
      @AuthenticationPrincipal UserDetails principal,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
      Pageable pageable
  ) {
    var page = service.list(ticketId, principal.getUsername(), pageable);
    return PageResponse.from(page);
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public TicketCommentResponse create(@RequestBody @Valid TicketCommentCreateRequest in,
                                      @AuthenticationPrincipal UserDetails principal) {
    return service.create(in, principal.getUsername());
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
    service.delete(id, principal.getUsername());
  }
}
