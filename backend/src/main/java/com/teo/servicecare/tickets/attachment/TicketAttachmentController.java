package com.teo.servicecare.tickets.attachment;

import com.teo.servicecare.tickets.attachment.dto.AttachmentResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.*;

import java.io.File;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/tickets")
public class TicketAttachmentController {

  private final TicketAttachmentService service;

  public TicketAttachmentController(TicketAttachmentService service) {
    this.service = service;
  }

  @PostMapping(value = "/{ticketId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("isAuthenticated()")
  public AttachmentResponse upload(@PathVariable Long ticketId,
      @RequestPart("file") MultipartFile file,
      @AuthenticationPrincipal UserDetails principal) throws Exception {
    return service.upload(principal.getUsername(), ticketId, file);
  }

  @GetMapping("/{ticketId}/attachments")
  @PreAuthorize("isAuthenticated()")
  public Page<AttachmentResponse> list(@PathVariable Long ticketId,
      @AuthenticationPrincipal UserDetails principal,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return service.list(
        principal.getUsername(),
        ticketId,
        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
  }

  @GetMapping("/attachments/{id}/download")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<FileSystemResource> download(@PathVariable Long id,
      @AuthenticationPrincipal UserDetails principal) throws Exception {
    var meta = service.getMeta(principal.getUsername(), id);
    File f = service.getFile(principal.getUsername(), id);

    var res = new FileSystemResource(f);
    String contentType = Files.probeContentType(f.toPath());
    if (contentType == null || contentType.isBlank())
      contentType = "application/octet-stream";

    String filename = meta.getOriginalName().replace("\"", "");

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(filename))
        .header(HttpHeaders.CACHE_CONTROL, "private, max-age=0, must-revalidate")
        .contentLength(f.length())
        .body(res);
  }

  @DeleteMapping("/attachments/{id}")
  @PreAuthorize("isAuthenticated()")
  public void delete(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
    service.delete(principal.getUsername(), id);
  }

  private String contentDisposition(String filename) {
    String ascii = filename.replaceAll("[\\r\\n]", "").replace(";", "");
    String encoded = java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20");
    return "attachment; filename=\"" + ascii + "\"; filename*=UTF-8''" + encoded;
  }
}
