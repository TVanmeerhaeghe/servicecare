export type UserRole = "ADMIN" | "AGENT" | "TECHNICIAN" | "CLIENT";
export type UserStatus = "ACTIVE" | "DISABLED" | "INVITED";

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string | null;
  role: UserRole;
  status: UserStatus;
  timezone?: string | null;
  avatarUrl?: string | null;
  clientId?: number | null;
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

export interface UserCreatePayload {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string | null;
  clientId?: number | null;
}

export interface UserUpdatePayload {
  email?: string;
  firstName?: string;
  lastName?: string;
  phone?: string | null;
  role?: UserRole;
  status?: UserStatus;
  clientId?: number | null;
}

export interface ResetPasswordPayload {
  newPassword: string;
}
