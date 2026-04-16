"use client";

import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import { apiFetch, refreshAccessToken, setAccessToken } from "./api";
import type { AuthResponse, UserResponse } from "./types";

interface AuthContextType {
  user: UserResponse | null;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, displayName: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const fetchUser = useCallback(async () => {
    try {
      const userData = await apiFetch<UserResponse>("/api/users/me");
      setUser(userData);
    } catch {
      setUser(null);
      setAccessToken(null);
    }
  }, []);

  useEffect(() => {
    async function init() {
      try {
        // Use the shared refresh helper so a concurrent apiFetch on page
        // mount dedupes onto the same in-flight promise (avoids double
        // refresh which would invalidate the rotated cookie).
        const token = await refreshAccessToken();
        if (token) {
          await fetchUser();
        }
      } finally {
        setIsLoading(false);
      }
    }
    init();
  }, [fetchUser]);

  const login = useCallback(
    async (email: string, password: string) => {
      const data = await apiFetch<AuthResponse>("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      });
      setAccessToken(data.accessToken);
      await fetchUser();
    },
    [fetchUser]
  );

  const register = useCallback(
    async (email: string, password: string, displayName: string) => {
      const data = await apiFetch<AuthResponse>("/api/auth/register", {
        method: "POST",
        body: JSON.stringify({ email, password, displayName }),
      });
      setAccessToken(data.accessToken);
      await fetchUser();
    },
    [fetchUser]
  );

  const logout = useCallback(async () => {
    try {
      await fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include",
      });
    } catch {
      // Best effort
    }
    setAccessToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within AuthProvider");
  return context;
}
