package com.teo.servicecare.jobs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.teo.servicecare.tickets.Ticket;
import com.teo.servicecare.tickets.TicketRepository;

import java.time.LocalDateTime;

@Component
public class TicketSlaBreachJob {

  private final TicketRepository repo;

  public TicketSlaBreachJob(TicketRepository repo) {
    this.repo = repo;
  }

  @Scheduled(cron = "0 */5 * * * *")
  public void markBreaches() {
    var now = LocalDateTime.now(java.time.ZoneId.of("Europe/Paris"));

    Specification<Ticket> notDeleted = (r,q,cb) -> cb.isNull(r.get("deletedAt"));
    Specification<Ticket> respBreached = (r,q,cb) ->
        cb.and(cb.isNull(r.get("respondedAt")), cb.lessThan(r.get("respondBy"), now));
    Specification<Ticket> resoBreached = (r,q,cb) ->
        cb.and(cb.isNull(r.get("resolvedAt")), cb.lessThan(r.get("resolveBy"), now));

    var spec = notDeleted.and(respBreached.or(resoBreached));

    repo.findAll(spec).forEach(t -> {
      if (!t.isSlaBreached()) {
        t.setSlaBreached(true);
        repo.save(t);
      }
    });
  }
}
