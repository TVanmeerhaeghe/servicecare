package com.teo.servicecare.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;

import com.teo.servicecare.clients.Client;

@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Email @NotBlank @Size(max=190)
  private String email;

  @JsonIgnore
  @NotBlank @Size(min=8, max=255)
  private String password;

  @Column(name = "first_name") @NotBlank @Size(max=100)
  private String firstName;

  @Column(name = "last_name") @NotBlank @Size(max=100)
  private String lastName;

  @Size(max=30)
  private String phone;

  @Enumerated(EnumType.STRING) @Column(nullable=false)
  private Role role = Role.CLIENT;

  @Enumerated(EnumType.STRING) @Column(nullable=false)
  private Status status = Status.ACTIVE;

  private Instant lastLoginAt;
  private String timezone;
  private String avatarUrl;

  @Column(name="created_at", updatable=false, insertable=false)
  private Instant createdAt;

  @Column(name="updated_at", insertable=false)
  private Instant updatedAt;

  @Column(name="created_by")
  private Long createdBy;

  @Column(name="updated_by")
  private Long updatedBy;

  public enum Role { ADMIN, AGENT, TECHNICIAN ,CLIENT }
  public enum Status { ACTIVE, DISABLED, INVITED }

  @ManyToOne
  @JoinColumn(name = "client_id")
  @JsonIgnore
  private Client client;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
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
  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }
  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }
  public Instant getLastLoginAt() { return lastLoginAt; }
  public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
  public String getTimezone() { return timezone; }
  public void setTimezone(String timezone) { this.timezone = timezone; }
  public String getAvatarUrl() { return avatarUrl; }
  public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
  public Long getCreatedBy() { return createdBy; }
  public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
  public Long getUpdatedBy() { return updatedBy; }
  public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }
  public Client getClient() { return client; }
  public void setClient(Client client) { this.client = client; }
}
