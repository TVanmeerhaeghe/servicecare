package com.teo.servicecare.tickets.ticketcomment.dto;

import com.teo.servicecare.tickets.ticketcomment.TicketComment;
import java.time.LocalDateTime;

public class TicketCommentResponse {
  private Long id;
  private Long ticketId;
  private Long authorUserId;
  private String authorName;
  private String body;
  private boolean internalOnly;
  private boolean authorIsClient;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static TicketCommentResponse from(TicketComment c) {
    var r = new TicketCommentResponse();
    r.id = c.getId();
    r.ticketId = c.getTicketId();
    r.authorUserId = c.getAuthorUserId();
    r.authorName = c.getAuthorName();
    r.body = c.getBody();
    r.internalOnly = c.isInternalOnly();
    r.authorIsClient = c.isAuthorIsClient();
    r.createdAt = c.getCreatedAt();
    r.updatedAt = c.getUpdatedAt();
    return r;
  }

  public Long getId() {
    return id;
  }

  public Long getTicketId() {
    return ticketId;
  }

  public Long getAuthorUserId() {
    return authorUserId;
  }

  public String getAuthorName() {
    return authorName;
  }

  public String getBody() {
    return body;
  }

  public boolean isInternalOnly() {
    return internalOnly;
  }

  public boolean isAuthorIsClient() {
    return authorIsClient;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
