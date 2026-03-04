import { API_URL } from "./config";

export type ApiResponse<T = unknown> = {
  message?: string;
  data?: T;
  error?: string;
};

const defaultOptions: RequestInit = {
  credentials: "include",
  headers: {
    "Content-Type": "application/json",
  },
};

export async function get<T = unknown>(
  url: string,
  options: RequestInit = {}
): Promise<ApiResponse<T>> {
  const res = await fetch(`${API_URL}${url}`, {
    method: "GET",
    ...defaultOptions,
    ...options,
  });

  const data = await res.json();

  if (!res.ok) {
    return {
      error: data?.message || "API GET error",
    };
  }

  return data;
}

export async function post<T = unknown>(
  url: string,
  body: unknown,
  options: RequestInit = {}
): Promise<ApiResponse<T>> {
  const res = await fetch(`${API_URL}${url}`, {
    method: "POST",
    body: JSON.stringify(body),
    ...defaultOptions,
    ...options,
  });

  const data = await res.json();

  if (!res.ok) {
    return {
      error: data?.message || "API POST error",
    };
  }

  return data;
}