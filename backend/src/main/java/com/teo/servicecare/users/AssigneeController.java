package com.teo.servicecare.users;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AssigneeController {

  private final UserRepository userRepo;

  public AssigneeController(UserRepository userRepo) {
    this.userRepo = userRepo;
  }

  public static class AssigneeResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    public static AssigneeResponse from(User u) {
      var r = new AssigneeResponse();
      r.id = u.getId();
      r.email = u.getEmail();
      r.firstName = u.getFirstName();
      r.lastName = u.getLastName();
      r.phone = u.getPhone();
      return r;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
  }

  @GetMapping("/assignees")
  @PreAuthorize("hasAnyRole('ADMIN','AGENT','TECHNICIAN')")
  public Page<AssigneeResponse> assignees(
      @RequestParam(required = false) String q,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
  ) {
    Specification<User> roleSpec = (r,qb,cb) ->
        r.get("role").in(User.Role.AGENT, User.Role.TECHNICIAN);

    Specification<User> activeSpec = (r,qb,cb) ->
        cb.equal(r.get("status"), User.Status.ACTIVE);

    Specification<User> spec = roleSpec.and(activeSpec);

    if (q != null && !q.isBlank()) {
      String like = "%" + q.trim().toLowerCase() + "%";
      spec = spec.and((r,qb,cb) ->
          cb.or(
              cb.like(cb.lower(r.get("email")), like),
              cb.like(cb.lower(r.get("firstName")), like),
              cb.like(cb.lower(r.get("lastName")), like)
          )
      );
    }

    return userRepo.findAll(spec, pageable).map(AssigneeResponse::from);
  }
}
