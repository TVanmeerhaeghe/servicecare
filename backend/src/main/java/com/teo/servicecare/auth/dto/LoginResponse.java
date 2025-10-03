package com.teo.servicecare.auth.dto;

public class LoginResponse {
  private String token;

  public LoginResponse(String token) {
    this.token = token;
  }

  public String getToken() { return token; }
}
