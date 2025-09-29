package com.teo.servicecare.users;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserRepository repo;
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public UserController(UserRepository repo) { this.repo = repo; }

  @GetMapping
  public List<User> all() { return repo.findAll(); }

  @GetMapping("/{id}")
  public User one(@PathVariable Long id) { return repo.findById(id).orElseThrow(); }

  @PostMapping
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
