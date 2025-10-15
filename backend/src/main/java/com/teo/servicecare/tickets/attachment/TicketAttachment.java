package com.teo.servicecare.tickets.attachment;

import com.teo.servicecare.users.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_attachments")
public class TicketAttachment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long ticketId;

  @Column(nullable = false, length = 190)
  private String originalName;

  @Column(nullable = false, length = 190)
  private String filename;

  @Column(nullable = false, length = 190)
  private String contentType;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false, length = 255)
  private String storagePath;

  @Column(insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uploaded_by_user_id")
  private User uploadedBy;

  public Long getId() {
    return id;
  }

  public Long getTicketId() {
    return ticketId;
  }

  public void setTicketId(Long ticketId) {
    this.ticketId = ticketId;
  }

  public String getOriginalName() {
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public String getStoragePath() {
    return storagePath;
  }

  public void setStoragePath(String storagePath) {
    this.storagePath = storagePath;
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

  public User getUploadedBy() {
    return uploadedBy;
  }

  public void setUploadedBy(User uploadedBy) {
    this.uploadedBy = uploadedBy;
  }
}
