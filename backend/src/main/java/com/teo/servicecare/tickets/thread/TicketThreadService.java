package com.teo.servicecare.tickets.thread;

import com.teo.servicecare.tickets.Ticket;
import com.teo.servicecare.tickets.TicketRepository;

import com.teo.servicecare.tickets.ticketcomment.TicketComment;
import com.teo.servicecare.tickets.ticketcomment.TicketCommentRepository;

import com.teo.servicecare.tickets.intervention.Intervention;
import com.teo.servicecare.tickets.intervention.InterventionRepository;

import com.teo.servicecare.tickets.attachment.TicketAttachment;
import com.teo.servicecare.tickets.attachment.TicketAttachmentRepository;

import com.teo.servicecare.tickets.thread.dto.ThreadEventResponse;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TicketThreadService {

  private final TicketRepository ticketRepo;
  private final UserRepository userRepo;
  private final TicketCommentRepository commentRepo;
  private final InterventionRepository interventionRepo;
  private final TicketAttachmentRepository attachmentRepo;

  public TicketThreadService(TicketRepository ticketRepo,
      UserRepository userRepo,
      TicketCommentRepository commentRepo,
      InterventionRepository interventionRepo,
      TicketAttachmentRepository attachmentRepo) {
    this.ticketRepo = ticketRepo;
    this.userRepo = userRepo;
    this.commentRepo = commentRepo;
    this.interventionRepo = interventionRepo;
    this.attachmentRepo = attachmentRepo;
  }

  public List<ThreadEventResponse> getTimeline(String username, Long ticketId) {
    var current = userRepo.findByEmail(username).orElseThrow();
    var t = ticketRepo.findById(ticketId).orElseThrow();
    enforceVisibility(current, t);

    Specification<TicketComment> commentSpec = (r, q, cb) -> cb.and(
        cb.equal(r.get("ticketId"), ticketId),
        cb.isNull(r.get("deletedAt")));
    if (current.getRole() == User.Role.CLIENT) {
      commentSpec = commentSpec.and((r, q, cb) -> cb.isFalse(r.get("internalOnly")));
    }
    var commentEvents = commentRepo.findAll(commentSpec, Sort.by(Sort.Direction.ASC, "id"))
        .stream()
        .map(c -> ThreadEventResponse.fromComment(
            c.getId(),
            c.getCreatedAt(),
            c.getAuthorName(),
            c.getBody(),
            c.isAuthorIsClient(),
            c.isInternalOnly()))
        .toList();

    Specification<Intervention> intervSpec = (r, q, cb) -> cb.and(
        cb.equal(r.get("ticketId"), ticketId),
        cb.isNull(r.get("deletedAt")));
    var interventionEvents = interventionRepo.findAll(
        intervSpec,
        Sort.by(Sort.Direction.ASC, "scheduledStart").and(Sort.by("id"))).stream()
        .map(i -> {
          LocalDateTime at = i.getActualStart() != null ? i.getActualStart()
              : (i.getScheduledStart() != null ? i.getScheduledStart() : i.getCreatedAt());
          return ThreadEventResponse.fromIntervention(
              i.getId(), at,
              i.getType(), i.getStatus(),
              i.getTitle(), i.getTechnicianUserId(),
              i.getScheduledStart(), i.getScheduledEnd(),
              i.getActualStart(), i.getActualEnd());
        })
        .toList();

    Specification<TicketAttachment> attSpec = (r, q, cb) -> cb.and(
        cb.equal(r.get("ticketId"), ticketId),
        cb.isNull(r.get("deletedAt")));
    var attachmentEvents = attachmentRepo.findAll(attSpec, Sort.by(Sort.Direction.ASC, "id"))
        .stream()
        .map(a -> {
          String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
              .path("/api/tickets/attachments/{id}/download")
              .buildAndExpand(a.getId())
              .toUriString();
          LocalDateTime at = a.getCreatedAt();
          if (at == null)
            at = LocalDateTime.now();
          return ThreadEventResponse.fromAttachment(
              a.getId(), at,
              a.getOriginalName(),
              a.getContentType(),
              a.getSize(),
              downloadUrl);
        })
        .toList();

    // Merge + tri
    var timeline = new ArrayList<ThreadEventResponse>();
    timeline.addAll(commentEvents);
    timeline.addAll(interventionEvents);
    timeline.addAll(attachmentEvents);

    timeline.sort(Comparator
        .comparing(ThreadEventResponse::getAt, Comparator.nullsLast(LocalDateTime::compareTo))
        .thenComparing(ThreadEventResponse::getId));

    return timeline;
  }

  private void enforceVisibility(User user, Ticket t) {
    if (user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.AGENT
        || user.getRole() == User.Role.TECHNICIAN)
      return;
    var userClientId = user.getClient() != null ? user.getClient().getId() : null;
    if (!(user.getRole() == User.Role.CLIENT && userClientId != null && userClientId.equals(t.getClientId()))) {
      throw new org.springframework.security.access.AccessDeniedException("forbidden");
    }
  }
}
