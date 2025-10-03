package com.teo.servicecare.dashboard;

import com.teo.servicecare.dashboard.dto.DashboardOverviewResponse;
import com.teo.servicecare.tickets.Ticket;
import com.teo.servicecare.tickets.TicketRepository;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.Set;

@Service
public class DashboardService {

  private static final ZoneId APP_ZONE = ZoneId.of("Europe/Paris");

  private final TicketRepository ticketRepo;
  private final UserRepository userRepo;

  public DashboardService(TicketRepository ticketRepo, UserRepository userRepo) {
    this.ticketRepo = ticketRepo;
    this.userRepo = userRepo;
  }

  public DashboardOverviewResponse overviewFor(String email) {
    User current = userRepo.findByEmail(email).orElseThrow();

    boolean privileged =
        current.getRole() == User.Role.ADMIN
     || current.getRole() == User.Role.AGENT
     || current.getRole() == User.Role.TECHNICIAN;

    Long scopedClientId = privileged ? null : (current.getClient() != null ? current.getClient().getId() : -1L);

    LocalDate today = LocalDate.now(APP_ZONE);
    LocalDateTime todayStart = today.atStartOfDay();
    LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();

    Set<Ticket.TicketStatus> openStatuses = EnumSet.of(
        Ticket.TicketStatus.OPEN, Ticket.TicketStatus.ASSIGNED,
        Ticket.TicketStatus.IN_PROGRESS, Ticket.TicketStatus.WAITING
    );
    Set<Ticket.TicketStatus> closedOrCanceled = EnumSet.of(
        Ticket.TicketStatus.CLOSED, Ticket.TicketStatus.CANCELED
    );

    long openCount =
        ticketRepo.countByDeletedAtIsNullAndStatusInAndClientIdScope(openStatuses, scopedClientId);

    long breachedOpenCount =
        ticketRepo.countBreachedOpen(scopedClientId);

    Double avgRespMinutes = ticketRepo.avgResponseMinutes(scopedClientId);
    Double avgResoMinutes = ticketRepo.avgResolveMinutes(scopedClientId);

    double avgResponseHours = round1(((avgRespMinutes != null) ? avgRespMinutes : 0.0) / 60.0);
    double avgResolveHours  = round1(((avgResoMinutes != null) ? avgResoMinutes : 0.0) / 60.0);

    long todayNewTickets =
        ticketRepo.countCreatedBetween(scopedClientId, todayStart, tomorrowStart);

    Long me = current.getId();
    long myAssignedOpen =
        ticketRepo.countMyAssignedOpen(me, scopedClientId, closedOrCanceled);

    return new DashboardOverviewResponse(
        openCount,
        breachedOpenCount,
        avgResponseHours,
        avgResolveHours,
        todayNewTickets,
        myAssignedOpen
    );
  }

  private double round1(double v) {
    return Math.round(v * 10.0) / 10.0;
  }
}
