package com.teo.servicecare.contracts;

import com.teo.servicecare.clients.Client;
import com.teo.servicecare.clients.ClientRepository;
import com.teo.servicecare.common.dto.PageResponse;
import com.teo.servicecare.contracts.dto.ContractCreateRequest;
import com.teo.servicecare.contracts.dto.ContractResponse;
import com.teo.servicecare.contracts.dto.ContractUpdateRequest;
import com.teo.servicecare.sites.Site;
import com.teo.servicecare.sites.SiteRepository;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class ContractService {

  private final ContractRepository repo;
  private final ClientRepository clientRepo;
  private final SiteRepository siteRepo;
  private final UserRepository userRepo;

  public ContractService(ContractRepository repo, ClientRepository clientRepo,
      SiteRepository siteRepo, UserRepository userRepo) {
    this.repo = repo;
    this.clientRepo = clientRepo;
    this.siteRepo = siteRepo;
    this.userRepo = userRepo;
  }

  public PageResponse<ContractResponse> list(Pageable pageable) {
    Page<Contract> page = repo.findAll(pageable);
    return PageResponse.map(page, ContractResponse::from);
  }

  public ContractResponse getVisibleTo(String email, Long id) {
    var current = userRepo.findByEmail(email).orElseThrow();
    var c = repo.findById(id).orElseThrow();

    if (current.getRole() == User.Role.ADMIN) {
      return ContractResponse.from(c);
    }
    var userClient = current.getClient();
    if (userClient != null && c.getClient() != null
        && userClient.getId().equals(c.getClient().getId())) {
      return ContractResponse.from(c);
    }
    throw new org.springframework.security.access.AccessDeniedException("forbidden");
  }

  public ContractResponse create(ContractCreateRequest in) {
    Client client = clientRepo.findById(in.getClientId()).orElseThrow();

    Contract c = new Contract();
    c.setName(in.getName());
    c.setDescription(in.getDescription());
    c.setClient(client);
    c.setStartDate(in.getStartDate());
    c.setEndDate(in.getEndDate());

    c.setAutoRenew(Boolean.TRUE.equals(in.getAutoRenew()));
    c.setNoticeDays(in.getNoticeDays() != null ? in.getNoticeDays() : 30);

    c.setTimezone(in.getTimezone() != null ? in.getTimezone() : "Europe/Paris");
    c.setSupportDays(in.getSupportDays() != null ? in.getSupportDays() : Contract.SupportDays.MON_FRI);
    c.setSupportHoursStart(in.getSupportHoursStart() != null ? in.getSupportHoursStart() : LocalTime.of(9, 0));
    c.setSupportHoursEnd(in.getSupportHoursEnd() != null ? in.getSupportHoursEnd() : LocalTime.of(18, 0));

    c.setMeasureWindow(in.getMeasureWindow() != null ? in.getMeasureWindow() : Contract.MeasureWindow.BUSINESS_HOURS);
    c.setPauseOnWaiting(in.getPauseOnWaiting() == null || in.getPauseOnWaiting());

    c.setRespCritHours(in.getRespCritHours() != null ? in.getRespCritHours() : 1);
    c.setRespHighHours(in.getRespHighHours() != null ? in.getRespHighHours() : 4);
    c.setRespMediumHours(in.getRespMediumHours() != null ? in.getRespMediumHours() : 8);
    c.setRespLowHours(in.getRespLowHours() != null ? in.getRespLowHours() : 24);

    c.setResoCritHours(in.getResoCritHours() != null ? in.getResoCritHours() : 4);
    c.setResoHighHours(in.getResoHighHours() != null ? in.getResoHighHours() : 16);
    c.setResoMediumHours(in.getResoMediumHours() != null ? in.getResoMediumHours() : 40);
    c.setResoLowHours(in.getResoLowHours() != null ? in.getResoLowHours() : 120);

    c.setIncludedHoursMonth(in.getIncludedHoursMonth() != null ? in.getIncludedHoursMonth() : 0);
    c.setMaxTicketsMonth(in.getMaxTicketsMonth() != null ? in.getMaxTicketsMonth() : 0);
    c.setOvertimeRate(in.getOvertimeRate());
    c.setEmergencyRate(in.getEmergencyRate());
    c.setStatus(in.getStatus() != null ? in.getStatus() : Contract.Status.ACTIVE);

    if (in.getSiteIds() != null && !in.getSiteIds().isEmpty()) {
      Set<Site> sitesSet = new HashSet<>(siteRepo.findAllById(in.getSiteIds()));
      c.setSites(sitesSet);
    }

    return ContractResponse.from(repo.save(c));
  }

  public ContractResponse update(Long id, ContractUpdateRequest in) {
    Contract c = repo.findById(id).orElseThrow();

    if (in.getName() != null)
      c.setName(in.getName());
    if (in.getDescription() != null)
      c.setDescription(in.getDescription());

    if (in.getClientId() != null) {
      Client client = clientRepo.findById(in.getClientId()).orElseThrow();
      c.setClient(client);
    }

    if (in.getSiteIds() != null) {
      Set<Site> sites = new HashSet<>(siteRepo.findAllById(in.getSiteIds()));
      c.setSites(sites);
    }

    if (in.getStartDate() != null)
      c.setStartDate(in.getStartDate());
    if (in.getEndDate() != null)
      c.setEndDate(in.getEndDate());
    if (in.getAutoRenew() != null)
      c.setAutoRenew(in.getAutoRenew());
    if (in.getNoticeDays() != null)
      c.setNoticeDays(in.getNoticeDays());

    if (in.getTimezone() != null)
      c.setTimezone(in.getTimezone());
    if (in.getSupportDays() != null)
      c.setSupportDays(in.getSupportDays());
    if (in.getSupportHoursStart() != null)
      c.setSupportHoursStart(in.getSupportHoursStart());
    if (in.getSupportHoursEnd() != null)
      c.setSupportHoursEnd(in.getSupportHoursEnd());

    if (in.getMeasureWindow() != null)
      c.setMeasureWindow(in.getMeasureWindow());
    if (in.getPauseOnWaiting() != null)
      c.setPauseOnWaiting(in.getPauseOnWaiting());

    if (in.getRespCritHours() != null)
      c.setRespCritHours(in.getRespCritHours());
    if (in.getRespHighHours() != null)
      c.setRespHighHours(in.getRespHighHours());
    if (in.getRespMediumHours() != null)
      c.setRespMediumHours(in.getRespMediumHours());
    if (in.getRespLowHours() != null)
      c.setRespLowHours(in.getRespLowHours());

    if (in.getResoCritHours() != null)
      c.setResoCritHours(in.getResoCritHours());
    if (in.getResoHighHours() != null)
      c.setResoHighHours(in.getResoHighHours());
    if (in.getResoMediumHours() != null)
      c.setResoMediumHours(in.getResoMediumHours());
    if (in.getResoLowHours() != null)
      c.setResoLowHours(in.getResoLowHours());

    if (in.getIncludedHoursMonth() != null)
      c.setIncludedHoursMonth(in.getIncludedHoursMonth());
    if (in.getMaxTicketsMonth() != null)
      c.setMaxTicketsMonth(in.getMaxTicketsMonth());
    if (in.getOvertimeRate() != null)
      c.setOvertimeRate(in.getOvertimeRate());
    if (in.getEmergencyRate() != null)
      c.setEmergencyRate(in.getEmergencyRate());

    if (in.getStatus() != null)
      c.setStatus(in.getStatus());

    return ContractResponse.from(repo.save(c));
  }

  public void delete(Long id) {
    if (!repo.existsById(id))
      throw new IllegalArgumentException("contract_not_found");
    repo.deleteById(id);
  }

  public PageResponse<ContractResponse> search(
      String email,
      Long clientId,
      Contract.Status status,
      Contract.MeasureWindow measureWindow,
      Contract.SupportDays supportDays,
      String q,
      String startFrom,
      String startTo,
      String endFrom,
      String endTo,
      Pageable pageable) {
    var current = userRepo.findByEmail(email).orElseThrow();

    Specification<Contract> spec = (r, qb, cb) -> cb.conjunction();

    if (current.getRole() != User.Role.ADMIN) {
      Long scopedClientId = (current.getClient() != null) ? current.getClient().getId() : -1L;
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("client").get("id"), scopedClientId));
    } else if (clientId != null) {
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("client").get("id"), clientId));
    }

    if (status != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("status"), status));
    if (measureWindow != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("measureWindow"), measureWindow));
    if (supportDays != null)
      spec = spec.and((r, qb, cb) -> cb.equal(r.get("supportDays"), supportDays));

    if (q != null && !q.isBlank()) {
      String like = "%" + q.trim().toLowerCase() + "%";
      spec = spec.and((r, qb, cb) -> cb.or(
          cb.like(cb.lower(r.get("name")), like),
          cb.like(r.get("description"), "%" + q.trim() + "%")));
    }

    if (startFrom != null && !startFrom.isBlank()) {
      var d = java.time.LocalDate.parse(startFrom);
      spec = spec.and((r, qb, cb) -> cb.greaterThanOrEqualTo(r.get("startDate"), d));
    }
    if (startTo != null && !startTo.isBlank()) {
      var d = java.time.LocalDate.parse(startTo);
      spec = spec.and((r, qb, cb) -> cb.lessThanOrEqualTo(r.get("startDate"), d));
    }
    if (endFrom != null && !endFrom.isBlank()) {
      var d = java.time.LocalDate.parse(endFrom);
      spec = spec.and((r, qb, cb) -> cb.greaterThanOrEqualTo(r.get("endDate"), d));
    }
    if (endTo != null && !endTo.isBlank()) {
      var d = java.time.LocalDate.parse(endTo);
      spec = spec.and((r, qb, cb) -> cb.lessThanOrEqualTo(r.get("endDate"), d));
    }

    Page<Contract> page = repo.findAll(spec, pageable);
    return PageResponse.map(page, ContractResponse::from);
  }
}
