export type ContractStatus = "ACTIVE" | "INACTIVE" | "EXPIRED";
export type SupportDays = "MON_FRI" | "SEVEN_DAYS";
export type MeasureWindow = "BUSINESS_HOURS" | "CALENDAR";

export interface Contract {
  id: number;
  clientId: number | null;
  clientName?: string | null;
  siteIds: number[];
  name: string;
  description?: string | null;
  startDate: string;
  endDate?: string | null;
  autoRenew: boolean;
  noticeDays: number;
  timezone: string;
  supportDays: SupportDays;
  supportHoursStart: string;
  supportHoursEnd: string;
  measureWindow: MeasureWindow;
  pauseOnWaiting: boolean;
  respCritHours: number;
  respHighHours: number;
  respMediumHours: number;
  respLowHours: number;
  resoCritHours: number;
  resoHighHours: number;
  resoMediumHours: number;
  resoLowHours: number;
  includedHoursMonth: number;
  maxTicketsMonth: number;
  overtimeRate?: string | null;
  emergencyRate?: string | null;
  status: ContractStatus;
  createdAt?: string;
  updatedAt?: string | null;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface ContractCreatePayload {
  name: string;
  description?: string | null;
  clientId: number;
  siteIds?: number[];
  startDate: string;
  endDate?: string | null;
  autoRenew?: boolean;
  noticeDays?: number;
  timezone?: string;
  supportDays?: SupportDays;
  supportHoursStart?: string;
  supportHoursEnd?: string;
  measureWindow?: MeasureWindow;
  pauseOnWaiting?: boolean;
  respCritHours?: number;
  respHighHours?: number;
  respMediumHours?: number;
  respLowHours?: number;
  resoCritHours?: number;
  resoHighHours?: number;
  resoMediumHours?: number;
  resoLowHours?: number;
  includedHoursMonth?: number;
  maxTicketsMonth?: number;
  overtimeRate?: string | null;
  emergencyRate?: string | null;
  status?: ContractStatus;
}

export type ContractUpdatePayload = Partial<
  Omit<ContractCreatePayload, "clientId">
> & {
  clientId?: number;
  siteIds?: number[];
};
