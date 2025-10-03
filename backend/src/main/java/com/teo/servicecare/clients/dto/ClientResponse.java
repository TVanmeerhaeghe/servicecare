package com.teo.servicecare.clients.dto;

import com.teo.servicecare.clients.Client;

public class ClientResponse {
  private Long id;
  private String name;
  private String legalName;
  private String siret;
  private String vatNumber;
  private String contactFirstName;
  private String contactLastName;
  private String contactEmail;
  private String contactPhone;
  private String billingEmail;
  private String technicalEmail;
  private String websiteUrl;
  private String addressLine1;
  private String postalCode;
  private String city;
  private String countryCode;
  private String currencyCode;
  private String status;

  public static ClientResponse from(Client c) {
    var r = new ClientResponse();
    r.id = c.getId();
    r.name = c.getName();
    r.legalName = c.getLegalName();
    r.siret = c.getSiret();
    r.vatNumber = c.getVatNumber();
    r.contactFirstName = c.getContactFirstName();
    r.contactLastName = c.getContactLastName();
    r.contactEmail = c.getContactEmail();
    r.contactPhone = c.getContactPhone();
    r.billingEmail = c.getBillingEmail();
    r.technicalEmail = c.getTechnicalEmail();
    r.websiteUrl = c.getWebsiteUrl();
    r.addressLine1 = c.getAddressLine1();
    r.postalCode = c.getPostalCode();
    r.city = c.getCity();
    r.countryCode = c.getCountryCode();
    r.currencyCode = c.getCurrencyCode();
    r.status = (c.getStatus() != null ? c.getStatus().name() : null);
    return r;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getLegalName() { return legalName; }
  public String getSiret() { return siret; }
  public String getVatNumber() { return vatNumber; }
  public String getContactFirstName() { return contactFirstName; }
  public String getContactLastName() { return contactLastName; }
  public String getContactEmail() { return contactEmail; }
  public String getContactPhone() { return contactPhone; }
  public String getBillingEmail() { return billingEmail; }
  public String getTechnicalEmail() { return technicalEmail; }
  public String getWebsiteUrl() { return websiteUrl; }
  public String getAddressLine1() { return addressLine1; }
  public String getPostalCode() { return postalCode; }
  public String getCity() { return city; }
  public String getCountryCode() { return countryCode; }
  public String getCurrencyCode() { return currencyCode; }
  public String getStatus() { return status; }
}
