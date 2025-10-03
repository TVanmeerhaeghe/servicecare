package com.teo.servicecare.tickets.ticketcomment;

import com.teo.servicecare.tickets.TicketRepository;
import com.teo.servicecare.tickets.ticketcomment.dto.TicketCommentCreateRequest;
import com.teo.servicecare.tickets.ticketcomment.dto.TicketCommentResponse;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class TicketCommentService {

  private static final java.time.ZoneId APP_ZONE = java.time.ZoneId.of("Europe/Paris");

  private final TicketRepository ticketRepo;
  private final TicketCommentRepository repo;
  private final UserRepository userRepo;

  public TicketCommentService(TicketRepository ticketRepo,
                              TicketCommentRepository repo,
                              UserRepository userRepo) {
    this.ticketRepo = ticketRepo;
    this.repo = repo;
    this.userRepo = userRepo;
  }

  public Page<TicketCommentResponse> list(Long ticketId, String username, Pageable pageable) {
    var t = ticketRepo.findById(ticketId).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(username).orElseThrow();

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

  public TicketCommentResponse create(TicketCommentCreateRequest in, String username) {
    var t = ticketRepo.findById(in.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(username).orElseThrow();

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

  public void delete(Long id, String username) {
    var c = repo.findById(id).orElseThrow();
    var t = ticketRepo.findById(c.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(username).orElseThrow();

    boolean canDelete = current.getRole() == User.Role.ADMIN
        || current.getRole() == User.Role.AGENT
        || c.getAuthorUserId().equals(current.getId());

    if (!canDelete) throw new org.springframework.security.access.AccessDeniedException("forbidden");

    if (c.getDeletedAt() != null) return;
    c.setDeletedAt(java.time.LocalDateTime.now(APP_ZONE));
    repo.save(c);
  }
}
