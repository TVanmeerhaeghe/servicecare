package com.teo.servicecare.sites;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.teo.servicecare.clients.Client;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sites")
public class Site {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String url;

  public enum SiteEnvironment { PROD, STAGING, DEV }
  @Enumerated(EnumType.STRING)
  @Column(
    name = "environment",
    columnDefinition = "enum('PROD','STAGING','DEV')"
  )
  private SiteEnvironment environment;

  public enum SiteType { WEBSITE, SHOP, API, APP }
  @Enumerated(EnumType.STRING)
  @Column(
    name = "type",
    columnDefinition = "enum('WEBSITE','SHOP','API','APP')"
  )
  private SiteType type;

  public enum SiteCms {
    WORDPRESS,
    SHOPIFY,
    PRESTASHOP,
    DRUPAL,
    JOOMLA,
    MAGENTO,
    WIX,
    CUSTOM,
    OTHER
  }
  @Enumerated(EnumType.STRING)
  @Column(
    name = "cms",
    columnDefinition = "enum('WORDPRESS','SHOPIFY','PRESTASHOP','DRUPAL','JOOMLA','MAGENTO','WIX','CUSTOM','OTHER')"
  )
  private SiteCms cms;

  public enum SiteStatus { ACTIVE, INACTIVE }
  @Enumerated(EnumType.STRING)
  @Column(
    name = "status",
    columnDefinition = "enum('ACTIVE','INACTIVE')"
  )
  private SiteStatus status;

  private String hostingProvider;

  @Column(name="created_at", updatable=false, insertable=false)
  private Instant createdAt;

  @Column(name="updated_at", insertable=false)
  private Instant updatedAt;

  @ManyToOne
  @JoinColumn(name="client_id")
  @JsonBackReference
  @JsonIgnore
  private Client client;

  // --- Getters & Setters ---

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }

  public SiteEnvironment getEnvironment() { return environment; }
  public void setEnvironment(SiteEnvironment environment) { this.environment = environment; }

  public SiteType getType() { return type; }
  public void setType(SiteType type) { this.type = type; }

  public SiteCms getCms() { return cms; }
  public void setCms(SiteCms cms) { this.cms = cms; }

  public SiteStatus getStatus() { return status; }
  public void setStatus(SiteStatus status) { this.status = status; }

  public String getHostingProvider() { return hostingProvider; }
  public void setHostingProvider(String hostingProvider) { this.hostingProvider = hostingProvider; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

  public Client getClient() { return client; }
  public void setClient(Client client) { this.client = client; }

  @JsonProperty("clientId")
  public Long getClientId() {
    return client != null ? client.getId() : null;
  }
}
