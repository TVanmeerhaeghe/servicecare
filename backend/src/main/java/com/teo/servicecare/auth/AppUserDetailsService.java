package com.teo.servicecare.auth;

import com.teo.servicecare.users.User;
import com.teo.servicecare.users.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
  private final UserRepository userRepo;

  public AppUserDetailsService(UserRepository userRepo) {
    this.userRepo = userRepo;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User u = userRepo.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("user_not_found"));
    if (u.getStatus() == User.Status.DISABLED)
      throw new UsernameNotFoundException("user_disabled");
    return org.springframework.security.core.userdetails.User
        .withUsername(u.getEmail())
        .password(u.getPassword())
        .roles(u.getRole().name())
        .build();
  }
}
