package com.teo.servicecare.tickets.ticketcomment;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_comments")
public class TicketComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long ticketId;

  @Column(nullable = false)
  private Long authorUserId;

  @Column(nullable = false, length = 190)
  private String authorName;

  @Lob
  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String body;

  @Column(nullable = false)
  private boolean internalOnly = false;

  @Column(nullable = false)
  private boolean authorIsClient = false;

  @Column(insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;

  public Long getId() {
    return id;
  }

  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }

  public Long getAuthorUserId() {
    return authorUserId;
  }

  public void setAuthorUserId(Long authorUserId) {
    this.authorUserId = authorUserId;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public boolean isInternalOnly() {
    return internalOnly;
  }

  public void setInternalOnly(boolean internalOnly) {
    this.internalOnly = internalOnly;
  }

  public boolean isAuthorIsClient() {
    return authorIsClient;
  }

  public void setAuthorIsClient(boolean authorIsClient) {
    this.authorIsClient = authorIsClient;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }
}
