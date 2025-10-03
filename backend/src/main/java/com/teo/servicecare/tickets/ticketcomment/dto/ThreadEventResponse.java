package com.teo.servicecare.tickets.ticketcomment.dto;

import com.teo.servicecare.tickets.intervention.Intervention;
import com.teo.servicecare.tickets.intervention.dto.InterventionResponse;

import java.time.LocalDateTime;

public class ThreadEventResponse {
  public enum Kind { COMMENT, INTERVENTION }

  private Kind kind;
  private Long id;
  private LocalDateTime at;

  private String authorName;
  private String body;

  private Intervention.Type interventionType;
  private Intervention.Status interventionStatus;
  private String title;
  private Long technicianUserId;
  private LocalDateTime scheduledStart;
  private LocalDateTime scheduledEnd;
  private LocalDateTime actualStart;
  private LocalDateTime actualEnd;

  public static ThreadEventResponse fromComment(TicketCommentResponse c) {
    var e = new ThreadEventResponse();
    e.kind = Kind.COMMENT;
    e.id = c.getId();
    e.at = c.getCreatedAt();
    e.authorName = c.getAuthorName();
    e.body = c.getBody();
    return e;
  }

  public static ThreadEventResponse fromIntervention(InterventionResponse i) {
    var e = new ThreadEventResponse();
    e.kind = Kind.INTERVENTION;
    e.id = i.getId();
    LocalDateTime at = i.getActualStart() != null ? i.getActualStart()
        : (i.getScheduledStart() != null ? i.getScheduledStart() : i.getCreatedAt());
    e.at = at;

    e.interventionType = i.getType();
    e.interventionStatus = i.getStatus();
    e.title = i.getTitle();
    e.technicianUserId = i.getTechnicianUserId();
    e.scheduledStart = i.getScheduledStart();
    e.scheduledEnd = i.getScheduledEnd();
    e.actualStart = i.getActualStart();
    e.actualEnd = i.getActualEnd();
    return e;
  }

  public Kind getKind() { return kind; }
  public Long getId() { return id; }
  public LocalDateTime getAt() { return at; }
  public String getAuthorName() { return authorName; }
  public String getBody() { return body; }
  public Intervention.Type getInterventionType() { return interventionType; }
  public Intervention.Status getInterventionStatus() { return interventionStatus; }
  public String getTitle() { return title; }
  public Long getTechnicianUserId() { return technicianUserId; }
  public LocalDateTime getScheduledStart() { return scheduledStart; }
  public LocalDateTime getScheduledEnd() { return scheduledEnd; }
  public LocalDateTime getActualStart() { return actualStart; }
  public LocalDateTime getActualEnd() { return actualEnd; }
}
