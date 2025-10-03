package com.teo.servicecare.tickets.attachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketAttachmentRepository
    extends JpaRepository<TicketAttachment, Long>, JpaSpecificationExecutor<TicketAttachment> {}
