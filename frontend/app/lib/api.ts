import { API_URL } from "./config";

export type ApiResponse<T = unknown> = {
  message?: string;
  data?: T;
  error?: string;
};

export async function get<T = unknown>(
  url: string,
  options: RequestInit = {}
): Promise<ApiResponse<T>> {
  const res = await fetch(`${API_URL}${url}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
    ...options,
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data?.message || "API GET error");
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
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
    ...options,
  });

  const data = await res.json();

  if (!res.ok) {
    throw new Error(data?.message || "API POST error");
  }

  return data;
}