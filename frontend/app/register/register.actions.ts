"use server"

import { redirect } from "next/navigation"
import { post } from "@/app/lib/api";

export async function registerAction(formData: FormData) {
  const username = formData.get("name")?.toString();
  const email = formData.get("email")?.toString();
  const institution = formData.get("institution")?.toString();
  const password = formData.get("password")?.toString();

  if (!username || !email || !institution || !password) {
    redirect("/register?error=missing_credentials");
  }

  const res = await post("/api/v1/auth/register", {
    username,
    email,
    institution,
    password,
  });

  if (res.error || (res.statusCode && res.statusCode !== 200)) {
    redirect("/register?error=invalid_credentials");
  }

  redirect("/login");
}