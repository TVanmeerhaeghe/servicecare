import http from "./http";
import api from "./http";
import type { PageResponse } from "@/types/users";
import type {
  User,
  UserCreatePayload,
  UserUpdatePayload,
  UserRole,
  UserStatus,
  ResetPasswordPayload,
} from "@/types/users";
import type { Assignee } from "@/types/users";

export function fetchUsers(params?: {
  page?: number;
  size?: number;
  sort?: string;
}) {
  return http.get<PageResponse<User>>("/users", { params });
}

export function searchUsers(params?: {
  q?: string;
  role?: UserRole;
  status?: UserStatus;
  clientId?: number;
  page?: number;
  size?: number;
  sort?: string;
}) {
  return http.get<PageResponse<User>>("/users/search", { params });
}

export function fetchUserDetails(id: number | string) {
  return http.get<User>(`/users/${id}`);
}

export function createUser(payload: UserCreatePayload) {
  return http.post<User>("/users", payload);
}

export function updateUser(id: number | string, payload: UserUpdatePayload) {
  return http.put<User>(`/users/${id}`, payload);
}

export function deleteUser(id: number | string) {
  return http.delete<void>(`/users/${id}`);
}

export function resetUserPassword(
  id: number | string,
  payload: ResetPasswordPayload
) {
  return http.post<void>(`/users/${id}/reset-password`, payload);
}

export const fetchAssignees = (params?: {
  q?: string;
  page?: number;
  size?: number;
}) =>
  api.get<{
    content: Assignee[];
    totalElements: number;
    totalPages: number;
    number: number;
  }>("/users/assignees", {
    params: {
      q: params?.q || undefined,
      page: params?.page ?? 0,
      size: params?.size ?? 20,
    },
  });
