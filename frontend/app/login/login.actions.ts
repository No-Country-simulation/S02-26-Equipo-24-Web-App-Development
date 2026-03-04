"use server"

import { redirect } from "next/navigation"
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

  console.log("Login response:", res);

  if (res.error) {
    redirect("/login?error=invalid_credentials");
  }

  redirect(`/dashboard?username=${username}`);
}