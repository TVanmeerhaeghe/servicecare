import api from "./http";
import type { Site, SitePage, SitePayload } from "@/types/sites";

export const fetchSites = (params: {
  page: number;
  size: number;
  search?: string;
  clientId?: number;
  environment?: string;
  type?: string;
  cms?: string;
  status?: string;
  createdFrom?: string;
  createdTo?: string;
}) =>
  api.get<SitePage>("/sites/search", {
    params: {
      page: params.page,
      size: params.size,
      q: params.search || undefined,
      clientId: params.clientId || undefined,
      environment: params.environment || undefined,
      type: params.type || undefined,
      cms: params.cms || undefined,
      status: params.status || undefined,
      createdFrom: params.createdFrom || undefined,
      createdTo: params.createdTo || undefined,
    },
  });

export const fetchSiteDetails = (id: string | number) =>
  api.get<Site>(`/sites/${id}`);

export const createSite = (payload: SitePayload) => api.post("/sites", payload);

export const updateSite = (id: string | number, payload: SitePayload) =>
  api.put(`/sites/${id}`, payload);

export const deleteSite = (id: string | number) => api.delete(`/sites/${id}`);
