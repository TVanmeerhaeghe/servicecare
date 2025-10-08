export type TicketPriority = "CRITICAL" | "HIGH" | "MEDIUM" | "LOW";
export type TicketStatus =
  | "OPEN"
  | "ASSIGNED"
  | "IN_PROGRESS"
  | "WAITING"
  | "CLOSED"
  | "CANCELED";

export interface Ticket {
  id: number;
  clientId: number | null;
  siteId: number | null;
  contractId: number | null;
  title: string;
  description: string | null;
  priority: TicketPriority;
  status: TicketStatus;
  waitingReason: string | null;
  assigneeUserId: number | null;
  respondBy: string;
  resolveBy: string;
  respondedAt: string | null;
  resolvedAt: string | null;
  slaBreached: boolean;
  pausedSeconds: number;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export interface TicketPage {
  content: Ticket[];
  page: number;
  size: number;
  totalElements: number;
}

export interface TicketPayload {
  clientId: number | null;
  title: string;
  description: string | null;
  siteId: number | null;
  contractId: number | null;
  priority: TicketPriority;
  assigneeUserId: number | null;
  status?: TicketStatus | null;
  waitingReason?: string | null;
}

export type TicketThreadKind = "COMMENT" | "INTERVENTION" | "ATTACHMENT";

export interface CommentEvent {
  kind: "COMMENT";
  id: number;
  at: string | null;
  authorName?: string | null;
  authorUserId?: number | null;
  body: string;
  internalOnly?: boolean;
  authorIsClient?: boolean;
}

export interface AttachmentEvent {
  kind: "ATTACHMENT";
  id: number;
  at: string | null;
  authorName?: string | null;
  authorUserId?: number | null;
  originalName: string;
  size?: number | null;
  downloadUrl?: string | null;
  contentType?: string | null;
}

export interface InterventionEvent {
  kind: "INTERVENTION";
  id: number;
  at: string | null;
  authorName?: string | null;
  authorUserId?: number | null;
  title?: string | null;
  interventionType?: string | null;
  interventionStatus?: string | null;
  technicianUserId?: number | null;
  scheduledStart?: string | null;
  scheduledEnd?: string | null;
  actualStart?: string | null;
  actualEnd?: string | null;
}

export type TicketThreadEvent =
  | CommentEvent
  | AttachmentEvent
  | InterventionEvent;
