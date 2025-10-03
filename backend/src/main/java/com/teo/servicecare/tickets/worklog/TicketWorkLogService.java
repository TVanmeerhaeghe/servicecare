package com.teo.servicecare.tickets.worklog;

import com.teo.servicecare.tickets.Ticket;
import com.teo.servicecare.tickets.TicketRepository;
import com.teo.servicecare.tickets.worklog.dto.TicketWorkLogCreateRequest;
import com.teo.servicecare.tickets.worklog.dto.TicketWorkLogResponse;
import com.teo.servicecare.tickets.worklog.dto.TicketWorkLogUpdateRequest;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
public class TicketWorkLogService {

  private static final ZoneId APP_ZONE = ZoneId.of("Europe/Paris");

  private final TicketWorkLogRepository repo;
  private final TicketRepository ticketRepo;
  private final UserRepository userRepo;

  public TicketWorkLogService(TicketWorkLogRepository repo,
                              TicketRepository ticketRepo,
                              UserRepository userRepo) {
    this.repo = repo;
    this.ticketRepo = ticketRepo;
    this.userRepo = userRepo;
  }

  private Ticket guardTicketVisible(Long ticketId, String username) {
    var t = ticketRepo.findById(ticketId).orElseThrow();
    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");

    var current = userRepo.findByEmail(username).orElseThrow();
    if (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT || current.getRole() == User.Role.TECHNICIAN) {
      return t;
    }
    if (current.getRole() == User.Role.CLIENT) {
      Long cid = current.getClient() != null ? current.getClient().getId() : null;
      if (cid != null && cid.equals(t.getClientId())) return t;
    }
    throw new org.springframework.security.access.AccessDeniedException("forbidden");
  }

  private TicketWorkLog guardLogVisible(Long worklogId, String username) {
    var w = repo.findById(worklogId).orElseThrow();
    guardTicketVisible(w.getTicketId(), username);
    return w;
  }

  // --- API ---
  public Page<TicketWorkLogResponse> list(Long ticketId, String username, Pageable pageable) {
    guardTicketVisible(ticketId, username);

    Specification<TicketWorkLog> spec = (r,q,cb) -> cb.and(
        cb.equal(r.get("ticketId"), ticketId),
        cb.isNull(r.get("deletedAt"))
    );
    return repo.findAll(spec, pageable).map(TicketWorkLogResponse::from);
  }

  public TicketWorkLogResponse get(Long id, String username) {
    return TicketWorkLogResponse.from(guardLogVisible(id, username));
  }

  public TicketWorkLogResponse create(TicketWorkLogCreateRequest in, String username) {
    var t = guardTicketVisible(in.getTicketId(), username);
    var current = userRepo.findByEmail(username).orElseThrow();

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

  public TicketWorkLogResponse update(Long id, TicketWorkLogUpdateRequest in, String username) {
    var w = guardLogVisible(id, username);
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

  public TicketWorkLogResponse start(Long ticketId, String note, String username) {
    var t = guardTicketVisible(ticketId, username);
    var current = userRepo.findByEmail(username).orElseThrow();

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

  public TicketWorkLogResponse stop(Long maybeTicketId, String username) {
    var current = userRepo.findByEmail(username).orElseThrow();

    Specification<TicketWorkLog> activeSpec = (r,q,cb) -> {
      var base = cb.and(cb.isNull(r.get("deletedAt")),
                        cb.isNull(r.get("endedAt")),
                        cb.equal(r.get("userId"), current.getId()));
      if (maybeTicketId != null) {
        base = cb.and(base, cb.equal(r.get("ticketId"), maybeTicketId));
      }
      return base;
    };

    var page = repo.findAll(activeSpec, PageRequest.of(0,1));
    if (!page.hasContent()) throw new IllegalStateException("no_active_log");

    var w = page.getContent().get(0);
    guardTicketVisible(w.getTicketId(), username);
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

  public void delete(Long id, String username) {
    var w = guardLogVisible(id, username);
    if (w.getDeletedAt() != null) return;
    w.setDeletedAt(LocalDateTime.now(APP_ZONE));
    repo.save(w);
  }
}
