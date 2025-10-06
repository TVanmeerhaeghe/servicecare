package com.teo.servicecare.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.teo.servicecare.common.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import java.io.IOException;
import java.util.Map;

public class JsonAccessDeniedHandler implements AccessDeniedHandler {
  private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)
      throws IOException {
    var body = new ErrorResponse.Body(
        "FORBIDDEN",
        "You do not have permission to access this resource",
        Map.of("reason", ex.getMessage()),
        request.getRequestURI(),
        request.getMethod());
    var payload = new ErrorResponse(body);

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    mapper.writeValue(response.getOutputStream(), payload);
  }
}
