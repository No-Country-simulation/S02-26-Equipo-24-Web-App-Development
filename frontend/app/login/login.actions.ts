"use server"

import { redirect } from "next/navigation"
import { post } from "@/app/lib/api";


export async function loginAction(formData: FormData) {
  const email = formData.get("email")?.toString()
  const password = formData.get("password")?.toString()
  const username = formData.get("email")?.toString()
  if (!email || !password) {
    throw new Error("Missing credentials")
  }

  const res = await post("/api/v1/auth/login", {
  username,
  password,
});

  console.log("Login request:", { password ,username });
  console.log("Login response:", res);
  if (res.message !== "Login exitoso") {
    redirect("/login?error=invalid_credentials");
  }

  // ✅ si todo está bien
  redirect(`/dashboard?username=${username}`)
}