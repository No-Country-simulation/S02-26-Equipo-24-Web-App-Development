"use server"

import { redirect } from "next/navigation"
import { post } from "@/app/lib/api";

export async function loginAction(formData: FormData) {
  const email = formData.get("email")?.toString();
  const password = formData.get("password")?.toString();

  if (!email || !password) {
    redirect("/login?error=missing_credentials");
  }

  const res = await post("/api/v1/auth/login", {
    username: email,
    password,
  });

  if (res.error) {
    redirect("/login?error=invalid_credentials");
  }

  redirect(`/dashboard?username=${email}`);
}