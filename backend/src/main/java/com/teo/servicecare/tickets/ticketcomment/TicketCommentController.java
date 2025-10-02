package com.teo.servicecare.tickets.ticketcomment;

import com.teo.servicecare.tickets.TicketRepository;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ticket-comments")
public class TicketCommentController {

  private static final java.time.ZoneId APP_ZONE = java.time.ZoneId.of("Europe/Paris");

  private final TicketRepository ticketRepo;
  private final TicketCommentRepository repo;
  private final UserRepository userRepo;

  public TicketCommentController(TicketRepository ticketRepo, TicketCommentRepository repo, UserRepository userRepo) {
    this.ticketRepo = ticketRepo;
    this.repo = repo;
    this.userRepo = userRepo;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public Page<TicketCommentResponse> list(
      @RequestParam Long ticketId,
      @AuthenticationPrincipal UserDetails principal,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
      Pageable pageable
  ) {
    var t = ticketRepo.findById(ticketId).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    if (current.getRole() == User.Role.CLIENT) {
      Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
      if (userClientId == null || !userClientId.equals(t.getClientId()))
        throw new org.springframework.security.access.AccessDeniedException("forbidden");
    }

    Specification<TicketComment> spec = (r,q,cb) -> cb.and(
        cb.equal(r.get("ticketId"), ticketId),
        cb.isNull(r.get("deletedAt"))
    );

    if (current.getRole() == User.Role.CLIENT) {
      spec = spec.and((r,q,cb) -> cb.isFalse(r.get("internalOnly")));
    }

    return repo.findAll(spec, pageable).map(TicketCommentResponse::from);
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public TicketCommentResponse create(@RequestBody @jakarta.validation.Valid TicketCommentCreateRequest in,
                                      @AuthenticationPrincipal UserDetails principal) {
    var t = ticketRepo.findById(in.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    if (current.getRole() == User.Role.CLIENT) {
      Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
      if (userClientId == null || !userClientId.equals(t.getClientId()))
        throw new org.springframework.security.access.AccessDeniedException("forbidden");
    }

    boolean internal = Boolean.TRUE.equals(in.getInternalOnly()) &&
        (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT);

    var c = new TicketComment();
    c.setTicketId(t.getId());
    c.setAuthorUserId(current.getId());
    String display = (current.getFirstName() != null ? current.getFirstName() : "")
        + " " + (current.getLastName() != null ? current.getLastName() : "");
    c.setAuthorName(display.trim().isEmpty() ? current.getEmail() : display.trim());
    c.setBody(in.getBody());
    c.setInternalOnly(internal);
    c.setDeletedAt(null);

    return TicketCommentResponse.from(repo.save(c));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
    var c = repo.findById(id).orElseThrow();
    var t = ticketRepo.findById(c.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    boolean canDelete = current.getRole() == User.Role.ADMIN
        || current.getRole() == User.Role.AGENT
        || c.getAuthorUserId().equals(current.getId());

    if (!canDelete) throw new org.springframework.security.access.AccessDeniedException("forbidden");

    if (c.getDeletedAt() != null) return;
    c.setDeletedAt(java.time.LocalDateTime.now(APP_ZONE));
    repo.save(c);
  }
}
