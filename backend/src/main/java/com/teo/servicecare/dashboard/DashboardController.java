package com.teo.servicecare.dashboard;

import com.teo.servicecare.dashboard.dto.DashboardOverviewResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final DashboardService service;

  public DashboardController(DashboardService service) {
    this.service = service;
  }

  @GetMapping("/overview")
  @PreAuthorize("isAuthenticated()")
  public DashboardOverviewResponse overview(@AuthenticationPrincipal UserDetails principal) {
    return service.overviewFor(principal.getUsername());
  }
}
