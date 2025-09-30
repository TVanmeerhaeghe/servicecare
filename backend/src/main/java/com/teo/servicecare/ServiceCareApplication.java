package com.teo.servicecare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.teo.servicecare.config.JwtProperties;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class ServiceCareApplication {
  public static void main(String[] args) { 
    SpringApplication.run(ServiceCareApplication.class, args);
  }
}
