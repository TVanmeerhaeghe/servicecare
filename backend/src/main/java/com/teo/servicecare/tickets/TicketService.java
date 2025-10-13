package com.teo.servicecare.tickets;

import com.teo.servicecare.contracts.Contract;
import com.teo.servicecare.contracts.ContractRepository;
import com.teo.servicecare.tickets.dto.TicketCreateRequest;
import com.teo.servicecare.tickets.dto.TicketUpdateRequest;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Map;
import java.util.Set;
import com.teo.servicecare.tickets.sla.TicketSlaEvent;
import com.teo.servicecare.tickets.sla.TicketSlaEventRepository;

@Service
public class TicketService {

  private final TicketRepository repo;
  private final UserRepository userRepo;
  private final ContractRepository contractRepo;
  private final TicketSlaEventRepository slaEventRepo;

  public TicketService(TicketRepository repo, UserRepository userRepo, ContractRepository contractRepo,
      TicketSlaEventRepository slaEventRepo) {
    this.repo = repo;
    this.userRepo = userRepo;
    this.contractRepo = contractRepo;
    this.slaEventRepo = slaEventRepo;
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
    } else if (current.getRole() == User.Role.TECHNICIAN) {
      Specification<Ticket> mine = notDeleted.and((r, q, cb) -> cb.equal(r.get("assigneeUserId"), current.getId()));
      return repo.findAll(mine, pageable);
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

    if (c != null) {
      t.setSlaTimezone(c.getTimezone());
      t.setSlaSupportDays(c.getSupportDays());
      t.setSlaSupportHoursStart(c.getSupportHoursStart());
      t.setSlaSupportHoursEnd(c.getSupportHoursEnd());
      t.setSlaMeasureWindow(c.getMeasureWindow());
      t.setSlaPauseOnWaiting(c.isPauseOnWaiting());

      t.setSlaRespCritHours(c.getRespCritHours());
      t.setSlaRespHighHours(c.getRespHighHours());
      t.setSlaRespMediumHours(c.getRespMediumHours());
      t.setSlaRespLowHours(c.getRespLowHours());

      t.setSlaResoCritHours(c.getResoCritHours());
      t.setSlaResoHighHours(c.getResoHighHours());
      t.setSlaResoMediumHours(c.getResoMediumHours());
      t.setSlaResoLowHours(c.getResoLowHours());
    } else {
      t.setSlaTimezone(APP_ZONE.getId());
      t.setSlaSupportDays(Contract.SupportDays.MON_FRI);
      t.setSlaSupportHoursStart(java.time.LocalTime.of(9, 0));
      t.setSlaSupportHoursEnd(java.time.LocalTime.of(18, 0));
      t.setSlaMeasureWindow(Contract.MeasureWindow.CALENDAR);
      t.setSlaPauseOnWaiting(true);

      t.setSlaRespCritHours(1);
      t.setSlaRespHighHours(4);
      t.setSlaRespMediumHours(8);
      t.setSlaRespLowHours(16);

      t.setSlaResoCritHours(4);
      t.setSlaResoHighHours(8);
      t.setSlaResoMediumHours(24);
      t.setSlaResoLowHours(48);
    }

    recomputeDeadlinesRemaining(t, c);

    return repo.save(t);
  }

  private void recordSlaEvent(Ticket t, TicketSlaEvent.Type type, LocalDateTime at,
                              Long actorUserId, String actorName, String note, String payloadJson) {
    TicketSlaEvent e = new TicketSlaEvent();
    e.setTicket(t);
    e.setType(type);
    e.setHappenedAt(at);
    e.setActorUserId(actorUserId);
    e.setActorUserName(actorName);
    e.setNote(note);
    e.setPayloadJson(payloadJson);
    slaEventRepo.save(e);
  }

  private void recordSlaEvent(Ticket t, TicketSlaEvent.Type type, LocalDateTime at,
                              Long actorUserId, String note, String payloadJson) {
    recordSlaEvent(t, type, at, actorUserId, null, note, payloadJson);
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

      if (newStatus == Ticket.TicketStatus.WAITING) {
        if (t.getPauseStartedAt() == null) {
          t.setPauseStartedAt(now);
          recordSlaEvent(t, TicketSlaEvent.Type.WAIT_START, now, current.getId(), current.getEmail(), t.getWaitingReason(), null);
        }
      } else {
        if (beforeStatus == Ticket.TicketStatus.WAITING && t.getPauseStartedAt() != null) {
          long add = Duration.between(t.getPauseStartedAt(), now).getSeconds();
          t.setPausedSeconds(t.getPausedSeconds() + (int) Math.max(add, 0));
          t.setPauseStartedAt(null);
          recordSlaEvent(t, TicketSlaEvent.Type.WAIT_END, now, current.getId(), current.getEmail(), "RESUME", null);
        }
      }

      t.setStatus(newStatus);
      recordSlaEvent(
          t,
          TicketSlaEvent.Type.STATUS_CHANGE,
          now,
          current.getId(),
          current.getEmail(),
          "STATUS_CHANGE",
          "{\"from\":\"" + beforeStatus + "\",\"to\":\"" + newStatus + "\"}"
      );
    }

