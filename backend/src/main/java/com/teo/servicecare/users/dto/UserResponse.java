package com.teo.servicecare.users.dto;

import com.teo.servicecare.users.User;

public class UserResponse {
  public Long id;
  public String email;
  public String firstName;
  public String lastName;
  public String phone;
  public User.Role role;
  public User.Status status;
  public String timezone;
  public String avatarUrl;
  public Long clientId;

  public static UserResponse from(User u) {
    var r = new UserResponse();
    r.id = u.getId();
    r.email = u.getEmail();
    r.firstName = u.getFirstName();
    r.lastName = u.getLastName();
    r.phone = u.getPhone();
    r.role = u.getRole();
    r.status = u.getStatus();
    r.timezone = u.getTimezone();
    r.avatarUrl = u.getAvatarUrl();
    r.clientId = (u.getClient() != null ? u.getClient().getId() : null);
    return r;
  }
}
