"use server"

import { redirect } from "next/navigation"
import { post } from "@/app/lib/api";

export async function registerAction(formData: FormData) {
  const username = formData.get("name")?.toString()
  const email = formData.get("email")?.toString()
  const institution = formData.get("institution")?.toString()
  const password = formData.get("password")?.toString()

  if (!username || !email || !institution || !password) {
    throw new Error("Missing credentials")
  }

   const res = await post("/register", {
    method: "POST",
    body: JSON.stringify({ username, email, institution, password }),
  });

  if (!res.ok) {
    redirect("/register?error=invalid_credentials");
  }

  // ✅ si todo está bien
  redirect("/login")
}