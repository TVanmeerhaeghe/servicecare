export type SiteEnvironment = "PROD" | "STAGING" | "DEV";
export type SiteType = "WEBSITE" | "SHOP" | "API" | "APP";
export type SiteCms =
  | "WORDPRESS"
  | "SHOPIFY"
  | "DRUPAL"
  | "PRESTASHOP"
  | "CUSTOM"
  | "OTHER";
export type SiteStatus = "ACTIVE" | "INACTIVE";
export type SiteSslStatus = "UNKNOWN" | "VALID" | "EXPIRED" | "NOT_INSTALLED";

export interface Site {
  id: number;
  clientId: number | null;
  name: string | null;
  url: string | null;
  environment: SiteEnvironment | null;
  type: SiteType | null;
  cms: SiteCms | null;
  status: SiteStatus | null;
  repoUrl: string | null;
  prodUrl: string | null;
  stagingUrl: string | null;
  hostingProvider: string | null;
  hostingPlan: string | null;
  serverIp: string | null;
  phpVersion: string | null;
  nodeVersion: string | null;
  mysqlVersion: string | null;
  sslStatus: SiteSslStatus | null;
  analyticsId: string | null;
  gtId: string | null;
  sentryDsn: string | null;
  maintenanceEnabled: boolean | null;
  maintenanceEmail: string | null;
  lastMaintenanceAt: string | null;
  nextMaintenanceAt: string | null;
  lastBackupAt: string | null;
  notes: string | null;
  createdAt: string | null;
  updatedAt: string | null;
  createdBy: number | null;
  updatedBy: number | null;
}

export interface SitePage {
  content: Site[];
  page: number;
  size: number;
  totalElements: number;
}

export interface SitePayload {
  clientId: number | null;
  name: string | null;
  url: string | null;
  environment: SiteEnvironment | null;
  type: SiteType | null;
  cms: SiteCms | null;
  status: SiteStatus | null;
  repoUrl: string | null;
  prodUrl: string | null;
  stagingUrl: string | null;
  hostingProvider: string | null;
  hostingPlan: string | null;
  serverIp: string | null;
  phpVersion: string | null;
  nodeVersion: string | null;
  mysqlVersion: string | null;
  sslStatus: SiteSslStatus | null;
  analyticsId: string | null;
  gtId: string | null;
  sentryDsn: string | null;
  maintenanceEnabled: boolean | null;
  maintenanceEmail: string | null;
  lastMaintenanceAt: string | null;
  nextMaintenanceAt: string | null;
  lastBackupAt: string | null;
  notes: string | null;
}
