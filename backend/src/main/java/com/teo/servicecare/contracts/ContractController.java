package com.teo.servicecare.contracts;

import com.teo.servicecare.common.dto.PageResponse;
import com.teo.servicecare.contracts.dto.ContractCreateRequest;
import com.teo.servicecare.contracts.dto.ContractResponse;
import com.teo.servicecare.contracts.dto.ContractUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

  private final ContractService service;

  public ContractController(ContractService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public PageResponse<ContractResponse> all(
      @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
    return service.list(pageable);
  }

  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ContractResponse one(@PathVariable Long id,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
    if (principal == null)
      throw new org.springframework.security.access.AccessDeniedException("unauthorized");
    return service.getVisibleTo(principal.getUsername(), id);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ContractResponse create(@RequestBody ContractCreateRequest in) {
    return service.create(in);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ContractResponse update(@PathVariable Long id, @RequestBody ContractUpdateRequest in) {
    return service.update(id, in);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @GetMapping("/search")
  @PreAuthorize("isAuthenticated()")
  public PageResponse<ContractResponse> search(
      @RequestParam(required = false) Long clientId,
      @RequestParam(required = false) Contract.Status status,
      @RequestParam(required = false) Contract.MeasureWindow measureWindow,
      @RequestParam(required = false) Contract.SupportDays supportDays,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String startFrom,
      @RequestParam(required = false) String startTo,
      @RequestParam(required = false) String endFrom,
      @RequestParam(required = false) String endTo,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
      @PageableDefault(size = 20, sort = "id", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
    return service.search(
        principal.getUsername(),
        clientId, status, measureWindow, supportDays, q,
        startFrom, startTo, endFrom, endTo, pageable);
  }
}
