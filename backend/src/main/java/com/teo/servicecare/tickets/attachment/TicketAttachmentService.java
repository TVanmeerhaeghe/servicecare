package com.teo.servicecare.tickets.attachment;

import com.teo.servicecare.config.UploadsProperties;
import com.teo.servicecare.tickets.Ticket;
import com.teo.servicecare.tickets.TicketRepository;
import com.teo.servicecare.tickets.attachment.dto.AttachmentResponse;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TicketAttachmentService {

  private static final ZoneId APP_ZONE = ZoneId.of("Europe/Paris");
  private static final DateTimeFormatter FMT_YYYY_MM = DateTimeFormatter.ofPattern("yyyy/MM");

  private final TicketAttachmentRepository repo;
  private final TicketRepository ticketRepo;
  private final UserRepository userRepo;
  private final UploadsProperties props;

  private final Path baseDir;

  public TicketAttachmentService(TicketAttachmentRepository repo,
      TicketRepository ticketRepo,
      UserRepository userRepo,
      UploadsProperties props) throws IOException {
    this.repo = repo;
    this.ticketRepo = ticketRepo;
    this.userRepo = userRepo;
    this.props = props;

    String configured = props.getDir();
    if (configured == null || configured.isBlank()) {
      configured = System.getProperty("user.dir") + File.separator + "uploads";
    }
    this.baseDir = Paths.get(configured).toAbsolutePath().normalize();
    Files.createDirectories(this.baseDir);
  }

  public AttachmentResponse upload(String email, Long ticketId, MultipartFile file) throws IOException {
    var user = userRepo.findByEmail(email).orElseThrow();
    var t = ticketRepo.findById(ticketId).orElseThrow();
    enforceVisibility(user, t);

    if (t.getDeletedAt() != null)
      throw new IllegalArgumentException("ticket_deleted");
    if (file.isEmpty())
      throw new IllegalArgumentException("file_empty");

    long maxBytes = Math.max(0, props.getMaxSizeBytes());
    if (maxBytes > 0 && file.getSize() > maxBytes) {
      throw new IllegalArgumentException("file_too_large");
    }

    var original = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
    var sanitizedOriginal = original.replace("\\", "_").replace("/", "_");
    String rawExt = "";
    int dot = sanitizedOriginal.lastIndexOf('.');
    if (dot >= 0 && dot < sanitizedOriginal.length() - 1) {
      rawExt = sanitizedOriginal.substring(dot);
    }
    final String extLower = rawExt.toLowerCase(Locale.ROOT);

    if (props.getBlockedExtensions() != null && !props.getBlockedExtensions().isEmpty()) {
      boolean blocked = props.getBlockedExtensions().stream()
          .anyMatch(bad -> bad != null && bad.equalsIgnoreCase(extLower));
      if (blocked)
        throw new IllegalArgumentException("file_extension_blocked");
    }

    String detectedContentType = safeDetectContentType(file);
    if (props.getAllowedContentTypes() != null && !props.getAllowedContentTypes().isEmpty()) {
      boolean allowed = props.getAllowedContentTypes().stream()
          .anyMatch(ct -> ct != null && ct.equalsIgnoreCase(detectedContentType));
      if (!allowed)
        throw new IllegalArgumentException("content_type_not_allowed");
    }

    var today = LocalDate.now(APP_ZONE);
    var yymm = FMT_YYYY_MM.format(today);
    var targetDir = baseDir.resolve(yymm).normalize();
    ensureChildOfBase(targetDir);
    Files.createDirectories(targetDir);

    var stored = "att_" + UUID.randomUUID() + (extLower.isBlank() ? "" : extLower);

    var target = targetDir.resolve(stored).normalize();
    ensureChildOfBase(target);
    try (var in = file.getInputStream()) {
      Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
    }

    var a = new TicketAttachment();
    a.setTicketId(ticketId);
    a.setOriginalName(sanitizedOriginal);
    a.setFilename(stored);
    a.setContentType(detectedContentType);
    a.setSize(file.getSize());
    a.setStoragePath(yymm + "/" + stored);
    a.setDeletedAt(null);

    return AttachmentResponse.from(repo.save(a));
  }

  public Page<AttachmentResponse> list(String email, Long ticketId, Pageable pageable) {
    var user = userRepo.findByEmail(email).orElseThrow();
    var t = ticketRepo.findById(ticketId).orElseThrow();
    enforceVisibility(user, t);

    Specification<TicketAttachment> spec = (r, q, cb) -> cb.and(
        cb.equal(r.get("ticketId"), ticketId),
        cb.isNull(r.get("deletedAt")));
    return repo.findAll(spec, pageable).map(AttachmentResponse::from);
  }

  public File getFile(String email, Long attachmentId) {
    var user = userRepo.findByEmail(email).orElseThrow();
    var a = repo.findById(attachmentId).orElseThrow();
    var t = ticketRepo.findById(a.getTicketId()).orElseThrow();
    enforceVisibility(user, t);

    if (a.getDeletedAt() != null)
      throw new IllegalArgumentException("attachment_deleted");
    Path p = baseDir.resolve(a.getStoragePath()).normalize();
    ensureChildOfBase(p);
    return p.toFile();
  }

  public TicketAttachment getMeta(String email, Long attachmentId) {
    var user = userRepo.findByEmail(email).orElseThrow();
    var a = repo.findById(attachmentId).orElseThrow();
    var t = ticketRepo.findById(a.getTicketId()).orElseThrow();
    enforceVisibility(user, t);
    if (a.getDeletedAt() != null)
      throw new IllegalArgumentException("attachment_deleted");
    return a;
  }

  public void delete(String email, Long attachmentId) {
    var user = userRepo.findByEmail(email).orElseThrow();
    var a = repo.findById(attachmentId).orElseThrow();
    var t = ticketRepo.findById(a.getTicketId()).orElseThrow();
    enforceVisibility(user, t);

    if (a.getDeletedAt() != null)
      return;
    a.setDeletedAt(LocalDateTime.now(APP_ZONE));
    repo.save(a);

    try {
      Path p = baseDir.resolve(a.getStoragePath()).normalize();
      ensureChildOfBase(p);
      Files.deleteIfExists(p);
    } catch (Exception ignore) {
    }
  }

  private void ensureChildOfBase(Path p) {
    if (!p.startsWith(baseDir)) {
      throw new IllegalArgumentException("invalid_path");
    }
  }

  private void enforceVisibility(User user, Ticket t) {
    switch (Objects.requireNonNullElse(user.getRole(), User.Role.CLIENT)) {
      case ADMIN, AGENT, TECHNICIAN -> {
        /* ok */ }
      case CLIENT -> {
        var userClientId = user.getClient() != null ? user.getClient().getId() : null;
        if (!(userClientId != null && userClientId.equals(t.getClientId()))) {
          throw new org.springframework.security.access.AccessDeniedException("forbidden");
        }
      }
      default -> throw new org.springframework.security.access.AccessDeniedException("forbidden");
    }
  }

  private String safeDetectContentType(MultipartFile file) {
    try {
      String ct = file.getContentType();
      if (ct != null && !ct.isBlank())
        return ct;

      try {
        Path tmp = Files.createTempFile("mimecheck_", ".bin");
        try (var in = file.getInputStream()) {
          Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
        }
        String probed = Files.probeContentType(tmp);
        Files.deleteIfExists(tmp);
        if (probed != null && !probed.isBlank())
          return probed;
      } catch (Exception ignore) {
      }

    } catch (Exception ignore) {
    }
    return "application/octet-stream";
  }
}
