package com.teo.servicecare.security;

import com.teo.servicecare.users.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {
  private final UserRepository repo;

  public UserSecurity(UserRepository repo) {
    this.repo = repo;
  }

  public boolean isSelf(Long id, Authentication auth) {
    if (auth == null || auth.getName() == null) return false;
    return repo.findById(id)
        .map(u -> u.getEmail().equalsIgnoreCase(auth.getName()))
        .orElse(false);
  }
}
