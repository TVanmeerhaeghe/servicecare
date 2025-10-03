package com.teo.servicecare.common.dto;

import java.time.Instant;
import java.util.Map;

public class ErrorResponse {
  public static class Body {
    private String code;
    private String message;
    private Map<String, Object> details;
    private String path;
    private String method;
    private Instant timestamp;

    public Body(String code, String message, Map<String, Object> details, String path, String method) {
      this.code = code;
      this.message = message;
      this.details = details;
      this.path = path;
      this.method = method;
      this.timestamp = Instant.now();
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Map<String, Object> getDetails() { return details; }
    public String getPath() { return path; }
    public String getMethod() { return method; }
    public Instant getTimestamp() { return timestamp; }
  }

  private Body error;
  public ErrorResponse(Body error) { this.error = error; }
  public Body getError() { return error; }
}
