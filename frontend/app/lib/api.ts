import { UUID } from "crypto";
import { API_URL } from "./config";
import { cookies } from "next/headers";

export type ApiResponse<T = unknown> = {
  message?: string;
  data?: T;
  error?: string;
  status?: number;
  headers?: Headers;
  token?: string;
};
export type ApigetResponse<T = unknown> = {
 id : UUID;
 username : string;
 role : string;
};

export type ApiError = {
  error: string;
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

  let data: any;
  try {
    data = await res.json();
  } catch (err) {
    return { error: "Invalid JSON response" };
  }

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