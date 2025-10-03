package com.teo.servicecare.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.uploads")
public class UploadsProperties {
  private String dir = "uploads";
  private long maxSizeBytes = 10 * 1024 * 1024;
  private List<String> allowedContentTypes = List.of("image/png","image/jpeg","application/pdf");
  private List<String> blockedExtensions = List.of(".exe",".bat",".cmd",".sh",".js",".jar");

  public String getDir() { return dir; }
  public void setDir(String dir) { this.dir = dir; }

  public long getMaxSizeBytes() { return maxSizeBytes; }
  public void setMaxSizeBytes(long maxSizeBytes) { this.maxSizeBytes = maxSizeBytes; }

  public List<String> getAllowedContentTypes() { return allowedContentTypes; }
  public void setAllowedContentTypes(List<String> allowedContentTypes) { this.allowedContentTypes = allowedContentTypes; }

  public List<String> getBlockedExtensions() { return blockedExtensions; }
  public void setBlockedExtensions(List<String> blockedExtensions) { this.blockedExtensions = blockedExtensions; }
}
