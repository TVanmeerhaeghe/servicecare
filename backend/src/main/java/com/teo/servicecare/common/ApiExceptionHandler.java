package com.teo.servicecare.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

  private ErrorResponse wrap(String code, String message, HttpServletRequest req, Map<String, Object> details) {
    return new ErrorResponse(new ErrorResponse.Body(
        code, message, details, req.getRequestURI(), req.getMethod()
    ));
  }

  // 400 - validations @Valid (body DTO)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(f -> f.getField(), f -> f.getDefaultMessage(), (a,b) -> a));
    return wrap("VALIDATION_ERROR", "Validation failed", req, Map.of("fields", fieldErrors));
  }

  // 400 - mauvais JSON, body illisible
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse badJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
    return wrap("BAD_REQUEST_BODY", "Malformed request body", req, Map.of("reason", ex.getMostSpecificCause().getMessage()));
  }

  // 400 - mauvais type de paramètre (ex: id=abc)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse typeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
    Class<?> required = ex.getRequiredType();
    String expected = (required != null) ? required.getSimpleName() : "unknown";

    Map<String, Object> details = new LinkedHashMap<>();
    details.put("param", ex.getName());
    details.put("expectedType", expected);
    Object value = ex.getValue();
    details.put("value", value != null ? value.toString() : "null");

    return wrap(
        "TYPE_MISMATCH",
        "Invalid parameter '" + ex.getName() + "'",
        req,
        details
    );
  }

  // 400 - erreurs métier qu’on jette nous-mêmes
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse badRequest(IllegalArgumentException ex, HttpServletRequest req) {
    return wrap("BAD_REQUEST", ex.getMessage(), req, Map.of());
  }

  // 405 - mauvaise méthode HTTP
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public ErrorResponse methodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
    return wrap("METHOD_NOT_ALLOWED", "HTTP method not allowed", req, Map.of("supported", ex.getSupportedHttpMethods()));
  }

  // 409 - contraintes DB (unique key, FK, etc.)
  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse conflict(DataIntegrityViolationException ex, HttpServletRequest req) {
    return wrap("CONFLICT", "Data integrity violation", req, Map.of("reason", ex.getMostSpecificCause().getMessage()));
  }

  // 500 - fallback
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse serverError(Exception ex, HttpServletRequest req) {
    return wrap("INTERNAL_ERROR", "Unexpected error", req, Map.of("reason", ex.getMessage()));
  }
}
