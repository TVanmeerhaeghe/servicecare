package com.teo.servicecare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class WebMvcPagingConfig {
  @Bean
  public PageableHandlerMethodArgumentResolverCustomizer pageSizeCustomizer() {
    return p -> {
      p.setMaxPageSize(100);
    };
  }
}
