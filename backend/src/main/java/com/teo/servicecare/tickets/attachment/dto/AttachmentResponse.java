package com.teo.servicecare.tickets.attachment.dto;

import com.teo.servicecare.tickets.attachment.TicketAttachment;
import java.time.LocalDateTime;

public class AttachmentResponse {
  private Long id;
  private Long ticketId;
  private String originalName;
  private String filename;
  private String contentType;
  private Long size;
  private String storagePath;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static AttachmentResponse from(TicketAttachment a) {
    var r = new AttachmentResponse();
    r.id = a.getId();
    r.ticketId = a.getTicketId();
    r.originalName = a.getOriginalName();
    r.filename = a.getFilename();
    r.contentType = a.getContentType();
    r.size = a.getSize();
    r.storagePath = a.getStoragePath();
    r.createdAt = a.getCreatedAt();
    r.updatedAt = a.getUpdatedAt();
    return r;
  }

  public Long getId() {
    return id;
  }

  public Long getTicketId() {
    return ticketId;
  }

  public String getOriginalName() {
    return originalName;
  }

  public String getFilename() {
    return filename;
  }

  public String getContentType() {
    return contentType;
  }

  public Long getSize() {
    return size;
  }

  public String getStoragePath() {
    return storagePath;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}
