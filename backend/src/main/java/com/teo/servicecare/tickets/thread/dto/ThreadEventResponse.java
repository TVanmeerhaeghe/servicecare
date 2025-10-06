package com.teo.servicecare.tickets.thread.dto;

import com.teo.servicecare.tickets.intervention.Intervention;
import java.time.LocalDateTime;

public class ThreadEventResponse {

  public enum Kind {
    COMMENT, INTERVENTION, ATTACHMENT
  }

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

  private String originalName;
  private String contentType;
  private Long size;
  private String downloadUrl;

  public static ThreadEventResponse fromComment(Long id, LocalDateTime at, String authorName, String body) {
    var e = new ThreadEventResponse();
    e.kind = Kind.COMMENT;
    e.id = id;
    e.at = at;
    e.authorName = authorName;
    e.body = body;
    return e;
  }

  public static ThreadEventResponse fromIntervention(
      Long id, LocalDateTime at,
      Intervention.Type type, Intervention.Status status,
      String title, Long technicianUserId,
      LocalDateTime scheduledStart, LocalDateTime scheduledEnd,
      LocalDateTime actualStart, LocalDateTime actualEnd) {
    var e = new ThreadEventResponse();
    e.kind = Kind.INTERVENTION;
    e.id = id;
    e.at = at;
    e.interventionType = type;
    e.interventionStatus = status;
    e.title = title;
    e.technicianUserId = technicianUserId;
    e.scheduledStart = scheduledStart;
    e.scheduledEnd = scheduledEnd;
    e.actualStart = actualStart;
    e.actualEnd = actualEnd;
    return e;
  }

  public static ThreadEventResponse fromAttachment(
      Long id, LocalDateTime at,
      String originalName, String contentType, Long size,
      String downloadUrl) {
    var e = new ThreadEventResponse();
    e.kind = Kind.ATTACHMENT;
    e.id = id;
    e.at = at;
    e.originalName = originalName;
    e.contentType = contentType;
    e.size = size;
    e.downloadUrl = downloadUrl;
    return e;
  }

  public Kind getKind() {
    return kind;
  }

  public Long getId() {
    return id;
  }

  public LocalDateTime getAt() {
    return at;
  }

  public String getAuthorName() {
    return authorName;
  }

  public String getBody() {
    return body;
  }

  public Intervention.Type getInterventionType() {
    return interventionType;
  }

  public Intervention.Status getInterventionStatus() {
    return interventionStatus;
  }

  public String getTitle() {
    return title;
  }

  public Long getTechnicianUserId() {
    return technicianUserId;
  }

  public LocalDateTime getScheduledStart() {
    return scheduledStart;
  }

  public LocalDateTime getScheduledEnd() {
    return scheduledEnd;
  }

  public LocalDateTime getActualStart() {
    return actualStart;
  }

  public LocalDateTime getActualEnd() {
    return actualEnd;
  }

  public String getOriginalName() {
    return originalName;
  }

  public String getContentType() {
    return contentType;
  }

  public Long getSize() {
    return size;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }
}
