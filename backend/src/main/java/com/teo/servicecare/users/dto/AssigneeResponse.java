package com.teo.servicecare.users.dto;

import com.teo.servicecare.users.User;

public class AssigneeResponse {
  private Long id;
  private String email;
  private String firstName;
  private String lastName;
  private String phone;

  public static AssigneeResponse from(User u) {
    var r = new AssigneeResponse();
    r.id = u.getId();
    r.email = u.getEmail();
    r.firstName = u.getFirstName();
    r.lastName = u.getLastName();
    r.phone = u.getPhone();
    return r;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getPhone() {
    return phone;
  }
}
