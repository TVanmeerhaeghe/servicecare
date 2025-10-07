export interface TicketComment {
  id: number;
  ticketId: number;
  authorId: number | null;
  authorName?: string | null;
  body: string;
  internalOnly?: boolean;
  createdAt: string;
  updatedAt?: string | null;
}

export interface TicketCommentPage {
  content: TicketComment[];
  page: number;
  size: number;
  totalElements: number;
}

export interface TicketCommentCreatePayload {
  body: string;
  internalOnly?: boolean;
}
