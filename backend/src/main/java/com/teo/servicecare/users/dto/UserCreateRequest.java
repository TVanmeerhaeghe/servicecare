package com.teo.servicecare.users.dto;

import jakarta.validation.constraints.*;

public class UserCreateRequest {
  @Email @NotBlank @Size(max=190)
  private String email;
  @NotBlank @Size(min=8, max=255)
  private String password;
  @NotBlank @Size(max=100)
  private String firstName;
  @NotBlank @Size(max=100)
  private String lastName;
  @Size(max=30)
  private String phone;
  private Long clientId;

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public Long getClientId() { return clientId; }
  public void setClientId(Long clientId) { this.clientId = clientId; }
}
