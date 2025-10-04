export type Role = 'ADMIN' | 'AGENT' | 'TECHNICIAN' | 'CLIENT'

export interface AuthUser {
  id: number
  email: string
  firstName?: string
  lastName?: string
  role: Role
}

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  user: AuthUser
}
