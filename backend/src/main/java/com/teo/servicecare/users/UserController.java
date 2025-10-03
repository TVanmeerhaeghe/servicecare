package com.teo.servicecare.users;

import com.teo.servicecare.common.dto.PageResponse;
import com.teo.servicecare.users.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public PageResponse<UserResponse> all(
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    var page = service.list(pageable).map(UserResponse::from);
    return PageResponse.from(page);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse one(@PathVariable Long id) {
    return UserResponse.from(service.get(id));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse create(@RequestBody @Valid UserCreateRequest in) {
    return UserResponse.from(service.create(in));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse adminUpdate(@PathVariable Long id, @RequestBody UserUpdateRequest in) {
    return UserResponse.from(service.updateAdmin(id, in));
  }

  @PostMapping("/{id}/reset-password")
  @PreAuthorize("hasRole('ADMIN')")
  public void resetPassword(@PathVariable Long id, @RequestBody @Valid ResetPasswordRequest in) {
    service.resetPassword(id, in);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }

  @GetMapping("/search")
  @PreAuthorize("hasRole('ADMIN')")
  public PageResponse<UserResponse> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) User.Role role,
      @RequestParam(required = false) User.Status status,
      @RequestParam(required = false) Long clientId,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    var page = service.search(q, role, status, clientId, pageable).map(UserResponse::from);
    return PageResponse.from(page);
  }

  @GetMapping("/self")
  @PreAuthorize("isAuthenticated()")
  public UserResponse self(@AuthenticationPrincipal UserDetails principal) {
    return UserResponse.from(service.getSelf(principal.getUsername()));
  }

  @PutMapping("/self")
  @PreAuthorize("isAuthenticated()")
  public UserResponse updateSelf(@AuthenticationPrincipal UserDetails principal,
                                 @RequestBody UserSelfUpdateRequest in) {
    return UserResponse.from(service.updateSelf(principal.getUsername(), in));
  }

  @PostMapping("/self/change-password")
  @PreAuthorize("isAuthenticated()")
  public void changePassword(@AuthenticationPrincipal UserDetails principal,
                             @RequestBody @Valid ChangePasswordRequest in) {
    service.changePassword(principal.getUsername(), in);
  }

  @GetMapping("/assignees")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public PageResponse<AssigneeResponse> assignees(
      @RequestParam(required = false) String q,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
  ) {
    var page = service.assignees(q, pageable);
    return PageResponse.from(page);
  }
}
