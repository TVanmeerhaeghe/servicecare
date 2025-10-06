package com.teo.servicecare.tickets;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Set;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

  // Count tickets open (not deleted + status in open) with optional client scope
  @Query("""
        select count(t) from Ticket t
        where t.deletedAt is null
          and t.status in :statuses
          and (:clientId is null or t.clientId = :clientId)
      """)
  long countByDeletedAtIsNullAndStatusInAndClientIdScope(@Param("statuses") Set<Ticket.TicketStatus> statuses,
      @Param("clientId") Long clientId);

  // Count breached (not closed/canceled, slaBreached = true)
  @Query("""
        select count(t) from Ticket t
        where t.deletedAt is null
          and t.slaBreached = true
          and t.status <> com.teo.servicecare.tickets.Ticket$TicketStatus.CLOSED
          and t.status <> com.teo.servicecare.tickets.Ticket$TicketStatus.CANCELED
          and (:clientId is null or t.clientId = :clientId)
      """)
  long countBreachedOpen(@Param("clientId") Long clientId);

  // Count created today
  @Query("""
        select count(t) from Ticket t
        where t.deletedAt is null
          and t.createdAt >= :from and t.createdAt < :to
          and (:clientId is null or t.clientId = :clientId)
      """)
  long countCreatedBetween(@Param("clientId") Long clientId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);

  // Count my assigned (not closed/canceled)
  @Query("""
        select count(t) from Ticket t
        where t.deletedAt is null
          and t.assigneeUserId = :assigneeId
          and t.status not in :excluded
          and (:clientId is null or t.clientId = :clientId)
      """)
  long countMyAssignedOpen(@Param("assigneeId") Long assigneeId,
      @Param("clientId") Long clientId,
      @Param("excluded") Set<Ticket.TicketStatus> excluded);

  // --- averages ---

  // average minutes from createdAt to respondedAt, only where respondedAt not
  // null
  @Query(value = """
        SELECT AVG(TIMESTAMPDIFF(MINUTE, created_at, responded_at))
        FROM tickets
        WHERE deleted_at IS NULL
          AND responded_at IS NOT NULL
          AND ( :clientId IS NULL OR client_id = :clientId )
      """, nativeQuery = true)
  Double avgResponseMinutes(@Param("clientId") Long clientId);

  // average minutes from createdAt to resolvedAt, only where resolvedAt not null
  @Query(value = """
        SELECT AVG(TIMESTAMPDIFF(MINUTE, created_at, resolved_at))
        FROM tickets
        WHERE deleted_at IS NULL
          AND resolved_at IS NOT NULL
          AND ( :clientId IS NULL OR client_id = :clientId )
      """, nativeQuery = true)
  Double avgResolveMinutes(@Param("clientId") Long clientId);
}
