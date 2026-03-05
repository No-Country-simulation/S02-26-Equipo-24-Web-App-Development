"use server";

import { cookies } from "next/headers";
import { get } from "@/app/lib/api";

export async function meAction() {
  const cookieStore = await cookies();
  const token = cookieStore.get("auth_token")?.value;

  if (!token) {

    return null;
  }

  const res = await get("/api/v1/auth/me", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res || res.error) {
    return null;
  }

  return res;
}