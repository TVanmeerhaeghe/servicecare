package com.teo.servicecare.tickets.intervention;

import com.teo.servicecare.tickets.Ticket;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/interventions")
public class InterventionController {

  private static final ZoneId APP_ZONE = ZoneId.of("Europe/Paris");

  private final InterventionRepository repo;
  private final TicketRepository ticketRepo;
  private final UserRepository userRepo;

  public InterventionController(InterventionRepository repo,
                                TicketRepository ticketRepo,
                                UserRepository userRepo) {
    this.repo = repo;
    this.ticketRepo = ticketRepo;
    this.userRepo = userRepo;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public Page<InterventionResponse> list(
      @RequestParam Long ticketId,
      @AuthenticationPrincipal UserDetails principal,
      @PageableDefault(size = 20, sort = "scheduledStart", direction = Sort.Direction.ASC) Pageable pageable
  ) {
    var t = ticketRepo.findById(ticketId).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    if (current.getRole() == User.Role.CLIENT) {
      Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
      if (userClientId == null || !userClientId.equals(t.getClientId()))
        throw new org.springframework.security.access.AccessDeniedException("forbidden");
    }

    Specification<Intervention> spec = (r,q,cb) -> cb.and(
        cb.equal(r.get("ticketId"), ticketId),
        cb.isNull(r.get("deletedAt"))
    );

    return repo.findAll(spec, pageable).map(InterventionResponse::from);
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public InterventionResponse get(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
    var i = repo.findById(id).orElseThrow();
    var t = ticketRepo.findById(i.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    if (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT) {
      return InterventionResponse.from(i);
    }

    Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
    if (current.getRole() == User.Role.CLIENT && userClientId != null && userClientId.equals(t.getClientId())) {
      return InterventionResponse.from(i);
    }

    throw new org.springframework.security.access.AccessDeniedException("forbidden");
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
  public InterventionResponse create(@RequestBody @jakarta.validation.Valid InterventionCreateRequest in) {
    var t = ticketRepo.findById(in.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    var i = new Intervention();
    i.setTicketId(t.getId());
    i.setType(in.getType() != null ? in.getType() : Intervention.Type.REMOTE);
    i.setTechnicianUserId(in.getTechnicianUserId());
    i.setTitle(in.getTitle());
    i.setNotes(in.getNotes());
    i.setScheduledStart(in.getScheduledStart());
    i.setScheduledEnd(in.getScheduledEnd());
    i.setStatus(Intervention.Status.PLANNED);
    i.setDeletedAt(null);

    return InterventionResponse.from(repo.save(i));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
  public InterventionResponse update(@PathVariable Long id, @RequestBody InterventionUpdateRequest in) {
    var i = repo.findById(id).orElseThrow();
    var t = ticketRepo.findById(i.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    if (in.getType() != null) i.setType(in.getType());
    if (in.getTechnicianUserId() != null) i.setTechnicianUserId(in.getTechnicianUserId());
    if (in.getTitle() != null) i.setTitle(in.getTitle());
    if (in.getNotes() != null) i.setNotes(in.getNotes());
    if (in.getReport() != null) i.setReport(in.getReport());
    if (in.getScheduledStart() != null) i.setScheduledStart(in.getScheduledStart());
    if (in.getScheduledEnd() != null) i.setScheduledEnd(in.getScheduledEnd());
    if (in.getTravelMinutes() != null) i.setTravelMinutes(in.getTravelMinutes());
    if (in.getWorkMinutes() != null) i.setWorkMinutes(in.getWorkMinutes());
    if (in.getActualStart() != null) i.setActualStart(in.getActualStart());
    if (in.getActualEnd() != null) i.setActualEnd(in.getActualEnd());

    return InterventionResponse.from(repo.save(i));
  }

  @PostMapping("/{id}/transition")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
  public InterventionResponse transition(@PathVariable Long id, @RequestParam String action) {
    var i = repo.findById(id).orElseThrow();
    var t = ticketRepo.findById(i.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    var now = LocalDateTime.now(APP_ZONE);

    switch (action) {
      case "START" -> {
        i.setStatus(Intervention.Status.IN_PROGRESS);
        if (i.getActualStart() == null) i.setActualStart(now);
        if (t.getStatus() == Ticket.TicketStatus.OPEN || t.getStatus() == Ticket.TicketStatus.ASSIGNED || t.getStatus() == Ticket.TicketStatus.WAITING) {
          t.setStatus(Ticket.TicketStatus.IN_PROGRESS);
          if (t.getRespondedAt() == null) t.setRespondedAt(now);
          ticketRepo.save(t);
        }
      }
      case "DONE" -> {
        i.setStatus(Intervention.Status.DONE);
        if (i.getActualEnd() == null) i.setActualEnd(now);
        if (i.getWorkMinutes() == null || i.getWorkMinutes() <= 0) {
          if (i.getActualStart() != null && i.getActualEnd() != null && i.getActualEnd().isAfter(i.getActualStart())) {
            long mins = Duration.between(i.getActualStart(), i.getActualEnd()).toMinutes();
            i.setWorkMinutes((int) Math.max(0, mins));
          }
        }
      }
      case "CANCEL" -> i.setStatus(Intervention.Status.CANCELED);
      case "NO_SHOW" -> i.setStatus(Intervention.Status.NO_SHOW);
      default -> throw new IllegalArgumentException("unknown_action");
    }

    return InterventionResponse.from(repo.save(i));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    var i = repo.findById(id).orElseThrow();
    if (i.getDeletedAt() != null) return;
    i.setDeletedAt(LocalDateTime.now(APP_ZONE));
    repo.save(i);
  }
}
