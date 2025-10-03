package com.teo.servicecare.auth;

import com.teo.servicecare.auth.dto.LoginRequest;
import com.teo.servicecare.auth.dto.LoginResponse;
import com.teo.servicecare.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public LoginResponse login(@RequestBody @Valid LoginRequest in) {
    String jwt = authService.login(in);
    return new LoginResponse(jwt);
  }

  @PostMapping("/register")
  @PreAuthorize("hasRole('ADMIN')")
  public LoginResponse register(@RequestBody @Valid RegisterRequest in) {
    String jwt = authService.register(in);
    return new LoginResponse(jwt);
  }
}
