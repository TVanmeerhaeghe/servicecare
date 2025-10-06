export type ClientStatus = "ACTIF" | "INACTIF" | "LEAD" | string;

export interface Client {
  id: number;
  name: string | null;
  legalName: string | null;
  siret: string | null;
  vatNumber: string | null;
  contactFirstName: string | null;
  contactLastName: string | null;
  contactEmail: string | null;
  contactPhone: string | null;
  billingEmail: string | null;
  technicalEmail: string | null;
  websiteUrl: string | null;
  addressLine1: string | null;
  postalCode: string | null;
  city: string | null;
  countryCode: string | null;
  currencyCode: string | null;
  status: ClientStatus;
}

export interface ClientPage {
  content: Client[];
  page: number;
  size: number;
  totalElements: number;
}

export interface ClientPayload {
  name: string | null;
  legalName: string | null;
  siret: string | null;
  vatNumber: string | null;
  contactFirstName: string | null;
  contactLastName: string | null;
  contactEmail: string | null;
  contactPhone: string | null;
  billingEmail: string | null;
  technicalEmail: string | null;
  websiteUrl: string | null;
  addressLine1: string | null;
  postalCode: string | null;
  city: string | null;
  countryCode: string | null;
  currencyCode: string | null;
  status: ClientStatus;
}
