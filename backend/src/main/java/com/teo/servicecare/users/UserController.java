package com.teo.servicecare.users;

import com.teo.servicecare.clients.ClientRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserRepository repo;
  private final ClientRepository clientRepo;
  private final PasswordEncoder encoder;

  public UserController(UserRepository repo, ClientRepository clientRepo, PasswordEncoder encoder) {
    this.repo = repo;
    this.clientRepo = clientRepo;
    this.encoder = encoder;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Page<User> all(
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return repo.findAll(pageable);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public User one(@PathVariable Long id) {
    return repo.findById(id).orElseThrow();
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public User create(@RequestBody @Valid UserCreateRequest in) {
    if (repo.existsByEmail(in.getEmail())) throw new IllegalArgumentException("email_already_used");
    var u = new User();
    u.setEmail(in.getEmail());
    u.setPassword(encoder.encode(in.getPassword()));
    u.setFirstName(in.getFirstName());
    u.setLastName(in.getLastName());
    u.setPhone(in.getPhone());
    if (in.getClientId() != null) {
      var c = clientRepo.findById(in.getClientId()).orElseThrow();
      u.setClient(c);
    }
    return repo.save(u);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public User adminUpdate(@PathVariable Long id, @RequestBody UserUpdateRequest in) {
    User u = repo.findById(id).orElseThrow();

    if (in.getEmail() != null) u.setEmail(in.getEmail());
    if (in.getFirstName() != null) u.setFirstName(in.getFirstName());
    if (in.getLastName() != null) u.setLastName(in.getLastName());
    if (in.getPhone() != null) u.setPhone(in.getPhone());
    if (in.getRole() != null) u.setRole(in.getRole());
    if (in.getStatus() != null) u.setStatus(in.getStatus());

    if (in.getClientId() != null) {
      var c = clientRepo.findById(in.getClientId()).orElseThrow();
      u.setClient(c);
    }

    return repo.save(u);
  }

  @PostMapping("/{id}/reset-password")
  @PreAuthorize("hasRole('ADMIN')")
  public void resetPassword(@PathVariable Long id, @RequestBody @Valid ResetPasswordRequest in) {
    User u = repo.findById(id).orElseThrow();
    u.setPassword(encoder.encode(in.getNewPassword()));
    repo.save(u);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("user_not_found");
    repo.deleteById(id);
  }

  @GetMapping("/self")
  @PreAuthorize("isAuthenticated()")
  public User self(@AuthenticationPrincipal UserDetails principal) {
    return repo.findByEmail(principal.getUsername()).orElseThrow();
  }

  @PutMapping("/self")
  @PreAuthorize("isAuthenticated()")
  public User updateSelf(@AuthenticationPrincipal UserDetails principal, @RequestBody UserSelfUpdateRequest in) {
    User u = repo.findByEmail(principal.getUsername()).orElseThrow();
    if (in.getFirstName() != null) u.setFirstName(in.getFirstName());
    if (in.getLastName() != null) u.setLastName(in.getLastName());
    if (in.getPhone() != null) u.setPhone(in.getPhone());
    return repo.save(u);
  }

  @PostMapping("/self/change-password")
  @PreAuthorize("isAuthenticated()")
  public void changePassword(@AuthenticationPrincipal UserDetails principal,
                             @RequestBody @Valid ChangePasswordRequest in) {
    User u = repo.findByEmail(principal.getUsername()).orElseThrow();
    if (!encoder.matches(in.getCurrentPassword(), u.getPassword())) {
      throw new IllegalArgumentException("invalid_current_password");
    }
    u.setPassword(encoder.encode(in.getNewPassword()));
    repo.save(u);
  }

  @GetMapping("/search")
  @PreAuthorize("hasRole('ADMIN')")
  public Page<User> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) User.Role role,
      @RequestParam(required = false) User.Status status,
      @RequestParam(required = false) Long clientId,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    Specification<User> spec = (r,qb,cb) -> cb.isNotNull(r.get("id"));

    if (q != null && !q.isBlank()) {
      String like = "%" + q.trim().toLowerCase() + "%";
      spec = spec.and((r,qb,cb) -> cb.or(
          cb.like(cb.lower(r.get("email")), like),
          cb.like(cb.lower(r.get("firstName")), like),
          cb.like(cb.lower(r.get("lastName")), like),
          cb.like(cb.lower(r.get("phone")), like)
      ));
    }

    if (role != null)   spec = spec.and((r,qb,cb) -> cb.equal(r.get("role"), role));
    if (status != null) spec = spec.and((r,qb,cb) -> cb.equal(r.get("status"), status));
    if (clientId != null) {
      spec = spec.and((r,qb,cb) -> cb.and(
          cb.isNotNull(r.get("client")),
          cb.equal(r.get("client").get("id"), clientId)
      ));
    }

    return repo.findAll(spec, pageable);
  }
}
