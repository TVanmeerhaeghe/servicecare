import http from "./http";
import type {
  Contract,
  PageResponse,
  ContractCreatePayload,
  ContractUpdatePayload,
  ContractStatus,
  SupportDays,
  MeasureWindow,
} from "@/types/contracts";

export function fetchContracts(params?: {
  page?: number;
  size?: number;
  sort?: string;
}) {
  return http.get<PageResponse<Contract>>("/contracts", { params });
}

export function searchContracts(params?: {
  clientId?: number;
  status?: ContractStatus;
  measureWindow?: MeasureWindow;
  supportDays?: SupportDays;
  q?: string;
  startFrom?: string;
  startTo?: string;
  endFrom?: string;
  endTo?: string;
  page?: number;
  size?: number;
  sort?: string;
}) {
  return http.get<PageResponse<Contract>>("/contracts/search", { params });
}

export function fetchContractDetails(id: number | string) {
  return http.get<Contract>(`/contracts/${id}`);
}

export function createContract(payload: ContractCreatePayload) {
  return http.post<Contract>("/contracts", payload);
}

export function updateContract(
  id: number | string,
  payload: ContractUpdatePayload
) {
  return http.put<Contract>(`/contracts/${id}`, payload);
}

export function deleteContract(id: number | string) {
  return http.delete<void>(`/contracts/${id}`);
}
