export interface TicketComment {
  id: number;
  ticketId: number;
  authorUserId: number;
  authorName: string;
  body: string;
  internalOnly: boolean;
  authorIsClient?: boolean;
  createdAt: string;
  updatedAt: string | null;
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
