package com.teo.servicecare.users;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserRepository repo;
  private final PasswordEncoder encoder;

  public UserController(UserRepository repo, PasswordEncoder encoder) {
    this.repo = repo;
    this.encoder = encoder;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<User> all() {
    return repo.findAll();
  }

  @GetMapping("/{id}")
  @PostAuthorize("hasRole('ADMIN') or returnObject.email == authentication.name")
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
    return repo.save(u);
  }
}
