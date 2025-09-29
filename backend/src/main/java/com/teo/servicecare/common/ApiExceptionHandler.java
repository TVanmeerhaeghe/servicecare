package com.teo.servicecare.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,String> badRequest(IllegalArgumentException ex) {
    return Map.of("error", ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String,String> validation(MethodArgumentNotValidException ex) {
    var f = ex.getBindingResult().getFieldErrors().stream().findFirst();
    return Map.of("error", f.map(e -> e.getField() + " " + e.getDefaultMessage()).orElse("validation_error"));
  }
}
