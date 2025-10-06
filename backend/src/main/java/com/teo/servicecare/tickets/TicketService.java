package com.teo.servicecare.tickets;

import com.teo.servicecare.contracts.Contract;
import com.teo.servicecare.contracts.ContractRepository;
import com.teo.servicecare.tickets.dto.TicketCreateRequest;
import com.teo.servicecare.tickets.dto.TicketUpdateRequest;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Map;
import java.util.Set;

@Service
public class TicketService {

  private final TicketRepository repo;
  private final UserRepository userRepo;
  private final ContractRepository contractRepo;

  public TicketService(TicketRepository repo, UserRepository userRepo, ContractRepository contractRepo) {
    this.repo = repo;
    this.userRepo = userRepo;
    this.contractRepo = contractRepo;
  }

  private static final ZoneId APP_ZONE = ZoneId.of("Europe/Paris");

  private static final Map<Ticket.TicketStatus, Set<String>> ALLOWED = Map.of(
      Ticket.TicketStatus.OPEN, Set.of("ASSIGN", "START", "CANCEL"),
      Ticket.TicketStatus.ASSIGNED, Set.of("START", "WAIT", "CANCEL"),
      Ticket.TicketStatus.IN_PROGRESS, Set.of("WAIT", "CLOSE", "CANCEL"),
      Ticket.TicketStatus.WAITING, Set.of("RESUME", "CANCEL"),
      Ticket.TicketStatus.CLOSED, Set.of(),
      Ticket.TicketStatus.CANCELED, Set.of());

  public Page<Ticket> listVisible(String email, Pageable pageable) {
    User current = userRepo.findByEmail(email).orElseThrow();
    Specification<Ticket> notDeleted = (r, q, cb) -> cb.isNull(r.get("deletedAt"));

    if (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT) {
      return repo.findAll(notDeleted, pageable);
    }

    Long userClientId = (current.getClient() != null) ? current.getClient().getId() : -1L;
    Specification<Ticket> scoped = notDeleted.and((r, q, cb) -> cb.equal(r.get("clientId"), userClientId));
    return repo.findAll(scoped, pageable);
  }

