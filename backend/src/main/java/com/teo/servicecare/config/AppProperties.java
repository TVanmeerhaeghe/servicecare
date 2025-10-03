package com.teo.servicecare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
  private final Uploads uploads = new Uploads();
  public Uploads getUploads() { return uploads; }

  public static class Uploads {
    private String dir = "uploads";
    public String getDir() { return dir; }
    public void setDir(String dir) { this.dir = dir; }
  }
}