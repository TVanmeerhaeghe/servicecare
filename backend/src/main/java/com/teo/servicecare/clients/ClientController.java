package com.teo.servicecare.clients;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.teo.servicecare.clients.Client.ClientStatus;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
  private final ClientRepository repo;

  public ClientController(ClientRepository repo) { this.repo = repo; }

  @GetMapping
  public List<Client> all() { return repo.findAll(); }

  @GetMapping("/{id}")
  public Client one(@PathVariable Long id) { return repo.findById(id).orElseThrow(); }

  @PostMapping
  public Client create(@RequestBody @Valid ClientCreateRequest in) {
    if (repo.existsByName(in.getName())) throw new IllegalArgumentException("client_name_already_used");

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
}
