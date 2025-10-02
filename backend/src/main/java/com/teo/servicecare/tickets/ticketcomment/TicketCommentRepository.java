package com.teo.servicecare.tickets.ticketcomment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketCommentRepository
    extends JpaRepository<TicketComment, Long>, JpaSpecificationExecutor<TicketComment> {}
