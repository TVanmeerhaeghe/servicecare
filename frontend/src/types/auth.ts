export type Role = "ADMIN" | "AGENT" | "TECHNICIAN" | "CLIENT";

export interface AuthUser {
  id: number;
  email: string;
  role: string;
  firstName?: string | null;
  lastName?: string | null;
  clientId?: number | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: AuthUser;
}
