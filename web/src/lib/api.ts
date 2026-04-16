import type { AuthResponse, ApiError } from "./types";

let accessToken: string | null = null;
let refreshPromise: Promise<string | null> | null = null;

export function setAccessToken(token: string | null): void {
  accessToken = token;
}

export function getAccessToken(): string | null {
  return accessToken;
}

/**
 * Refresh the access token from the refresh cookie.
 * All concurrent callers share the same in-flight promise so we never fire
 * two refresh requests at once (which would cause the second to 403 because
 * the first has already rotated the refresh token).
 */
export function refreshAccessToken(): Promise<string | null> {
  if (refreshPromise) return refreshPromise;

  refreshPromise = (async () => {
    try {
      const res = await fetch("/api/auth/refresh", {
        method: "POST",
        credentials: "include",
      });
      if (!res.ok) {
        accessToken = null;
        return null;
      }
      const data: AuthResponse = await res.json();
      accessToken = data.accessToken;
      return accessToken;
    } catch {
      accessToken = null;
      return null;
    }
  })().finally(() => {
    refreshPromise = null;
  });

  return refreshPromise;
}

async function ensureToken(): Promise<string | null> {
  if (accessToken) return accessToken;
  return refreshAccessToken();
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const token = await ensureToken();

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  let res = await fetch(path, {
    ...options,
    headers,
    credentials: "include",
  });

  if (res.status === 401 && token) {
    const newToken = await refreshAccessToken();
    if (newToken) {
      headers["Authorization"] = `Bearer ${newToken}`;
      res = await fetch(path, {
        ...options,
        headers,
        credentials: "include",
      });
    }
  }

  if (!res.ok) {
    const error: ApiError = await res.json().catch(() => ({
      error: "Request failed",
      status: res.status,
    }));
    throw error;
  }

  // Handle empty bodies (204, 202, or any 2xx with no content)
  if (res.status === 204) return undefined as T;
  const text = await res.text();
  if (!text) return undefined as T;
  return JSON.parse(text) as T;
}
