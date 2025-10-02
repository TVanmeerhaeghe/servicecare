package com.teo.servicecare.dashboard;

import com.teo.servicecare.tickets.Ticket;
import com.teo.servicecare.tickets.TicketRepository;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final TicketRepository ticketRepo;
  private final UserRepository userRepo;

  public DashboardController(TicketRepository ticketRepo, UserRepository userRepo) {
    this.ticketRepo = ticketRepo;
    this.userRepo = userRepo;
  }

  @GetMapping("/overview")
  @PreAuthorize("isAuthenticated()")
  public Map<String, Object> overview(@AuthenticationPrincipal UserDetails principal) {
    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    boolean isPrivileged = current.getRole() == User.Role.ADMIN
        || current.getRole() == User.Role.AGENT
        || current.getRole() == User.Role.TECHNICIAN;

    final Long scopedClientId = isPrivileged
        ? null
        : (current.getClient() != null ? current.getClient().getId() : -1L);

    var all = ticketRepo.findAll();

    java.util.function.Predicate<Ticket> notDeleted = t -> t.getDeletedAt() == null;
    java.util.function.Predicate<Ticket> scoped = t -> (scopedClientId == null) || scopedClientId.equals(t.getClientId());

    long open = all.stream().filter(notDeleted).filter(scoped).filter(t ->
        t.getStatus() == Ticket.TicketStatus.OPEN
            || t.getStatus() == Ticket.TicketStatus.ASSIGNED
            || t.getStatus() == Ticket.TicketStatus.IN_PROGRESS
            || t.getStatus() == Ticket.TicketStatus.WAITING
    ).count();

    long breachedOpen = all.stream().filter(notDeleted).filter(scoped).filter(t ->
        (t.getStatus() != Ticket.TicketStatus.CLOSED && t.getStatus() != Ticket.TicketStatus.CANCELED)
            && t.isSlaBreached()
    ).count();

    double avgResponseHours = all.stream().filter(notDeleted).filter(scoped)
        .filter(t -> t.getRespondedAt() != null && t.getCreatedAt() != null)
        .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getRespondedAt()).toMinutes())
        .average().orElse(0.0) / 60.0;

    double avgResolveHours = all.stream().filter(notDeleted).filter(scoped)
        .filter(t -> t.getResolvedAt() != null && t.getCreatedAt() != null)
        .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getResolvedAt()).toMinutes())
        .average().orElse(0.0) / 60.0;

    LocalDate today = LocalDate.now();
    long todayNew = all.stream().filter(notDeleted).filter(scoped).filter(t -> {
      LocalDateTime c = t.getCreatedAt();
      return c != null && c.toLocalDate().isEqual(today);
    }).count();

    final Long me = current.getId();
    long myAssignedOpen = all.stream().filter(notDeleted).filter(scoped).filter(t ->
        me != null
            && me.equals(t.getAssigneeUserId())
            && t.getStatus() != Ticket.TicketStatus.CLOSED
            && t.getStatus() != Ticket.TicketStatus.CANCELED
    ).count();

    return Map.of(
        "openCount", open,
        "breachedOpenCount", breachedOpen,
        "avgResponseHours", Math.round(avgResponseHours * 10.0) / 10.0,
        "avgResolveHours", Math.round(avgResolveHours * 10.0) / 10.0,
        "todayNewTickets", todayNew,
        "myAssignedOpen", myAssignedOpen
    );
  }
}