    boolean priorityChanged = (in.getPriority() != null && in.getPriority() != beforePriority);
    boolean statusChanged = (in.getStatus() != null && in.getStatus() != beforeStatus);
    if (priorityChanged) {
      recordSlaEvent(t, TicketSlaEvent.Type.PRIORITY_CHANGE,
          LocalDateTime.now(APP_ZONE),
          current.getId(),
          current.getEmail(),
          "Priority changed",
          "{\"from\":\"" + beforePriority + "\",\"to\":\"" + in.getPriority() + "\"}");
    }

    if (priorityChanged || statusChanged) {
      Contract c = (t.getContractId() != null) ? contractRepo.findById(t.getContractId()).orElse(null) : null;
      recomputeDeadlinesRemaining(t, c);
    }

    return repo.save(t);
  }

  public Ticket transition(String email, Long id, String action, boolean force) {
    var t = repo.findById(id).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    User current = userRepo.findByEmail(email).orElseThrow();
    boolean isAdmin = current.getRole() == User.Role.ADMIN;

    if (t.getStatus() == Ticket.TicketStatus.CLOSED || t.getStatus() == Ticket.TicketStatus.CANCELED) {
      throw new IllegalArgumentException("transition_not_allowed");
    }

    String act = (action == null ? "" : action.trim().toUpperCase(java.util.Locale.ROOT));

    var allowed = ALLOWED.getOrDefault(t.getStatus(), java.util.Set.of());
    if (!allowed.contains(act) && !(force && isAdmin)) {
      if (force && !isAdmin) throw new IllegalArgumentException("transition_not_allowed_force_denied");
      throw new IllegalArgumentException("transition_not_allowed");
    }

    var now = LocalDateTime.now(APP_ZONE);
    switch (act) {
      case "CLOSE" -> {
        if (t.getPauseStartedAt() != null) {
          long add = Duration.between(t.getPauseStartedAt(), now).getSeconds();
          t.setPausedSeconds(t.getPausedSeconds() + (int) Math.max(add, 0));
          t.setPauseStartedAt(null);
        }
        if (t.getRespondedAt() == null) t.setRespondedAt(now);
        t.setStatus(Ticket.TicketStatus.CLOSED);
        if (t.getResolvedAt() == null) t.setResolvedAt(now);

        String payload = "{\"to\":\"CLOSED\",\"forced\":" + (force && isAdmin) + "}";
        try {
          recordSlaEvent(t, com.teo.servicecare.tickets.sla.TicketSlaEvent.Type.STATUS_CHANGE, now,
              current.getId(), current.getFirstName() + " " + current.getLastName(),
              "CLOSE", payload);
        } catch (NoSuchMethodError err) {
          recordSlaEvent(t, com.teo.servicecare.tickets.sla.TicketSlaEvent.Type.STATUS_CHANGE, now,
              current.getId(), "CLOSE", payload);
        }
      }
      case "ASSIGN" -> {
        t.setStatus(Ticket.TicketStatus.ASSIGNED);
        if (t.getRespondedAt() == null) t.setRespondedAt(now);
        recordSlaEvent(t, TicketSlaEvent.Type.STATUS_CHANGE, now, null, "ASSIGN", "{\"to\":\"ASSIGNED\"}");
      }
      case "START" -> {
        t.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        if (t.getRespondedAt() == null) t.setRespondedAt(now);
        recordSlaEvent(t, TicketSlaEvent.Type.STATUS_CHANGE, now, null, "START", "{\"to\":\"IN_PROGRESS\"}");
      }
      case "WAIT" -> {
        t.setStatus(Ticket.TicketStatus.WAITING);
        if (t.getPauseStartedAt() == null) t.setPauseStartedAt(now);
        recordSlaEvent(t, TicketSlaEvent.Type.WAIT_START, now, null, t.getWaitingReason(), null);
      }
      case "RESUME" -> {
        if (t.getPauseStartedAt() != null) {
          long add = Duration.between(t.getPauseStartedAt(), now).getSeconds();
          t.setPausedSeconds(t.getPausedSeconds() + (int) Math.max(add, 0));
          t.setPauseStartedAt(null);
        }
        t.setStatus(Ticket.TicketStatus.IN_PROGRESS);
        recordSlaEvent(t, TicketSlaEvent.Type.WAIT_END, now, null, "RESUME", null);
      }
      case "CANCEL" -> {
        t.setStatus(Ticket.TicketStatus.CANCELED);
        recordSlaEvent(t, TicketSlaEvent.Type.STATUS_CHANGE, now, null, "CANCEL", "{\"to\":\"CANCELED\"}");
      }
      default -> { /* no-op */ }
    }
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
    } else if (current.getRole() == User.Role.TECHNICIAN) {
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("assigneeUserId"), current.getId()));
    }

    if (clientId != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("clientId"), clientId));
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

  public com.teo.servicecare.tickets.dto.TicketSlaResponse getSla(Long id) {
    var t = repo.findById(id).orElseThrow();
    Contract c = (t.getContractId() != null) ? contractRepo.findById(t.getContractId()).orElse(null) : null;
    var now = LocalDateTime.now(APP_ZONE);
    recomputeDeadlinesRemaining(t, c);

    var r = new com.teo.servicecare.tickets.dto.TicketSlaResponse();
    r.ticketId = t.getId();
    r.contractId = t.getContractId();
    r.priority = t.getPriority();
    r.timezone = (t.getSlaTimezone() != null) ? t.getSlaTimezone() : APP_ZONE.getId();
    r.supportDays = (t.getSlaSupportDays() != null) ? t.getSlaSupportDays()
        : (c != null ? c.getSupportDays() : Contract.SupportDays.MON_FRI);
    r.measureWindow = slaWindow(t, c);
    r.pauseOnWaiting = slaPauseOnWaiting(t, c);

    r.responseDueAt = t.getRespondBy();
    r.resolutionDueAt = t.getResolveBy();
    r.responseMetAt = t.getRespondedAt();
    r.resolutionMetAt = t.getResolvedAt();

    r.paused = t.getPauseStartedAt() != null;
    r.responseBreached = (t.getRespondedAt() == null && t.getRespondBy() != null && now.isAfter(t.getRespondBy()));
    r.resolutionBreached = (t.getResolvedAt() == null && t.getResolveBy() != null && now.isAfter(t.getResolveBy()));
    r.secondsToResponseDeadline = (t.getRespondBy() != null)
        ? Math.max(0, Duration.between(now, t.getRespondBy()).getSeconds())
        : 0;
    r.secondsToResolutionDeadline = (t.getResolveBy() != null)
        ? Math.max(0, Duration.between(now, t.getResolveBy()).getSeconds())
        : 0;
    return r;
  }

  private int targetRespondHours(Ticket.TicketPriority p, Contract c, Ticket t) {
    Integer snap = switch (p) {
      case CRITICAL -> t.getSlaRespCritHours();
      case HIGH -> t.getSlaRespHighHours();
      case MEDIUM -> t.getSlaRespMediumHours();
      case LOW -> t.getSlaRespLowHours();
    };
    if (snap != null)
      return snap;
    return (c != null)
        ? switch (p) {
          case CRITICAL -> c.getRespCritHours();
          case HIGH -> c.getRespHighHours();
          case MEDIUM -> c.getRespMediumHours();
          case LOW -> c.getRespLowHours();
        }
        : switch (p) {
          case CRITICAL -> 1;
          case HIGH -> 4;
          case MEDIUM -> 8;
          case LOW -> 16;
        };
  }

  private int targetResolveHours(Ticket.TicketPriority p, Contract c, Ticket t) {
    Integer snap = switch (p) {
      case CRITICAL -> t.getSlaResoCritHours();
      case HIGH -> t.getSlaResoHighHours();
      case MEDIUM -> t.getSlaResoMediumHours();
      case LOW -> t.getSlaResoLowHours();
    };
    if (snap != null)
      return snap;
    return (c != null)
        ? switch (p) {
          case CRITICAL -> c.getResoCritHours();
          case HIGH -> c.getResoHighHours();
          case MEDIUM -> c.getResoMediumHours();
          case LOW -> c.getResoLowHours();
        }
        : switch (p) {
          case CRITICAL -> 4;
          case HIGH -> 8;
          case MEDIUM -> 24;
          case LOW -> 48;
        };
  }

  private Contract.MeasureWindow slaWindow(Ticket t, Contract c) {
    if (t.getSlaMeasureWindow() != null)
      return t.getSlaMeasureWindow();
    return (c != null) ? c.getMeasureWindow() : Contract.MeasureWindow.CALENDAR;
  }

  private boolean slaPauseOnWaiting(Ticket t, Contract c) {
    if (t.getSlaPauseOnWaiting() != null)
      return Boolean.TRUE.equals(t.getSlaPauseOnWaiting());
    return (c == null) || c.isPauseOnWaiting();
  }

  private LocalTime slaStart(Ticket t, Contract c) {
    return (t.getSlaSupportHoursStart() != null) ? t.getSlaSupportHoursStart()
        : (c != null ? c.getSupportHoursStart() : LocalTime.of(9, 0));
  }

  private LocalTime slaEnd(Ticket t, Contract c) {
    return (t.getSlaSupportHoursEnd() != null) ? t.getSlaSupportHoursEnd()
        : (c != null ? c.getSupportHoursEnd() : LocalTime.of(18, 0));
  }

  private boolean slaMonFriOnly(Ticket t, Contract c) {
    var d = (t.getSlaSupportDays() != null) ? t.getSlaSupportDays()
        : (c != null ? c.getSupportDays() : Contract.SupportDays.MON_FRI);
    return d == Contract.SupportDays.MON_FRI;
  }

  private ZoneId slaZone(Ticket t, Contract c) {
    String tz = (t.getSlaTimezone() != null) ? t.getSlaTimezone()
        : (c != null && c.getTimezone() != null ? c.getTimezone() : APP_ZONE.getId());
    try {
      return ZoneId.of(tz);
    } catch (Exception ex) {
      return APP_ZONE;
    }
  }

  private LocalDateTime toSlaZone(LocalDateTime dt, ZoneId zone) {
    if (dt == null)
      return null;
    return dt.atZone(APP_ZONE).withZoneSameInstant(zone).toLocalDateTime();
  }

  private long businessElapsedSecondsSla(LocalDateTime startApp, LocalDateTime endApp, Ticket t, Contract c) {
    ZoneId zone = slaZone(t, c);
    LocalDateTime start = toSlaZone(startApp, zone);
    LocalDateTime end = toSlaZone(endApp, zone);
    return businessElapsedSeconds(start, end, slaStart(t, c), slaEnd(t, c), slaMonFriOnly(t, c));
  }

  private long businessElapsedSeconds(LocalDateTime start, LocalDateTime end,
      LocalTime supportStart, LocalTime supportEnd,
      boolean monFriOnly) {
    if (!end.isAfter(start))
      return 0L;
    LocalDate d = start.toLocalDate();
    LocalDate last = end.toLocalDate();
    long total = 0L;
    while (!d.isAfter(last)) {
      if (!monFriOnly || isMonToFri(d)) {
        LocalDateTime dayStart = LocalDateTime.of(d, supportStart);
        LocalDateTime dayEnd = LocalDateTime.of(d, supportEnd);
        LocalDateTime overlapStart = max(start, dayStart);
        LocalDateTime overlapEnd = min(end, dayEnd);
        if (overlapEnd.isAfter(overlapStart)) {
          total += Duration.between(overlapStart, overlapEnd).getSeconds();
        }
      }
      d = d.plusDays(1);
    }
    return Math.max(total, 0);
  }

  private LocalDateTime addWorkingSeconds(LocalDateTime start, long seconds,
      LocalTime dayStart, LocalTime dayEnd,
      boolean monFriOnly) {
    if (seconds <= 0)
      return start;
    LocalDateTime cur = start;
    long remaining = seconds;
    while (remaining > 0) {
      if (monFriOnly && !isMonToFri(cur.toLocalDate())) {
        cur = LocalDateTime.of(cur.toLocalDate().plusDays(1), dayStart);
        continue;
      }
      LocalDateTime windowStart = LocalDateTime.of(cur.toLocalDate(), dayStart);
      LocalDateTime windowEnd = LocalDateTime.of(cur.toLocalDate(), dayEnd);
      if (cur.isBefore(windowStart))
        cur = windowStart;
      if (!cur.isBefore(windowEnd)) {
        cur = LocalDateTime.of(cur.toLocalDate().plusDays(1), dayStart);
        continue;
      }
      long avail = Duration.between(cur, windowEnd).getSeconds();
      long take = Math.min(avail, remaining);
      cur = cur.plusSeconds(take);
      remaining -= take;
      if (remaining > 0) {
        cur = LocalDateTime.of(cur.toLocalDate().plusDays(1), dayStart);
      }
    }
    return cur;
  }

  private void recomputeDeadlinesRemaining(Ticket t, Contract c) {
    var now = LocalDateTime.now(APP_ZONE);
    var prio = t.getPriority();

    int targetRespH = targetRespondHours(prio, c, t);
    int targetResoH = targetResolveHours(prio, c, t);

    Contract.MeasureWindow window = slaWindow(t, c);

    if (t.getRespondedAt() == null) {
      long elapsed = effectiveElapsedSeconds(t, now, window, slaPauseOnWaiting(t, c), c);
      long remaining = Math.max(0, targetRespH * 3600L - elapsed);
      if (window == Contract.MeasureWindow.CALENDAR) {
        t.setRespondBy(now.plusSeconds(remaining));
      } else {
        ZoneId zone = slaZone(t, c);
        LocalDateTime dueSla = addWorkingSeconds(
            toSlaZone(now, zone),
            remaining,
            slaStart(t, c),
            slaEnd(t, c),
            slaMonFriOnly(t, c));
        t.setRespondBy(dueSla.atZone(zone).withZoneSameInstant(APP_ZONE).toLocalDateTime());
      }
    }
    if (t.getResolvedAt() == null) {
      long elapsed = effectiveElapsedSeconds(t, now, window, slaPauseOnWaiting(t, c), c);
      long remaining = Math.max(0, targetResoH * 3600L - elapsed);
      if (window == Contract.MeasureWindow.CALENDAR) {
        t.setResolveBy(now.plusSeconds(remaining));
      } else {
        ZoneId zone = slaZone(t, c);
        LocalDateTime dueSla = addWorkingSeconds(
            toSlaZone(now, zone),
            remaining,
            slaStart(t, c),
            slaEnd(t, c),
            slaMonFriOnly(t, c));
        t.setResolveBy(dueSla.atZone(zone).withZoneSameInstant(APP_ZONE).toLocalDateTime());
      }
    }

    if (t.getResolvedAt() == null && t.getResolveBy() != null && now.isAfter(t.getResolveBy())) {
      t.setSlaBreached(true);
    }
  }

  private long effectiveElapsedSeconds(Ticket t, LocalDateTime now,
      Contract.MeasureWindow window, boolean pauseOnWaiting, Contract c) {
    LocalDateTime start = t.getCreatedAt() != null ? t.getCreatedAt() : now;
    long base;
    if (window == Contract.MeasureWindow.CALENDAR) {
      base = Duration.between(start, now).getSeconds();
    } else {
      base = businessElapsedSecondsSla(start, now, t, c);
    }

    if (!pauseOnWaiting)
      return Math.max(base, 0);

    long paused = t.getPausedSeconds();
    if (t.getPauseStartedAt() != null) {
      if (window == Contract.MeasureWindow.CALENDAR) {
        paused += Duration.between(t.getPauseStartedAt(), now).getSeconds();
      } else {
        paused += businessElapsedSecondsSla(t.getPauseStartedAt(), now, t, c);
      }
    }
    return Math.max(base - paused, 0);
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

  public Ticket assign(String email, Long ticketId, Long assigneeUserId) {
    var admin = userRepo.findByEmail(email).orElseThrow();

    var assignee = userRepo.findById(assigneeUserId)
        .filter(u -> u.getStatus() == User.Status.ACTIVE)
        .filter(u -> u.getRole() == User.Role.ADMIN || u.getRole() == User.Role.AGENT || u.getRole() == User.Role.TECHNICIAN)
        .orElseThrow(() -> new IllegalArgumentException("assignee_invalid"));

    var t = repo.findById(ticketId).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    t.setAssigneeUserId(assignee.getId());

    var now = LocalDateTime.now(APP_ZONE);
    if (t.getStatus() == Ticket.TicketStatus.OPEN) {
      t.setStatus(Ticket.TicketStatus.ASSIGNED);
      if (t.getRespondedAt() == null) t.setRespondedAt(now);
      recordSlaEvent(t, TicketSlaEvent.Type.STATUS_CHANGE, now,
          admin.getId(), admin.getFirstName() + " " + admin.getLastName(),
          "ASSIGN", "{\"to\":\"ASSIGNED\",\"assigneeId\":" + assignee.getId() + "}");
    }

    return repo.save(t);
  }
}
