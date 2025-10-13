package com.teo.servicecare.tickets.sla;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketSlaEventRepository extends JpaRepository<TicketSlaEvent, Long> {
    java.util.List<TicketSlaEvent> findByTicket_IdOrderByHappenedAtDesc(Long ticketId);
}