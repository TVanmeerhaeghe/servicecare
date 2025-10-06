package com.teo.servicecare.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private boolean success;
  private T data;
  private ApiError error;

  public ApiResponse() {
  }

  private ApiResponse(boolean success, T data, ApiError error) {
    this.success = success;
    this.data = data;
    this.error = error;
  }

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, data, null);
  }

  public static <T> ApiResponse<T> fail(String message, String code, Map<String, Object> details) {
    return new ApiResponse<>(false, null, new ApiError(message, code, details));
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public ApiError getError() {
    return error;
  }

  public void setError(ApiError error) {
    this.error = error;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ApiError {
    private String message;
    private String code;
    private Map<String, Object> details;

    public ApiError() {
    }

    public ApiError(String message, String code, Map<String, Object> details) {
      this.message = message;
      this.code = code;
      this.details = details;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public Map<String, Object> getDetails() {
      return details;
    }

    public void setDetails(Map<String, Object> details) {
      this.details = details;
    }
  }
}
