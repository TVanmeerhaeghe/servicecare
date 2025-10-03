package com.teo.servicecare.users;

import com.teo.servicecare.clients.ClientRepository;
import com.teo.servicecare.users.dto.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository repo;
  private final ClientRepository clientRepo;
  private final PasswordEncoder encoder;

  public UserService(UserRepository repo, ClientRepository clientRepo, PasswordEncoder encoder) {
    this.repo = repo;
    this.clientRepo = clientRepo;
    this.encoder = encoder;
  }

  public Page<User> list(Pageable pageable) {
    return repo.findAll(pageable);
  }

  public User get(Long id) {
    return repo.findById(id).orElseThrow();
  }

  public User create(UserCreateRequest in) {
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

  public User updateAdmin(Long id, UserUpdateRequest in) {
    var u = repo.findById(id).orElseThrow();

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

  public void resetPassword(Long id, ResetPasswordRequest in) {
    var u = repo.findById(id).orElseThrow();
    u.setPassword(encoder.encode(in.getNewPassword()));
    repo.save(u);
  }

  public void delete(Long id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("user_not_found");
    repo.deleteById(id);
  }

  public User getSelf(String email) {
    return repo.findByEmail(email).orElseThrow();
  }

  public User updateSelf(String email, UserSelfUpdateRequest in) {
    var u = repo.findByEmail(email).orElseThrow();
    if (in.getFirstName() != null) u.setFirstName(in.getFirstName());
    if (in.getLastName() != null) u.setLastName(in.getLastName());
    if (in.getPhone() != null) u.setPhone(in.getPhone());
    return repo.save(u);
  }

  public void changePassword(String email, ChangePasswordRequest in) {
    var u = repo.findByEmail(email).orElseThrow();
    if (!encoder.matches(in.getCurrentPassword(), u.getPassword())) {
      throw new IllegalArgumentException("invalid_current_password");
    }
    u.setPassword(encoder.encode(in.getNewPassword()));
    repo.save(u);
  }

  public Page<User> search(String q, User.Role role, User.Status status, Long clientId, Pageable pageable) {
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

  public Page<AssigneeResponse> assignees(String q, Pageable pageable) {
    Specification<User> roleSpec = (r,qb,cb) ->
        r.get("role").in(User.Role.AGENT, User.Role.TECHNICIAN);
    Specification<User> activeSpec = (r,qb,cb) ->
        cb.equal(r.get("status"), User.Status.ACTIVE);

    Specification<User> spec = roleSpec.and(activeSpec);

    if (q != null && !q.isBlank()) {
      String like = "%" + q.trim().toLowerCase() + "%";
      spec = spec.and((r,qb,cb) -> cb.or(
          cb.like(cb.lower(r.get("email")), like),
          cb.like(cb.lower(r.get("firstName")), like),
          cb.like(cb.lower(r.get("lastName")), like)
      ));
    }

    return repo.findAll(spec, pageable).map(AssigneeResponse::from);
  }
}
