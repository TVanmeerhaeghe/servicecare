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

export const transitionTicket = (
  id: number | string,
  action: string,
  extra?: Record<string, any>
) =>
  api.post(`/tickets/${id}/transition`, null, {
    params: { action, ...(extra || {}) },
  });

export const fetchTicketThread = (ticketId: number | string) =>
  api.get<TicketThreadEvent[]>(`/tickets/${ticketId}/thread`);

export const assignTicket = (
  id: number | string,
  assigneeUserId: number | string
) => api.post(`/tickets/${id}/assign`, null, { params: { assigneeUserId } });

export const uploadTicketAttachment = (
  ticketId: number | string,
  file: File
) => {
  const fd = new FormData();
  fd.append("file", file);
  return api.post(`/tickets/${ticketId}/attachments`, fd, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

export const fetchAttachmentBlob = (attachmentId: number | string) =>
  api.get(`/tickets/attachments/${attachmentId}/download`, {
    responseType: "blob",
  });

export const attachmentDownloadUrl = (attachmentId: number | string) =>
  `/api/tickets/attachments/${attachmentId}/download`;
