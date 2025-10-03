package com.teo.servicecare.users.dto;

import com.teo.servicecare.users.User;

public class UserUpdateRequest {
  private String email;
  private String firstName;
  private String lastName;
  private String phone;
  private User.Role role;
  private User.Status status;
  private Long clientId;

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public User.Role getRole() { return role; }
  public void setRole(User.Role role) { this.role = role; }
  public User.Status getStatus() { return status; }
  public void setStatus(User.Status status) { this.status = status; }
  public Long getClientId() { return clientId; }
  public void setClientId(Long clientId) { this.clientId = clientId; }
}
