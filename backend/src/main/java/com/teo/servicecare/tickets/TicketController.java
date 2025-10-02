package com.teo.servicecare.tickets;

import com.teo.servicecare.contracts.Contract;
import com.teo.servicecare.contracts.ContractRepository;
import com.teo.servicecare.tickets.intervention.InterventionRepository;
import com.teo.servicecare.tickets.ticketcomment.ThreadEventResponse;
import com.teo.servicecare.tickets.ticketcomment.TicketComment;
import com.teo.servicecare.tickets.ticketcomment.TicketCommentRepository;
import com.teo.servicecare.tickets.ticketcomment.TicketCommentResponse;
import com.teo.servicecare.tickets.ticketcomment.TicketThreadResponse;
import com.teo.servicecare.tickets.intervention.Intervention;
import com.teo.servicecare.tickets.intervention.InterventionResponse;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
  private final TicketRepository repo;
  private final UserRepository userRepo;
  private final ContractRepository contractRepo;
  private final TicketCommentRepository commentRepo;
  private final InterventionRepository interventionRepo;

  public TicketController(TicketRepository repo, UserRepository userRepo, ContractRepository contractRepo, TicketCommentRepository commentRepo, InterventionRepository interventionRepo) {
    this.repo = repo;
    this.userRepo = userRepo;
    this.contractRepo = contractRepo;
    this.commentRepo = commentRepo; 
    this.interventionRepo = interventionRepo;
  }

  private static final ZoneId APP_ZONE = ZoneId.of("Europe/Paris");

  private static final java.util.Map<Ticket.TicketStatus, java.util.Set<String>> ALLOWED =
      java.util.Map.of(
          Ticket.TicketStatus.OPEN,        java.util.Set.of("ASSIGN", "START", "CANCEL"),
          Ticket.TicketStatus.ASSIGNED,    java.util.Set.of("START", "WAIT", "CANCEL"),
          Ticket.TicketStatus.IN_PROGRESS, java.util.Set.of("WAIT", "CLOSE", "CANCEL"),
          Ticket.TicketStatus.WAITING,     java.util.Set.of("RESUME", "CANCEL"),
          Ticket.TicketStatus.CLOSED,      java.util.Set.of(),
          Ticket.TicketStatus.CANCELED,    java.util.Set.of()
      );

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public TicketResponse get(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();
    if (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT) {
      return TicketResponse.from(t);
    }
    Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
    if (current.getRole() == User.Role.CLIENT && userClientId != null && userClientId.equals(t.getClientId())) {
      return TicketResponse.from(t);
    }
    throw new org.springframework.security.access.AccessDeniedException("forbidden");
  }

  @GetMapping("/{id}/thread")
  @PreAuthorize("isAuthenticated()")
  public TicketThreadResponse thread(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails principal,
                                     @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.ASC)
                                     Pageable pageable) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    if (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT) {
    } else {
      Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
      if (!(current.getRole() == User.Role.CLIENT && userClientId != null && userClientId.equals(t.getClientId()))) {
        throw new org.springframework.security.access.AccessDeniedException("forbidden");
      }
    }

    Specification<com.teo.servicecare.tickets.ticketcomment.TicketComment> spec =
        (r,q,cb) -> cb.and(cb.equal(r.get("ticketId"), id), cb.isNull(r.get("deletedAt")));

    if (current.getRole() == User.Role.CLIENT) {
      spec = spec.and((r,q,cb) -> cb.isFalse(r.get("internalOnly")));
    }

    Page<TicketCommentResponse> commentsPage =
        commentRepo.findAll(spec, pageable).map(TicketCommentResponse::from);

    return TicketThreadResponse.of(TicketResponse.from(t), commentsPage);
  }

  @GetMapping("/{id}/thread/timeline")
  @PreAuthorize("isAuthenticated()")
  public java.util.List<ThreadEventResponse> timeline(@PathVariable Long id,
                                                      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
                                                      @RequestParam(name = "limit", defaultValue = "100") int limit) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    boolean isAdminOrAgent = current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT;
    if (!isAdminOrAgent) {
      Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
      if (!(current.getRole() == User.Role.CLIENT && userClientId != null && userClientId.equals(t.getClientId()))) {
        throw new org.springframework.security.access.AccessDeniedException("forbidden");
      }
    }

    Specification<TicketComment> commentSpec = (r,q,cb) -> cb.and(
        cb.equal(r.get("ticketId"), id),
        cb.isNull(r.get("deletedAt"))
    );
    if (current.getRole() == User.Role.CLIENT) {
      commentSpec = commentSpec.and((r,q,cb) -> cb.isFalse(r.get("internalOnly")));
    }
    var commentsPage = commentRepo.findAll(
        commentSpec,
        PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "id"))
    ).map(TicketCommentResponse::from);

    Specification<Intervention> interSpec = (r,q,cb) -> cb.and(
        cb.equal(r.get("ticketId"), id),
        cb.isNull(r.get("deletedAt"))
    );
    var interventionsPage = interventionRepo.findAll(
        interSpec,
        PageRequest.of(0, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "id"))
    ).map(InterventionResponse::from);

    java.util.List<ThreadEventResponse> events = new java.util.ArrayList<>();
    for (var c : commentsPage.getContent()) events.add(ThreadEventResponse.fromComment(c));
    for (var i : interventionsPage.getContent()) events.add(ThreadEventResponse.fromIntervention(i));

    events.sort(java.util.Comparator.comparing(ThreadEventResponse::getAt,
        java.util.Comparator.nullsLast(java.time.LocalDateTime::compareTo)));

    if (events.size() > limit) {
      events = events.subList(events.size() - limit, events.size());
    }

    return events;
  }


  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public Page<TicketResponse> all(
      @AuthenticationPrincipal UserDetails principal,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    Specification<Ticket> notDeleted = (r,q,cb) -> cb.isNull(r.get("deletedAt"));

    if (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT) {
      return repo.findAll(notDeleted, pageable).map(TicketResponse::from);
    }

    Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
    if (current.getRole() == User.Role.CLIENT && userClientId != null) {
      Specification<Ticket> scoped = notDeleted.and((r,q,cb) -> cb.equal(r.get("clientId"), userClientId));
      return repo.findAll(scoped, pageable).map(TicketResponse::from);
    }

    throw new org.springframework.security.access.AccessDeniedException("forbidden");
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','CLIENT')")
  public TicketResponse create(@RequestBody @Valid TicketCreateRequest in,
                               @AuthenticationPrincipal UserDetails principal) {
    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    Ticket.TicketPriority prio = in.getPriority() == null ? Ticket.TicketPriority.MEDIUM : in.getPriority();
    var now = LocalDateTime.now(APP_ZONE);

    Long clientIdToUse;
    if (current.getRole() == User.Role.CLIENT) {
      if (current.getClient() == null) throw new IllegalArgumentException("client_scope_missing");
      clientIdToUse = current.getClient().getId();
    } else {
      if (in.getClientId() == null) throw new IllegalArgumentException("clientId_required");
      clientIdToUse = in.getClientId();
    }

    Contract c = (in.getContractId() != null) ? contractRepo.findById(in.getContractId()).orElse(null) : null;

    Ticket t = new Ticket();
    t.setClientId(clientIdToUse);
    t.setSiteId(in.getSiteId());
    t.setContractId(in.getContractId());
    t.setTitle(in.getTitle());
    t.setDescription(in.getDescription());
    t.setPriority(prio);
    t.setAssigneeUserId(in.getAssigneeUserId());
    t.setStatus(Ticket.TicketStatus.OPEN);
    t.setDeletedAt(null);

    int targetRespH = targetRespondHours(prio, c);
    int targetResoH = targetResolveHours(prio, c);
    t.setRespondBy(now.plusHours(targetRespH));
    t.setResolveBy(now.plusHours(targetResoH));

    return TicketResponse.from(repo.save(t));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','CLIENT')")
  public TicketResponse update(@PathVariable Long id,
                               @RequestBody TicketUpdateRequest in,
                               @AuthenticationPrincipal UserDetails principal) {
    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    if (current.getRole() == User.Role.CLIENT) {
      Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
      if (userClientId == null || !userClientId.equals(t.getClientId())) {
        throw new org.springframework.security.access.AccessDeniedException("forbidden");
      }
    }

    var beforePriority = t.getPriority();
    var beforeStatus = t.getStatus();

    if (in.getTitle() != null) t.setTitle(in.getTitle());
    if (in.getDescription() != null) t.setDescription(in.getDescription());
    if (in.getPriority() != null) t.setPriority(in.getPriority());
    if (in.getAssigneeUserId() != null) t.setAssigneeUserId(in.getAssigneeUserId());
    if (in.getWaitingReason() != null) t.setWaitingReason(in.getWaitingReason());

    if (in.getStatus() != null) {
      var newStatus = in.getStatus();
      var now = LocalDateTime.now(APP_ZONE);
      if ((newStatus == Ticket.TicketStatus.ASSIGNED || newStatus == Ticket.TicketStatus.IN_PROGRESS)
          && t.getRespondedAt() == null) {
        t.setRespondedAt(now);
      }
      if (newStatus == Ticket.TicketStatus.CLOSED && t.getResolvedAt() == null) {
        t.setResolvedAt(now);
      }
      t.setStatus(newStatus);
    }

    boolean priorityChanged = (in.getPriority() != null && in.getPriority() != beforePriority);
    boolean statusChanged = (in.getStatus() != null && in.getStatus() != beforeStatus);
    if (priorityChanged || statusChanged) {
      Contract c = (t.getContractId() != null) ? contractRepo.findById(t.getContractId()).orElse(null) : null;
      recomputeDeadlinesRemaining(t, c);
    }

    return TicketResponse.from(repo.save(t));
  }

  @PostMapping("/{id}/transition")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
  public TicketResponse transition(@PathVariable Long id,
                                   @RequestParam String action) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    var allowed = ALLOWED.getOrDefault(t.getStatus(), java.util.Set.of());
    if (!allowed.contains(action)) {
      throw new IllegalArgumentException("transition_not_allowed");
    }

    var now = LocalDateTime.now(APP_ZONE);
    switch (action) {
      case "ASSIGN" -> {
        t.setStatus(Ticket.TicketStatus.ASSIGNED);
        if (t.getRespondedAt() == null) t.setRespondedAt(now);
      }
      case "START" -> {
        t.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        if (t.getRespondedAt() == null) t.setRespondedAt(now);
      }
      case "WAIT" -> {
        t.setStatus(Ticket.TicketStatus.WAITING);
        if (t.getPauseStartedAt() == null) t.setPauseStartedAt(now);
      }
      case "RESUME" -> {
        if (t.getPauseStartedAt() != null) {
          long paused = java.time.Duration.between(t.getPauseStartedAt(), now).getSeconds();
          t.setPausedSeconds(t.getPausedSeconds() + (int) paused);
          t.setPauseStartedAt(null);
        }
        t.setStatus(Ticket.TicketStatus.IN_PROGRESS);
      }
      case "CLOSE" -> {
        t.setStatus(Ticket.TicketStatus.CLOSED);
        if (t.getResolvedAt() == null) t.setResolvedAt(now);
      }
      case "CANCEL" -> t.setStatus(Ticket.TicketStatus.CANCELED);
      default -> throw new IllegalArgumentException("unknown_action");
    }

    Contract c = (t.getContractId() != null) ? contractRepo.findById(t.getContractId()).orElse(null) : null;
    recomputeDeadlinesRemaining(t, c);

    return TicketResponse.from(repo.save(t));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null) return; // idempotent
    t.setDeletedAt(LocalDateTime.now(APP_ZONE));
    repo.save(t);
  }

  @PostMapping("/{id}/restore")
  @PreAuthorize("hasRole('ADMIN')")
  public TicketResponse restore(@PathVariable Long id) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() == null) return TicketResponse.from(t);
    t.setDeletedAt(null);
    return TicketResponse.from(repo.save(t));
  }

  private int targetRespondHours(Ticket.TicketPriority p, Contract c) {
    if (c == null) {
      return switch (p) { case CRITICAL -> 1; case HIGH -> 4; case MEDIUM -> 8; case LOW -> 16; };
    }
    return switch (p) {
      case CRITICAL -> c.getRespCritHours();
      case HIGH     -> c.getRespHighHours();
      case MEDIUM   -> c.getRespMediumHours();
      case LOW      -> c.getRespLowHours();
    };
  }

  private int targetResolveHours(Ticket.TicketPriority p, Contract c) {
    if (c == null) {
      return switch (p) { case CRITICAL -> 4; case HIGH -> 8; case MEDIUM -> 24; case LOW -> 48; };
    }
    return switch (p) {
      case CRITICAL -> c.getResoCritHours();
      case HIGH     -> c.getResoHighHours();
      case MEDIUM   -> c.getResoMediumHours();
      case LOW      -> c.getResoLowHours();
    };
  }

  private void recomputeDeadlinesRemaining(Ticket t, Contract c) {
    var now = LocalDateTime.now(APP_ZONE);
    var prio = t.getPriority();

    int targetRespH = targetRespondHours(prio, c);
    int targetResoH = targetResolveHours(prio, c);

    boolean pauseOnWaiting = (c == null) || c.isPauseOnWaiting();
    Contract.MeasureWindow window = (c == null) ? Contract.MeasureWindow.CALENDAR : c.getMeasureWindow();

    if (t.getRespondedAt() == null) {
      long elapsed = effectiveElapsedSeconds(t, now, window, pauseOnWaiting, c);
      long remaining = Math.max(0, targetRespH * 3600L - elapsed);
      t.setRespondBy(now.plusSeconds(remaining));
    }
    if (t.getResolvedAt() == null) {
      long elapsed = effectiveElapsedSeconds(t, now, window, pauseOnWaiting, c);
      long remaining = Math.max(0, targetResoH * 3600L - elapsed);
      t.setResolveBy(now.plusSeconds(remaining));
    }
  }

  private long effectiveElapsedSeconds(Ticket t, LocalDateTime now,
                                       Contract.MeasureWindow window, boolean pauseOnWaiting, Contract c) {
    LocalDateTime start = t.getCreatedAt() != null ? t.getCreatedAt() : now;
    long base;
    if (window == Contract.MeasureWindow.CALENDAR || c == null) {
      base = Duration.between(start, now).getSeconds();
    } else {
      var supportStart = c.getSupportHoursStart();
      var supportEnd = c.getSupportHoursEnd();
      var monFriOnly = (c.getSupportDays() == Contract.SupportDays.MON_FRI);
      base = businessElapsedSeconds(start, now, supportStart, supportEnd, monFriOnly);
    }

    if (!pauseOnWaiting) return Math.max(base, 0);

    long paused = t.getPausedSeconds();
    if (t.getPauseStartedAt() != null) {
      paused += Duration.between(t.getPauseStartedAt(), now).getSeconds();
    }
    return Math.max(base - paused, 0);
  }

  private long businessElapsedSeconds(LocalDateTime start, LocalDateTime now,
                                      LocalTime supportStart, LocalTime supportEnd,
                                      boolean monFriOnly) {
    if (!now.isAfter(start)) return 0L;
    LocalDate d = start.toLocalDate();
    LocalDate last = now.toLocalDate();
    long total = 0L;
    while (!d.isAfter(last)) {
      if (!monFriOnly || isMonToFri(d)) {
        LocalDateTime dayStart = LocalDateTime.of(d, supportStart);
        LocalDateTime dayEnd   = LocalDateTime.of(d, supportEnd);
        LocalDateTime overlapStart = max(start, dayStart);
        LocalDateTime overlapEnd   = min(now, dayEnd);
        if (overlapEnd.isAfter(overlapStart)) {
          total += Duration.between(overlapStart, overlapEnd).getSeconds();
        }
      }
      d = d.plusDays(1);
    }
    return Math.max(total, 0);
  }

  private boolean isMonToFri(LocalDate d) {
    int v = d.getDayOfWeek().getValue();
    return v >= 1 && v <= 5;
  }
  private LocalDateTime max(LocalDateTime a, LocalDateTime b) { return a.isAfter(b) ? a : b; }
  private LocalDateTime min(LocalDateTime a, LocalDateTime b) { return a.isBefore(b) ? a : b; }
}
