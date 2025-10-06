package com.teo.servicecare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.teo.servicecare.config.AppProperties;
import com.teo.servicecare.config.JwtProperties;
import com.teo.servicecare.config.UploadsProperties;

@EnableConfigurationProperties({ JwtProperties.class, AppProperties.class, UploadsProperties.class })
@SpringBootApplication
@EnableScheduling
public class ServiceCareApplication {
  public static void main(String[] args) {
    SpringApplication.run(ServiceCareApplication.class, args);
  }
}
