package com.teo.servicecare.auth;

import com.teo.servicecare.auth.dto.LoginRequest;
import com.teo.servicecare.auth.dto.RegisterRequest;
import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authManager;
  private final JwtService jwtService;
  private final UserRepository userRepo;
  private final PasswordEncoder encoder;

  public AuthService(AuthenticationManager authManager,
                     JwtService jwtService,
                     UserRepository userRepo,
                     PasswordEncoder encoder) {
    this.authManager = authManager;
    this.jwtService = jwtService;
    this.userRepo = userRepo;
    this.encoder = encoder;
  }

  public String login(LoginRequest in) {
    var token = new UsernamePasswordAuthenticationToken(in.getEmail(), in.getPassword());
    authManager.authenticate(token);
    return jwtService.generateToken(in.getEmail());
  }

  public String register(RegisterRequest in) {
    if (userRepo.existsByEmail(in.getEmail())) {
      throw new IllegalArgumentException("email_already_used");
    }
    User u = new User();
    u.setEmail(in.getEmail());
    u.setPassword(encoder.encode(in.getPassword()));
    u.setFirstName(in.getFirstName());
    u.setLastName(in.getLastName());
    u.setPhone(in.getPhone());
    u.setRole(User.Role.CLIENT);
    u.setStatus(User.Status.ACTIVE);
    userRepo.save(u);

    return jwtService.generateToken(u.getEmail());
  }
}
