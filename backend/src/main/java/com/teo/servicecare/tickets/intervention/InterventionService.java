package com.teo.servicecare.tickets.intervention;

import com.teo.servicecare.tickets.Ticket;
import com.teo.servicecare.tickets.TicketRepository;
import com.teo.servicecare.tickets.intervention.dto.InterventionCreateRequest;
import com.teo.servicecare.tickets.intervention.dto.InterventionResponse;
import com.teo.servicecare.tickets.intervention.dto.InterventionUpdateRequest;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class InterventionService {

  private static final ZoneId APP_ZONE = ZoneId.of("Europe/Paris");

  private final InterventionRepository repo;
  private final TicketRepository ticketRepo;
  private final UserRepository userRepo;

  public InterventionService(InterventionRepository repo,
      TicketRepository ticketRepo,
      UserRepository userRepo) {
    this.repo = repo;
    this.ticketRepo = ticketRepo;
    this.userRepo = userRepo;
  }

  private Ticket guardTicketVisible(Long ticketId, String username) {
    var t = ticketRepo.findById(ticketId).orElseThrow();
    if (t.getDeletedAt() != null)
      throw new IllegalArgumentException("ticket_deleted");
    var current = userRepo.findByEmail(username).orElseThrow();

    if (current.getRole() == User.Role.ADMIN || current.getRole() == User.Role.AGENT)
      return t;

    if (current.getRole() == User.Role.CLIENT) {
      Long cid = current.getClient() != null ? current.getClient().getId() : null;
      if (cid != null && cid.equals(t.getClientId()))
        return t;
    }

    throw new org.springframework.security.access.AccessDeniedException("forbidden");
  }

  private Intervention guardInterventionVisible(Long id, String username) {
    var i = repo.findById(id).orElseThrow();
    guardTicketVisible(i.getTicketId(), username);
    return i;
  }

  public Page<InterventionResponse> list(Long ticketId, String username, Pageable pageable) {
    guardTicketVisible(ticketId, username);

    Specification<Intervention> spec = (r, q, cb) -> cb.and(
        cb.equal(r.get("ticketId"), ticketId),
        cb.isNull(r.get("deletedAt")));
    return repo.findAll(spec, pageable).map(InterventionResponse::from);
  }

  public InterventionResponse get(Long id, String username) {
    return InterventionResponse.from(guardInterventionVisible(id, username));
  }

  public InterventionResponse create(InterventionCreateRequest in, String username) {
    var t = guardTicketVisible(in.getTicketId(), username);

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

  public InterventionResponse update(Long id, InterventionUpdateRequest in, String username) {
    var i = guardInterventionVisible(id, username);
    var t = ticketRepo.findById(i.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null)
      throw new IllegalArgumentException("ticket_deleted");

    if (in.getType() != null)
      i.setType(in.getType());
    if (in.getTechnicianUserId() != null)
      i.setTechnicianUserId(in.getTechnicianUserId());
    if (in.getTitle() != null)
      i.setTitle(in.getTitle());
    if (in.getNotes() != null)
      i.setNotes(in.getNotes());
    if (in.getReport() != null)
      i.setReport(in.getReport());
    if (in.getScheduledStart() != null)
      i.setScheduledStart(in.getScheduledStart());
    if (in.getScheduledEnd() != null)
      i.setScheduledEnd(in.getScheduledEnd());
    if (in.getTravelMinutes() != null)
      i.setTravelMinutes(in.getTravelMinutes());
    if (in.getWorkMinutes() != null)
      i.setWorkMinutes(in.getWorkMinutes());
    if (in.getActualStart() != null)
      i.setActualStart(in.getActualStart());
    if (in.getActualEnd() != null)
      i.setActualEnd(in.getActualEnd());

    return InterventionResponse.from(repo.save(i));
  }

  public InterventionResponse transition(Long id, String action, String username) {
    var i = guardInterventionVisible(id, username);
    var t = ticketRepo.findById(i.getTicketId()).orElseThrow();
    if (t.getDeletedAt() != null)
      throw new IllegalArgumentException("ticket_deleted");

    var now = LocalDateTime.now(APP_ZONE);

    switch (action) {
      case "START" -> {
        i.setStatus(Intervention.Status.IN_PROGRESS);
        if (i.getActualStart() == null)
          i.setActualStart(now);
        if (t.getStatus() == Ticket.TicketStatus.OPEN
            || t.getStatus() == Ticket.TicketStatus.ASSIGNED
            || t.getStatus() == Ticket.TicketStatus.WAITING) {
          t.setStatus(Ticket.TicketStatus.IN_PROGRESS);
          if (t.getRespondedAt() == null)
            t.setRespondedAt(now);
          ticketRepo.save(t);
        }
      }
      case "DONE" -> {
        i.setStatus(Intervention.Status.DONE);
        if (i.getActualEnd() == null)
          i.setActualEnd(now);
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

  public void delete(Long id, String username) {
    var i = guardInterventionVisible(id, username);
    if (i.getDeletedAt() != null)
      return;
    i.setDeletedAt(LocalDateTime.now(APP_ZONE));
    repo.save(i);
  }
}
