package com.teo.servicecare.auth;

import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import jakarta.validation.constraints.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UserRepository userRepo;
  private final PasswordEncoder encoder;

  public AuthController(AuthenticationManager authManager, JwtService jwtService,
                        UserRepository userRepo, PasswordEncoder encoder) {
    this.authManager = authManager;
    this.jwtService = jwtService;
    this.userRepo = userRepo;
    this.encoder = encoder;
  }

  public static class LoginRequest {
    @Email @NotBlank public String email;
    @NotBlank public String password;
  }
  public static class AuthResponse {
    public String token;
    public AuthResponse(String token) { this.token = token; }
  }
  public static class RegisterRequest {
    @Email @NotBlank public String email;
    @NotBlank @Size(min=8) public String password;
    @NotBlank public String firstName;
    @NotBlank public String lastName;
    public String phone;
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody LoginRequest in) {
    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(in.email, in.password)
    );
    return new AuthResponse(jwtService.generateToken(in.email));
  }

  @PostMapping("/register")
  public AuthResponse register(@RequestBody RegisterRequest in) {
    if (userRepo.existsByEmail(in.email)) throw new IllegalArgumentException("email_already_used");

    User u = new User();
    u.setEmail(in.email);
    u.setPassword(encoder.encode(in.password));
    u.setFirstName(in.firstName);
    u.setLastName(in.lastName);
    u.setPhone(in.phone);
    u.setRole(User.Role.AGENT);
    u.setStatus(User.Status.ACTIVE);
    userRepo.save(u);

    return new AuthResponse(jwtService.generateToken(u.getEmail()));
  }
}
