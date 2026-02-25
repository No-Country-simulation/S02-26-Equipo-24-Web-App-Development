import { API_URL } from "@/app/lib/config";

export async function get<T>(url: string, options?: RequestInit) {
  const res = await fetch(API_URL + url, options);
  return res.json();
}

export async function post<T>(url: string, body: unknown, options?: RequestInit) {
  const res = await fetch(API_URL + url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
    ...options,
  });
  return res.json();
}