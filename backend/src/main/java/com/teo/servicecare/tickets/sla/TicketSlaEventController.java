package com.teo.servicecare.tickets.sla;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/sla-events")
public class TicketSlaEventController {
    private final TicketSlaEventRepository repo;

    public TicketSlaEventController(TicketSlaEventRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<TicketSlaEvent> list(@PathVariable Long ticketId) {
        return repo.findByTicket_IdOrderByHappenedAtDesc(ticketId);
    }
}