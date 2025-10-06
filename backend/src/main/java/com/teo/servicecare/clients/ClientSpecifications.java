package com.teo.servicecare.clients;

import com.teo.servicecare.clients.Client.ClientStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

final class ClientSpecifications {

  private ClientSpecifications() {
  }

  static Specification<Client> truePredicate() {
    return (r, q, cb) -> cb.conjunction();
  }

  static Specification<Client> byId(Long id) {
    return (r, q, cb) -> cb.equal(r.get("id"), id);
  }

  static Specification<Client> nameLike(String q) {
    String like = "%" + q.trim().toLowerCase() + "%";
    return (r, qy, cb) -> cb.like(cb.lower(r.get("name")), like);
  }

  static Specification<Client> statusEquals(ClientStatus status) {
    return (r, q, cb) -> cb.equal(r.get("status"), status);
  }

  static Specification<Client> fullText(String q) {
    String like = "%" + q.trim().toLowerCase() + "%";
    return (r, qy, cb) -> cb.or(
        cb.like(cb.lower(r.get("name")), like),
        cb.like(cb.lower(r.get("legalName")), like),
        cb.like(cb.lower(r.get("contactFirstName")), like),
        cb.like(cb.lower(r.get("contactLastName")), like),
        cb.like(cb.lower(r.get("contactEmail")), like),
        cb.like(cb.lower(r.get("billingEmail")), like),
        cb.like(cb.lower(r.get("technicalEmail")), like),
        cb.like(cb.lower(r.get("websiteUrl")), like),
        cb.like(cb.lower(r.get("vatNumber")), like),
        cb.like(cb.lower(r.get("siret")), like),
        cb.like(cb.lower(r.get("city")), like));
  }

  static Specification<Client> createdAtGte(Instant from) {
    return (r, q, cb) -> cb.greaterThanOrEqualTo(r.get("createdAt"), from);
  }

  static Specification<Client> createdAtLt(Instant toExcl) {
    return (r, q, cb) -> cb.lessThan(r.get("createdAt"), toExcl);
  }
}
