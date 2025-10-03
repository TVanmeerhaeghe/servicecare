package com.teo.servicecare.common;

import org.springframework.data.domain.Page;

import com.teo.servicecare.common.dto.PageResponse;

import java.util.function.Function;

public final class Pages {
  private Pages() {}

  public static <S, T> PageResponse<T> map(Page<S> page, Function<S, T> mapper) {
    return PageResponse.from(page.map(mapper));
  }
}
