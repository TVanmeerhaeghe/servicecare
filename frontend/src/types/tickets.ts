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

export interface TicketThreadEvent {
  id: number;
  kind: TicketThreadKind;
  at: string | null;
  authorName?: string | null;
  body?: string | null;
  interventionType?: string | null;
  interventionStatus?: string | null;
  title?: string | null;
  technicianUserId?: number | null;
  scheduledStart?: string | null;
  scheduledEnd?: string | null;
  actualStart?: string | null;
  actualEnd?: string | null;
  originalName?: string | null;
  contentType?: string | null;
  size?: number | null;
  downloadUrl?: string | null;
}
