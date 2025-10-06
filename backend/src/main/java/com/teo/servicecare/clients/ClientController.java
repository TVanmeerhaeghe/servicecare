package com.teo.servicecare.clients;

import com.teo.servicecare.clients.Client.ClientStatus;
import com.teo.servicecare.clients.dto.ClientCreateRequest;
import com.teo.servicecare.clients.dto.ClientUpdateRequest;
import com.teo.servicecare.common.dto.PageResponse;
import com.teo.servicecare.clients.dto.ClientResponse;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

  private final ClientService service;

  public ClientController(ClientService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public PageResponse<ClientResponse> all(
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    var page = service.list(pageable).map(ClientResponse::from);
    return PageResponse.from(page);
  }

  @GetMapping("/{id}")
  public ClientResponse one(@PathVariable Long id,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
    if (principal == null)
      throw new org.springframework.security.access.AccessDeniedException("unauthorized");
    var client = service.getVisibleTo(principal.getUsername(), id);
    return ClientResponse.from(client);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ClientResponse create(@RequestBody @Valid ClientCreateRequest in) {
    var created = service.create(in);
    return ClientResponse.from(created);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ClientResponse update(@PathVariable Long id, @RequestBody ClientUpdateRequest in) {
    var updated = service.update(id, in);
    return ClientResponse.from(updated);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @GetMapping("/lookup")
  @PreAuthorize("isAuthenticated()")
  public PageResponse<Map<String, Object>> lookup(
      @RequestParam(required = false) String q,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
    var page = service.lookup(principal.getUsername(), q, pageable);
    return PageResponse.from(page);
  }

  @GetMapping("/search")
  @PreAuthorize("hasRole('ADMIN')")
  public PageResponse<ClientResponse> search(
      @RequestParam(required = false) ClientStatus status,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String createdFrom,
      @RequestParam(required = false) String createdTo,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    var page = service.search(status, q, createdFrom, createdTo, pageable)
        .map(ClientResponse::from);
    return PageResponse.from(page);
  }
}
