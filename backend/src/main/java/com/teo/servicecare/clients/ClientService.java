package com.teo.servicecare.clients;

import com.teo.servicecare.clients.Client.ClientStatus;
import com.teo.servicecare.clients.dto.ClientCreateRequest;
import com.teo.servicecare.clients.dto.ClientUpdateRequest; 
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ClientService {

  private final ClientRepository repo;
  private final UserRepository userRepo;

  public ClientService(ClientRepository repo, UserRepository userRepo) {
    this.repo = repo;
    this.userRepo = userRepo;
  }

  public Page<Client> list(Pageable pageable) {
    return repo.findAll(pageable);
  }

  public Client getVisibleTo(String email, Long id) {
    var client = repo.findById(id).orElseThrow();
    var current = userRepo.findByEmail(email).orElseThrow();

    if (current.getRole() == User.Role.ADMIN) return client;

    if (current.getClient() != null && client.getId().equals(current.getClient().getId())) {
      return client;
    }

    throw new AccessDeniedException("forbidden");
  }

  public Page<Map<String, Object>> lookup(String email, String q, Pageable pageable) {
    var current = userRepo.findByEmail(email).orElseThrow();

    Specification<Client> spec = ClientSpecifications.truePredicate();

    if (current.getRole() == User.Role.CLIENT) {
      Long cid = (current.getClient() != null) ? current.getClient().getId() : -1L;
      spec = spec.and(ClientSpecifications.byId(cid));
    }

    if (q != null && !q.isBlank()) {
      spec = spec.and(ClientSpecifications.nameLike(q));
    }

    return repo.findAll(spec, pageable).map(c ->
        Map.of("id", c.getId(), "name", c.getName())
    );
  }

  public Page<Client> search(
      ClientStatus status,
      String q,
      String createdFrom,
      String createdTo,
      Pageable pageable
  ) {
    Specification<Client> spec = ClientSpecifications.truePredicate();

    if (status != null) spec = spec.and(ClientSpecifications.statusEquals(status));
    if (q != null && !q.isBlank()) spec = spec.and(ClientSpecifications.fullText(q));

    ZoneId tz = ZoneId.of("Europe/Paris");
    if (createdFrom != null && !createdFrom.isBlank()) {
      var from = LocalDate.parse(createdFrom).atStartOfDay(tz).toInstant();
      spec = spec.and(ClientSpecifications.createdAtGte(from));
    }
    if (createdTo != null && !createdTo.isBlank()) {
      var toExcl = LocalDate.parse(createdTo).plusDays(1).atStartOfDay(tz).toInstant();
      spec = spec.and(ClientSpecifications.createdAtLt(toExcl));
    }

    return repo.findAll(spec, pageable);
  }

  @Transactional
  public Client create(@Valid ClientCreateRequest in) {
    if (repo.existsByName(in.getName())) {
      throw new IllegalArgumentException("client_name_already_used");
    }
    var c = new Client();
    copyCreate(in, c);
    return repo.save(c);
  }

  @Transactional
  public Client update(Long id, ClientUpdateRequest in) {
    var c = repo.findById(id).orElseThrow();

    if (in.getName() != null && !in.getName().equals(c.getName())) {
      if (repo.existsByNameAndIdNot(in.getName(), id)) {
        throw new IllegalArgumentException("client_name_already_used");
      }
    }

    copyUpdate(in, c);
    return repo.save(c);
  }

  @Transactional
  public void delete(Long id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("client_not_found");
    repo.deleteById(id);
  }

  private void copyCreate(ClientCreateRequest in, Client c) {
    c.setName(in.getName());
    c.setLegalName(in.getLegalName());
    c.setSiret(in.getSiret());
    c.setVatNumber(in.getVatNumber());
    c.setContactFirstName(in.getContactFirstName());
    c.setContactLastName(in.getContactLastName());
    c.setContactEmail(in.getContactEmail());
    c.setContactPhone(in.getContactPhone());
    c.setBillingEmail(in.getBillingEmail());
    c.setTechnicalEmail(in.getTechnicalEmail());
    c.setWebsiteUrl(in.getWebsiteUrl());
    c.setAddressLine1(in.getAddressLine1());
    c.setPostalCode(in.getPostalCode());
    c.setCity(in.getCity());
    c.setCountryCode(in.getCountryCode());
    c.setCurrencyCode(in.getCurrencyCode());
    c.setStatus(in.getStatus() != null ? in.getStatus() : Client.ClientStatus.ACTIVE);
  }

  private void copyUpdate(ClientUpdateRequest in, Client c) {
    if (in.getName() != null) c.setName(in.getName());
    if (in.getLegalName() != null) c.setLegalName(in.getLegalName());
    if (in.getSiret() != null) c.setSiret(in.getSiret());
    if (in.getVatNumber() != null) c.setVatNumber(in.getVatNumber());
    if (in.getContactFirstName() != null) c.setContactFirstName(in.getContactFirstName());
    if (in.getContactLastName() != null) c.setContactLastName(in.getContactLastName());
    if (in.getContactEmail() != null) c.setContactEmail(in.getContactEmail());
    if (in.getContactPhone() != null) c.setContactPhone(in.getContactPhone());
    if (in.getBillingEmail() != null) c.setBillingEmail(in.getBillingEmail());
    if (in.getTechnicalEmail() != null) c.setTechnicalEmail(in.getTechnicalEmail());
    if (in.getWebsiteUrl() != null) c.setWebsiteUrl(in.getWebsiteUrl());
    if (in.getAddressLine1() != null) c.setAddressLine1(in.getAddressLine1());
    if (in.getPostalCode() != null) c.setPostalCode(in.getPostalCode());
    if (in.getCity() != null) c.setCity(in.getCity());
    if (in.getCountryCode() != null) c.setCountryCode(in.getCountryCode());
    if (in.getCurrencyCode() != null) c.setCurrencyCode(in.getCurrencyCode());
    if (in.getStatus() != null) c.setStatus(in.getStatus());
  }
}
