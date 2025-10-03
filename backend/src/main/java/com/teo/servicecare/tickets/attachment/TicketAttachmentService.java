package com.teo.servicecare.tickets.attachment;

import com.teo.servicecare.tickets.Ticket;
import com.teo.servicecare.tickets.TicketRepository;
import com.teo.servicecare.tickets.attachment.dto.AttachmentResponse;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketAttachmentService {

  private static final ZoneId APP_ZONE = ZoneId.of("Europe/Paris");
  private static final DateTimeFormatter FMT_YYYY_MM = DateTimeFormatter.ofPattern("yyyy/MM");

  private final TicketAttachmentRepository repo;
  private final TicketRepository ticketRepo;
  private final UserRepository userRepo;

  private final Path baseDir;

  public TicketAttachmentService(TicketAttachmentRepository repo,
                                 TicketRepository ticketRepo,
                                 UserRepository userRepo,
                                 @Value("${app.uploads.dir:${user.dir}/uploads}") String uploadsDir) throws IOException {
    this.repo = repo;
    this.ticketRepo = ticketRepo;
    this.userRepo = userRepo;

    this.baseDir = Paths.get(uploadsDir).toAbsolutePath().normalize();
    Files.createDirectories(this.baseDir);
  }

  public AttachmentResponse upload(String email, Long ticketId, MultipartFile file) throws IOException {
    var user = userRepo.findByEmail(email).orElseThrow();
    var t = ticketRepo.findById(ticketId).orElseThrow();
    enforceVisibility(user, t);

    if (t.getDeletedAt() != null) throw new IllegalArgumentException("ticket_deleted");
    if (file.isEmpty()) throw new IllegalArgumentException("file_empty");

    var today = LocalDate.now(APP_ZONE);
    var yymm = FMT_YYYY_MM.format(today);

    var targetDir = baseDir.resolve(yymm).normalize();
    ensureChildOfBase(targetDir);
    Files.createDirectories(targetDir);

    var original = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
    var sanitizedOriginal = original.replace("\\", "_").replace("/", "_");

    String ext = "";
    int dot = sanitizedOriginal.lastIndexOf('.');
    if (dot >= 0 && dot < sanitizedOriginal.length() - 1) {
      ext = sanitizedOriginal.substring(dot);
    }

    var stored = "att_" + UUID.randomUUID() + (ext.isBlank() ? "" : ext);

    var target = targetDir.resolve(stored).normalize();
    ensureChildOfBase(target);
    try (var in = file.getInputStream()) {
      Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
    }

    var a = new TicketAttachment();
    a.setTicketId(ticketId);
    a.setOriginalName(sanitizedOriginal);
    a.setFilename(stored);
    a.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
    a.setSize(file.getSize());
    a.setStoragePath(yymm + "/" + stored);
    a.setDeletedAt(null);

    return AttachmentResponse.from(repo.save(a));
  }

  public Page<AttachmentResponse> list(String email, Long ticketId, Pageable pageable) {
    var user = userRepo.findByEmail(email).orElseThrow();
    var t = ticketRepo.findById(ticketId).orElseThrow();
    enforceVisibility(user, t);

    Specification<TicketAttachment> spec = (r,q,cb) -> cb.and(
        cb.equal(r.get("ticketId"), ticketId),
        cb.isNull(r.get("deletedAt"))
    );
    return repo.findAll(spec, pageable).map(AttachmentResponse::from);
  }

  public File getFile(String email, Long attachmentId) {
    var user = userRepo.findByEmail(email).orElseThrow();
    var a = repo.findById(attachmentId).orElseThrow();
    var t = ticketRepo.findById(a.getTicketId()).orElseThrow();
    enforceVisibility(user, t);

    if (a.getDeletedAt() != null) throw new IllegalArgumentException("attachment_deleted");
    Path p = baseDir.resolve(a.getStoragePath()).normalize();
    ensureChildOfBase(p);
    return p.toFile();
  }

  public TicketAttachment getMeta(String email, Long attachmentId) {
    var user = userRepo.findByEmail(email).orElseThrow();
    var a = repo.findById(attachmentId).orElseThrow();
    var t = ticketRepo.findById(a.getTicketId()).orElseThrow();
    enforceVisibility(user, t);
    if (a.getDeletedAt() != null) throw new IllegalArgumentException("attachment_deleted");
    return a;
  }

  public void delete(String email, Long attachmentId) {
    var user = userRepo.findByEmail(email).orElseThrow();
    var a = repo.findById(attachmentId).orElseThrow();
    var t = ticketRepo.findById(a.getTicketId()).orElseThrow();
    enforceVisibility(user, t);

    if (a.getDeletedAt() != null) return;
    a.setDeletedAt(LocalDateTime.now(APP_ZONE));
    repo.save(a);
    try { Files.deleteIfExists(baseDir.resolve(a.getStoragePath()).normalize()); } catch (Exception ignore) {}
  }

  private void ensureChildOfBase(Path p) {
    if (!p.startsWith(baseDir)) {
      throw new IllegalArgumentException("invalid_path");
    }
  }

  private void enforceVisibility(User user, Ticket t) {
    if (user.getRole() == User.Role.ADMIN || user.getRole() == User.Role.AGENT || user.getRole() == User.Role.TECHNICIAN) return;
    var userClientId = user.getClient() != null ? user.getClient().getId() : null;
    if (!(user.getRole() == User.Role.CLIENT && userClientId != null && userClientId.equals(t.getClientId()))) {
      throw new org.springframework.security.access.AccessDeniedException("forbidden");
    }
  }
}
