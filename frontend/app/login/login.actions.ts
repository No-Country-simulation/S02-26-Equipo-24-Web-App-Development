"use server"

import { redirect } from "next/navigation"
import { cookies } from "next/headers"
import { post } from "@/app/lib/api";

export async function loginAction(formData: FormData) {
  const username = formData.get("username")?.toString();
  const password = formData.get("password")?.toString();

  if (!username || !password) {
    redirect("/login?error=missing_credentials");
  }

  const res = await post("/api/v1/auth/login", {
    username,
    password,
  });

  const token = res?.token;

  if (!token) {
    redirect("/login?error=invalid_credentials");
  }

  const cookieStore = await cookies();

  cookieStore.set("auth_token", token, {
    httpOnly: true,
    secure: true,
    sameSite: "lax",
    path: "/"
  });

  redirect("/dashboard");
}