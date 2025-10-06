package com.teo.servicecare.auth;

import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/me")
public class MeController {

  private final UserRepository userRepo;

  public MeController(UserRepository userRepo) {
    this.userRepo = userRepo;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public Map<String, Object> me(@AuthenticationPrincipal UserDetails principal) {
    User u = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    Long clientId = (u.getClient() != null) ? u.getClient().getId() : null;
    String clientName = (u.getClient() != null) ? u.getClient().getName() : null;

    boolean isAdmin = u.getRole() == User.Role.ADMIN;
    boolean isAgent = u.getRole() == User.Role.AGENT;
    boolean isTechnician = u.getRole() == User.Role.TECHNICIAN;
    boolean isClient = u.getRole() == User.Role.CLIENT;

    Map<String, Boolean> permissions = Map.of(
        "canManageTickets", (isAdmin || isAgent || isTechnician),
        "canCommentInternal", (isAdmin || isAgent || isTechnician),
        "canCreateTicket", (isAdmin || isAgent || isTechnician || isClient),
        "canPlanIntervention", (isAdmin || isAgent || isTechnician),
        "isClientScoped", isClient);

    return Map.ofEntries(
        Map.entry("id", u.getId()),
        Map.entry("email", u.getEmail()),
        Map.entry("firstName", u.getFirstName()),
        Map.entry("lastName", u.getLastName()),
        Map.entry("phone", u.getPhone()),
        Map.entry("role", u.getRole().name()),
        Map.entry("status", u.getStatus().name()),
        Map.entry("timezone", u.getTimezone()),
        Map.entry("avatarUrl", u.getAvatarUrl()),
        Map.entry("clientId", clientId),
        Map.entry("clientName", clientName),
        Map.entry("permissions", permissions));
  }
}
