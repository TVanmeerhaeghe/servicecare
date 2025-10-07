import api from "./http";
import type {
  Ticket,
  TicketPage,
  TicketPayload,
  TicketThreadEvent,
} from "@/types/tickets";

export const fetchTickets = (params: {
  page: number;
  size: number;
  search?: string;
  clientId?: number;
  siteId?: number;
  contractId?: number;
  assigneeUserId?: number;
  status?: string;
  priority?: string;
  slaBreached?: boolean;
}) =>
  api.get<TicketPage>("/tickets/search", {
    params: {
      page: params.page,
      size: params.size,
      q: params.search || undefined,
      clientId: params.clientId || undefined,
      siteId: params.siteId || undefined,
      contractId: params.contractId || undefined,
      assigneeUserId: params.assigneeUserId || undefined,
      status: params.status || undefined,
      priority: params.priority || undefined,
      slaBreached: params.slaBreached ?? undefined,
    },
  });

export const fetchTicketDetails = (id: number | string) =>
  api.get<Ticket>(`/tickets/${id}`);

export const createTicket = (payload: TicketPayload) =>
  api.post("/tickets", {
    clientId: payload.clientId,
    title: payload.title,
    description: payload.description,
    siteId: payload.siteId,
    contractId: payload.contractId,
    priority: payload.priority,
    assigneeUserId: payload.assigneeUserId,
  });

export const updateTicket = (
  id: number | string,
  payload: Partial<TicketPayload>
) => api.put(`/tickets/${id}`, payload);

export const deleteTicket = (id: number | string) =>
  api.delete(`/tickets/${id}`);

export const restoreTicket = (id: number | string) =>
  api.post(`/tickets/${id}/restore`);

export const transitionTicket = (id: number | string, action: string) =>
  api.post(`/tickets/${id}/transition`, null, { params: { action } });

export const fetchTicketThread = (ticketId: number | string) =>
  api.get<TicketThreadEvent[]>(`/tickets/${ticketId}/thread`);
