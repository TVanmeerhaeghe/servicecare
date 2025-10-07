import api from "./http";
import type { TicketCommentPage, TicketComment } from "@/types/ticketComments";

export function fetchTicketComments(
  ticketId: number | string,
  params?: { page?: number; size?: number }
) {
  return api.get<TicketCommentPage>("/ticket-comments", {
    params: {
      ticketId,
      page: params?.page ?? 0,
      size: params?.size ?? 50,
    },
  });
}

export function createTicketComment(payload: {
  ticketId: number | string;
  body: string;
  internalOnly?: boolean;
}) {
  return api.post<TicketComment>("/ticket-comments", payload);
}

export function deleteTicketComment(id: number | string) {
  return api.delete<void>(`/ticket-comments/${id}`);
}
