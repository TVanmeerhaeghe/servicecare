import { defineStore } from "pinia";
import { api, setAuthToken } from "@/api/http";
import type { AuthUser, LoginRequest } from "@/types/auth";

const STORAGE_KEY = "sc_auth_v1";

interface AuthState {
  token: string | null;
  user: AuthUser | null;
  hydrated: boolean;
}

export const useAuthStore = defineStore("auth", {
  state: (): AuthState => ({
    token: null,
    user: null,
    hydrated: false,
  }),

  getters: {
    isAuthenticated: (s) => !!s.token,
    userRole: (s) => s.user?.role ?? null,
    isClientRole(): boolean {
      return this.userRole === "CLIENT";
    },
    clientId: (s) => s.user?.clientId ?? null,
    displayName: (s) =>
      s.user
        ? `${s.user.firstName ?? ""} ${s.user.lastName ?? ""}`.trim() ||
          s.user.email
        : "",
    hasRole: (s) => {
      return (role: string) => (s.user?.role ? s.user.role === role : false);
    },
    canSeeAdminSections(): boolean {
      return !this.isClientRole;
    },
  },

  actions: {
    hydrateFromStorage() {
      try {
        const raw = localStorage.getItem(STORAGE_KEY);
        if (raw) {
          const parsed = JSON.parse(raw) as { token: string; user: AuthUser };
          this.token = parsed.token;
          this.user = parsed.user;
          setAuthToken(this.token);
        }
      } catch {
        this.token = null;
        this.user = null;
      }
      this.hydrated = true;
    },

    persist() {
      if (this.token && this.user) {
        localStorage.setItem(
          STORAGE_KEY,
          JSON.stringify({ token: this.token, user: this.user })
        );
      } else {
        localStorage.removeItem(STORAGE_KEY);
      }
    },

    async login(payload: LoginRequest) {
      const { data } = await api.post<{ token: string }>(
        "/auth/login",
        payload
      );
      this.token = data.token;
      setAuthToken(this.token);
      const userData = await api.get<AuthUser>("/users/self");
      this.user = userData.data;
      this.persist();
    },

    logout() {
      this.token = null;
      this.user = null;
      setAuthToken(null);
      this.persist();
    },

    async fetchMe() {
      const { data } = await api.get<AuthUser>("/users/self");
      this.user = data;
      this.persist();
    },
  },
});
