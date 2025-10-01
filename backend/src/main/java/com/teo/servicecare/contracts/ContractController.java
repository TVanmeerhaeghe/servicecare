package com.teo.servicecare.contracts;

import com.teo.servicecare.clients.Client;
import com.teo.servicecare.clients.ClientRepository;
import com.teo.servicecare.sites.Site;
import com.teo.servicecare.sites.SiteRepository;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

  private final ContractRepository repo;
  private final ClientRepository clientRepo;
  private final SiteRepository siteRepo;
  private final UserRepository userRepo;

  public ContractController(ContractRepository repo,
                            ClientRepository clientRepo,
                            SiteRepository siteRepo,
                            UserRepository userRepo) {
    this.repo = repo;
    this.clientRepo = clientRepo;
    this.siteRepo = siteRepo;
    this.userRepo = userRepo;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<Contract> all() {
    return repo.findAll();
  }

  @GetMapping("/{id}")
  public Contract one(@PathVariable Long id,
                      @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {

    Contract c = repo.findById(id).orElseThrow();

    if (principal == null) {
      throw new AccessDeniedException("unauthorized");
    }

    User current = userRepo.findByEmail(principal.getUsername()).orElseThrow();

    if (current.getRole() == User.Role.ADMIN) {
      return c;
    }

    if (current.getClient() != null && c.getClient() != null
        && current.getClient().getId().equals(c.getClient().getId())) {
      return c;
    }

    throw new AccessDeniedException("forbidden");
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Contract create(@RequestBody @Valid ContractCreateRequest in) {
    Client client = clientRepo.findById(in.getClientId()).orElseThrow();

    Set<Site> sites = new HashSet<>();
    if (in.getSiteIds() != null && !in.getSiteIds().isEmpty()) {
      for (Long siteId : in.getSiteIds()) {
        sites.add(siteRepo.findById(siteId).orElseThrow());
      }
    }

    Contract c = new Contract();
    c.setName(in.getName());
    c.setDescription(in.getDescription());
    c.setClient(client);
    c.setSites(sites);

    c.setStartDate(in.getStartDate());
    c.setEndDate(in.getEndDate());
    c.setAutoRenew(in.isAutoRenew());
    c.setNoticeDays(in.getNoticeDays());

    c.setTimezone(in.getTimezone());
    c.setSupportDays(Contract.SupportDays.valueOf(in.getSupportDays()));
    c.setSupportHoursStart(in.getSupportHoursStart());
    c.setSupportHoursEnd(in.getSupportHoursEnd());

    c.setRespCritHours(in.getRespCritHours());
    c.setRespHighHours(in.getRespHighHours());
    c.setRespMediumHours(in.getRespMediumHours());
    c.setRespLowHours(in.getRespLowHours());

    c.setResoCritHours(in.getResoCritHours());
    c.setResoHighHours(in.getResoHighHours());
    c.setResoMediumHours(in.getResoMediumHours());
    c.setResoLowHours(in.getResoLowHours());

    c.setIncludedHoursMonth(in.getIncludedHoursMonth());
    c.setMaxTicketsMonth(in.getMaxTicketsMonth());
    c.setOvertimeRate(in.getOvertimeRate());
    c.setEmergencyRate(in.getEmergencyRate());

    c.setStatus(in.getStatus());

    return repo.save(c);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Contract update(@PathVariable Long id, @RequestBody ContractUpdateRequest in) {
    Contract c = repo.findById(id).orElseThrow();

    if (in.getName() != null) c.setName(in.getName());
    if (in.getDescription() != null) c.setDescription(in.getDescription());

    if (in.getClientId() != null) {
      Client client = clientRepo.findById(in.getClientId()).orElseThrow();
      c.setClient(client);
    }

    if (in.getSiteIds() != null) {
      Set<Site> sites = new HashSet<>();
      for (Long siteId : in.getSiteIds()) {
        sites.add(siteRepo.findById(siteId).orElseThrow());
      }
      c.setSites(sites);
    }

    if (in.getStartDate() != null) c.setStartDate(in.getStartDate());
    if (in.getEndDate() != null) c.setEndDate(in.getEndDate());
    if (in.getAutoRenew() != null) c.setAutoRenew(in.getAutoRenew());
    if (in.getNoticeDays() != null) c.setNoticeDays(in.getNoticeDays());

    if (in.getTimezone() != null) c.setTimezone(in.getTimezone());
    if (in.getSupportDays() != null) c.setSupportDays(Contract.SupportDays.valueOf(in.getSupportDays()));
    if (in.getSupportHoursStart() != null) c.setSupportHoursStart(in.getSupportHoursStart());
    if (in.getSupportHoursEnd() != null) c.setSupportHoursEnd(in.getSupportHoursEnd());

    if (in.getRespCritHours() != null) c.setRespCritHours(in.getRespCritHours());
    if (in.getRespHighHours() != null) c.setRespHighHours(in.getRespHighHours());
    if (in.getRespMediumHours() != null) c.setRespMediumHours(in.getRespMediumHours());
    if (in.getRespLowHours() != null) c.setRespLowHours(in.getRespLowHours());

    if (in.getResoCritHours() != null) c.setResoCritHours(in.getResoCritHours());
    if (in.getResoHighHours() != null) c.setResoHighHours(in.getResoHighHours());
    if (in.getResoMediumHours() != null) c.setResoMediumHours(in.getResoMediumHours());
    if (in.getResoLowHours() != null) c.setResoLowHours(in.getResoLowHours());

    if (in.getIncludedHoursMonth() != null) c.setIncludedHoursMonth(in.getIncludedHoursMonth());
    if (in.getMaxTicketsMonth() != null) c.setMaxTicketsMonth(in.getMaxTicketsMonth());
    if (in.getOvertimeRate() != null) c.setOvertimeRate(in.getOvertimeRate());
    if (in.getEmergencyRate() != null) c.setEmergencyRate(in.getEmergencyRate());

    if (in.getStatus() != null) c.setStatus(in.getStatus());

    return repo.save(c);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    if (!repo.existsById(id)) {
      throw new IllegalArgumentException("contract_not_found");
    }
    repo.deleteById(id);
  }
}
