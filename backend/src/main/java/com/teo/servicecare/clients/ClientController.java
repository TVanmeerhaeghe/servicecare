package com.teo.servicecare.clients;

import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.teo.servicecare.clients.Client.ClientStatus;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
  private final ClientRepository repo;
  private final UserRepository userRepo;

  public ClientController(ClientRepository repo, UserRepository userRepo) {
    this.repo = repo;
    this.userRepo = userRepo;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public org.springframework.data.domain.Page<Client> all(
      @org.springframework.data.web.PageableDefault(size = 20, sort = "id",
          direction = org.springframework.data.domain.Sort.Direction.DESC)
      org.springframework.data.domain.Pageable pageable) {
    return repo.findAll(pageable);
  }

  @GetMapping("/{id}")
  public Client one(@PathVariable Long id,
                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
    Client client = repo.findById(id).orElseThrow();

    if (principal == null) {
      throw new AccessDeniedException("unauthorized");
    }

    String email = principal.getUsername();
    User current = userRepo.findByEmail(email).orElseThrow();

    if (current.getRole() == User.Role.ADMIN) {
      return client;
    }

    if (current.getClient() != null && client.getId().equals(current.getClient().getId())) {
      return client;
    }

    throw new AccessDeniedException("forbidden");
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Client create(@RequestBody @Valid ClientCreateRequest in) {
    if (repo.existsByName(in.getName())) {
      throw new IllegalArgumentException("client_name_already_used");
    }

    var c = new Client();
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
    c.setStatus(in.getStatus() != null ? in.getStatus() : ClientStatus.ACTIVE);

    return repo.save(c);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Client update(@PathVariable Long id, @RequestBody ClientUpdateRequest in) {
    Client c = repo.findById(id).orElseThrow();

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

    return repo.save(c);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    if (!repo.existsById(id)) {
      throw new IllegalArgumentException("client_not_found");
    }
    repo.deleteById(id);
  }
}
