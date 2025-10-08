import api from "./http";
import type { Client, ClientPage, ClientPayload } from "@/types/clients";

export const fetchClients = (params: {
  page: number;
  size: number;
  search?: string;
  status?: string;
}) =>
  api.get("/clients/search", {
    params: {
      page: params.page,
      size: params.size,
      q: params.search || undefined,
      status: params.status || undefined,
    },
  });

export const fetchClientDetails = (id: string | number) =>
  api.get<Client>(`/clients/${id}`);

export const createClient = (payload: ClientPayload) =>
  api.post("/clients", payload);

export const updateClient = (id: string, payload: ClientPayload) =>
  api.put(`/clients/${id}`, payload);

export const deleteClient = (id: string | number) =>
  api.delete(`/clients/${id}`);
