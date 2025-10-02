package com.teo.servicecare.tickets.worklog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketWorkLogRepository
    extends JpaRepository<TicketWorkLog, Long>, JpaSpecificationExecutor<TicketWorkLog> {}
