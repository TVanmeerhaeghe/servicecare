package com.teo.servicecare.security;

import com.teo.servicecare.users.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("clientSecurity")
public class ClientSecurity {
  private final UserRepository users;

  public ClientSecurity(UserRepository users) {
    this.users = users;
  }

  public boolean isOwner(Long clientId, Authentication auth) {
    if (auth == null || auth.getName() == null) return false;
    return users.findByEmail(auth.getName())
        .map(u -> u.getClient() != null && u.getClient().getId().equals(clientId))
        .orElse(false);
  }
}
