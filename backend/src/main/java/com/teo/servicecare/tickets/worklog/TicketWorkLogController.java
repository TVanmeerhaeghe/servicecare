package com.teo.servicecare.tickets.worklog;

import com.teo.servicecare.tickets.TicketRepository;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.*;

@RestController
@RequestMapping("/api/ticket-work-logs")
public class TicketWorkLogController {

  private static final ZoneId APP_ZONE = ZoneId.of("Europe/Paris");

  private final TicketWorkLogRepository repo;
  private final TicketRepository ticketRepo;
  private final UserRepository userRepo;

  public TicketWorkLogController(TicketWorkLogRepository repo,
                                 TicketRepository ticketRepo,
                                 UserRepository userRepo) {
    this.repo = repo;
    this.ticketRepo = ticketRepo;
    this.userRepo = userRepo;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public Page<TicketWorkLogResponse> list(@RequestParam Long ticketId,
                                          @AuthenticationPrincipal UserDetails principal,
                                          @PageableDefault(size = 20, sort = "startedAt", direction = Sort.Direction.DESC) Pageable pageable) {
    var t = ticketRepo.findById(ticketId).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    if (current.getRole() == User.Role.CLIENT) {
      Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
      if (userClientId == null || !userClientId.equals(t.getClientId()))
        throw new org.springframework.security.access.AccessDeniedException("forbidden");
    }

    Specification<TicketWorkLog> spec = (r,q,cb) -> cb.and(
        cb.equal(r.get("ticketId"), ticketId),
        cb.isNull(r.get("deletedAt"))
    );

    return repo.findAll(spec, pageable).map(TicketWorkLogResponse::from);
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public TicketWorkLogResponse get(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails principal) {
    var w = repo.findById(id).orElseThrow();
    var t = ticketRepo.findById(w.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    if (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT || current.getRole() == User.Role.TECHNICIAN) {
      return TicketWorkLogResponse.from(w);
    }

    Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
    if (current.getRole() == User.Role.CLIENT && userClientId != null && userClientId.equals(t.getClientId())) {
      return TicketWorkLogResponse.from(w);
    }

    throw new org.springframework.security.access.AccessDeniedException("forbidden");
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public TicketWorkLogResponse create(@RequestBody @Valid TicketWorkLogCreateRequest in,
                                      @AuthenticationPrincipal UserDetails principal) {
    var t = ticketRepo.findById(in.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    var w = new TicketWorkLog();
    w.setTicketId(t.getId());
    w.setUserId(in.getUserId() != null ? in.getUserId() : current.getId());

    LocalDateTime started = in.getStartedAt() != null ? in.getStartedAt() : LocalDateTime.now(APP_ZONE);
    w.setStartedAt(started);
    w.setEndedAt(in.getEndedAt());
    w.setNote(in.getNote());

    if (in.getMinutes() != null) {
      w.setMinutes(Math.max(0, in.getMinutes()));
    } else if (in.getEndedAt() != null) {
      long mins = Duration.between(started, in.getEndedAt()).toMinutes();
      w.setMinutes((int) Math.max(0, mins));
    } else {
      w.setMinutes(null);
    }

    w.setDeletedAt(null);
    return TicketWorkLogResponse.from(repo.save(w));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public TicketWorkLogResponse update(@PathVariable Long id,
                                      @RequestBody TicketWorkLogUpdateRequest in,
                                      @AuthenticationPrincipal UserDetails principal) {
    var w = repo.findById(id).orElseThrow();
    var t = ticketRepo.findById(w.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    if (in.getStartedAt() != null) w.setStartedAt(in.getStartedAt());
    if (in.getEndedAt() != null) w.setEndedAt(in.getEndedAt());
    if (in.getNote() != null) w.setNote(in.getNote());

    if (in.getMinutes() != null) {
      w.setMinutes(Math.max(0, in.getMinutes()));
    } else if (in.getEndedAt() != null && w.getStartedAt() != null) {
      long mins = Duration.between(w.getStartedAt(), in.getEndedAt()).toMinutes();
      w.setMinutes((int) Math.max(0, mins));
    }

    return TicketWorkLogResponse.from(repo.save(w));
  }

  @PostMapping("/start")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public TicketWorkLogResponse start(@RequestParam Long ticketId,
                                     @RequestParam(required = false) String note,
                                     @AuthenticationPrincipal UserDetails principal) {
    var t = ticketRepo.findById(ticketId).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    Specification<TicketWorkLog> activeSpec = (r,q,cb) -> cb.and(
        cb.isNull(r.get("deletedAt")),
        cb.isNull(r.get("endedAt")),
        cb.equal(r.get("userId"), current.getId())
    );
    boolean hasActive = repo.findAll(activeSpec, PageRequest.of(0,1)).hasContent();
    if (hasActive) throw new IllegalStateException("active_log_exists");

    var w = new TicketWorkLog();
    w.setTicketId(t.getId());
    w.setUserId(current.getId());
    w.setStartedAt(LocalDateTime.now(APP_ZONE));
    w.setEndedAt(null);
    w.setMinutes(null);
    w.setNote(note);
    w.setDeletedAt(null);

    return TicketWorkLogResponse.from(repo.save(w));
  }

  @PostMapping("/stop")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public TicketWorkLogResponse stop(@RequestParam(required = false) Long ticketId,
                                    @AuthenticationPrincipal UserDetails principal) {
    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    Specification<TicketWorkLog> activeSpec = (r,q,cb) -> {
      var p = cb.and(cb.isNull(r.get("deletedAt")),
                     cb.isNull(r.get("endedAt")),
                     cb.equal(r.get("userId"), current.getId()));
      if (ticketId != null) {
        p = cb.and(p, cb.equal(r.get("ticketId"), ticketId));
      }
      return p;
    };
    var page = repo.findAll(activeSpec, PageRequest.of(0,1));
    if (!page.hasContent()) throw new IllegalStateException("no_active_log");

    var w = page.getContent().get(0);
    var now = LocalDateTime.now(APP_ZONE);
    w.setEndedAt(now);
    if (w.getStartedAt() != null) {
      long mins = Duration.between(w.getStartedAt(), now).toMinutes();
      w.setMinutes((int) Math.max(0, mins));
    } else if (w.getMinutes() == null) {
      w.setMinutes(0);
    }

    return TicketWorkLogResponse.from(repo.save(w));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public void delete(@PathVariable Long id) {
    var w = repo.findById(id).orElseThrow();
    if (w.getDeletedAt() != null) return;
    w.setDeletedAt(LocalDateTime.now(APP_ZONE));
    repo.save(w);
  }
}