  public Ticket getVisible(String email, Long id) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null)
      throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(email).orElseThrow();
    if (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT)
      return t;

    Long userClientId = current.getClient() != null ? current.getClient().getId() : null;
    if (current.getRole() == User.Role.CLIENT && userClientId != null && userClientId.equals(t.getClientId())) {
      return t;
    }
    throw new org.springframework.security.access.AccessDeniedException("forbidden");
  }

  public Ticket create(String email, TicketCreateRequest in) {
    User current = userRepo.findByEmail(email).orElseThrow();

    Ticket.TicketPriority prio = (in.getPriority() == null ? Ticket.TicketPriority.MEDIUM : in.getPriority());
    var now = LocalDateTime.now(APP_ZONE);

    Long clientIdToUse;
    if (current.getRole() == User.Role.CLIENT) {
      if (current.getClient() == null)
        throw new IllegalArgumentException("client_scope_missing");
      clientIdToUse = current.getClient().getId();
    } else {
      if (in.getClientId() == null)
        throw new IllegalArgumentException("clientId_required");
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

    return repo.save(t);
  }

  public Ticket update(String email, Long id, TicketUpdateRequest in) {
    User current = userRepo.findByEmail(email).orElseThrow();
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null)
      throw new IllegalArgumentException("ticket_deleted");

    if (current.getRole() == User.Role.CLIENT) {
      Long userClientId = (current.getClient() != null) ? current.getClient().getId() : null;
      if (userClientId == null || !userClientId.equals(t.getClientId())) {
        throw new org.springframework.security.access.AccessDeniedException("forbidden");
      }
    }

    var beforePriority = t.getPriority();
    var beforeStatus = t.getStatus();

    if (in.getTitle() != null)
      t.setTitle(in.getTitle());
    if (in.getDescription() != null)
      t.setDescription(in.getDescription());
    if (in.getPriority() != null)
      t.setPriority(in.getPriority());
    if (in.getAssigneeUserId() != null)
      t.setAssigneeUserId(in.getAssigneeUserId());
    if (in.getWaitingReason() != null)
      t.setWaitingReason(in.getWaitingReason());

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

    return repo.save(t);
  }

  public Ticket transition(Long id, String action) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null)
      throw new IllegalArgumentException("ticket_deleted");

    var allowed = ALLOWED.getOrDefault(t.getStatus(), Set.of());
    if (!allowed.contains(action))
      throw new IllegalArgumentException("transition_not_allowed");

    var now = LocalDateTime.now(APP_ZONE);
    switch (action) {
      case "ASSIGN" -> {
        t.setStatus(Ticket.TicketStatus.ASSIGNED);
        if (t.getRespondedAt() == null)
          t.setRespondedAt(now);
      }
      case "START" -> {
        t.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        if (t.getRespondedAt() == null)
          t.setRespondedAt(now);
      }
      case "WAIT" -> {
        t.setStatus(Ticket.TicketStatus.WAITING);
        if (t.getPauseStartedAt() == null)
          t.setPauseStartedAt(now);
      }
      case "RESUME" -> {
        if (t.getPauseStartedAt() != null) {
          long paused = Duration.between(t.getPauseStartedAt(), now).getSeconds();
          t.setPausedSeconds(t.getPausedSeconds() + (int) paused);
          t.setPauseStartedAt(null);
        }
        t.setStatus(Ticket.TicketStatus.IN_PROGRESS);
      }
      case "CLOSE" -> {
        t.setStatus(Ticket.TicketStatus.CLOSED);
        if (t.getResolvedAt() == null)
          t.setResolvedAt(now);
      }
      case "CANCEL" -> t.setStatus(Ticket.TicketStatus.CANCELED);
      default -> throw new IllegalArgumentException("unknown_action");
    }

    Contract c = (t.getContractId() != null) ? contractRepo.findById(t.getContractId()).orElse(null) : null;
    recomputeDeadlinesRemaining(t, c);

    return repo.save(t);
  }

  public void softDelete(Long id) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null)
      return;
    t.setDeletedAt(LocalDateTime.now(APP_ZONE));
    repo.save(t);
  }

  public Ticket restore(Long id) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() == null)
      return t;
    t.setDeletedAt(null);
    return repo.save(t);
  }

  public Page<Ticket> search(String email,
      Long clientId, Long siteId, Long contractId, Long assigneeUserId,
      Ticket.TicketStatus status, Ticket.TicketPriority priority, Boolean slaBreached,
      String q, String createdFrom, String createdTo, String updatedFrom, String updatedTo,
      String respondByBefore, String resolveByBefore,
      Pageable pageable) {
    var current = userRepo.findByEmail(email).orElseThrow();
    Specification<Ticket> spec = (r, qb, cb) -> cb.isNull(r.get("deletedAt"));

    boolean isClient = current.getRole() == User.Role.CLIENT;
    if (isClient) {
      Long scopedClientId = (current.getClient() != null) ? current.getClient().getId() : -1L;
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("clientId"), scopedClientId));
    } else {
      if (clientId != null)
        spec = spec.and((r, qb, cb) -> cb.equal(r.get("clientId"), clientId));
    }

    if (siteId != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("siteId"), siteId));
    if (contractId != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("contractId"), contractId));
    if (assigneeUserId != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("assigneeUserId"), assigneeUserId));
    if (status != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("status"), status));
    if (priority != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("priority"), priority));
    if (slaBreached != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("slaBreached"), slaBreached));

    if (q != null && !q.isBlank()) {
      String like = "%" + q.trim().toLowerCase() + "%";
      spec = spec.and((r, qb, cb) -> {
        var titleLower = cb.lower(r.get("title"));
        var descAsVarchar = cb.concat("", r.get("description"));
        var descLower = cb.lower(descAsVarchar);
        return cb.or(cb.like(titleLower, like), cb.like(descLower, like));
      });
    }

    ZoneId tz = APP_ZONE;

    if (createdFrom != null && !createdFrom.isBlank()) {
      var from = LocalDate.parse(createdFrom).atStartOfDay();
      spec = spec.and((r, qb, cb) -> cb.greaterThanOrEqualTo(r.get("createdAt"), from));
    }
    if (createdTo != null && !createdTo.isBlank()) {
      var toExcl = LocalDate.parse(createdTo).plusDays(1).atStartOfDay();
      spec = spec.and((r, qb, cb) -> cb.lessThan(r.get("createdAt"), toExcl));
    }
    if (updatedFrom != null && !updatedFrom.isBlank()) {
      var from = LocalDate.parse(updatedFrom).atStartOfDay();
      spec = spec.and((r, qb, cb) -> cb.greaterThanOrEqualTo(r.get("updatedAt"), from));
    }
    if (updatedTo != null && !updatedTo.isBlank()) {
      var toExcl = LocalDate.parse(updatedTo).plusDays(1).atStartOfDay();
      spec = spec.and((r, qb, cb) -> cb.lessThan(r.get("updatedAt"), toExcl));
    }
    if (respondByBefore != null && !respondByBefore.isBlank()) {
      var dt = LocalDate.parse(respondByBefore).atStartOfDay(tz).toLocalDateTime();
      spec = spec.and((r, qb, cb) -> cb.lessThanOrEqualTo(r.get("respondBy"), dt));
    }
    if (resolveByBefore != null && !resolveByBefore.isBlank()) {
      var dt = LocalDate.parse(resolveByBefore).atStartOfDay(tz).toLocalDateTime();
      spec = spec.and((r, qb, cb) -> cb.lessThanOrEqualTo(r.get("resolveBy"), dt));
    }

    return repo.findAll(spec, pageable);
  }

  // --- helpers SLA ---
  private int targetRespondHours(Ticket.TicketPriority p, Contract c) {
    if (c == null) {
      return switch (p) {
        case CRITICAL -> 1;
        case HIGH -> 4;
        case MEDIUM -> 8;
        case LOW -> 16;
      };
    }
    return switch (p) {
      case CRITICAL -> c.getRespCritHours();
      case HIGH -> c.getRespHighHours();
      case MEDIUM -> c.getRespMediumHours();
      case LOW -> c.getRespLowHours();
    };
  }

  private int targetResolveHours(Ticket.TicketPriority p, Contract c) {
    if (c == null) {
      return switch (p) {
        case CRITICAL -> 4;
        case HIGH -> 8;
        case MEDIUM -> 24;
        case LOW -> 48;
      };
    }
    return switch (p) {
      case CRITICAL -> c.getResoCritHours();
      case HIGH -> c.getResoHighHours();
      case MEDIUM -> c.getResoMediumHours();
      case LOW -> c.getResoLowHours();
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

    if (!pauseOnWaiting)
      return Math.max(base, 0);

    long paused = t.getPausedSeconds();
    if (t.getPauseStartedAt() != null) {
      paused += Duration.between(t.getPauseStartedAt(), now).getSeconds();
    }
    return Math.max(base - paused, 0);
  }

  private long businessElapsedSeconds(LocalDateTime start, LocalDateTime now,
      LocalTime supportStart, LocalTime supportEnd,
      boolean monFriOnly) {
    if (!now.isAfter(start))
      return 0L;
    LocalDate d = start.toLocalDate();
    LocalDate last = now.toLocalDate();
    long total = 0L;
    while (!d.isAfter(last)) {
      if (!monFriOnly || isMonToFri(d)) {
        LocalDateTime dayStart = LocalDateTime.of(d, supportStart);
        LocalDateTime dayEnd = LocalDateTime.of(d, supportEnd);
        LocalDateTime overlapStart = max(start, dayStart);
        LocalDateTime overlapEnd = min(now, dayEnd);
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

  private LocalDateTime max(LocalDateTime a, LocalDateTime b) {
    return a.isAfter(b) ? a : b;
  }

  private LocalDateTime min(LocalDateTime a, LocalDateTime b) {
    return a.isBefore(b) ? a : b;
  }
}
