"use server";
import { get } from "@/app/lib/api";

export async function meAction() {
  const res = await get("/api/v1/auth/me");

  if (!res || res.error) {
    return null;
  }

  return res;
}