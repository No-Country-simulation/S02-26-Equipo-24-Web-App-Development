"use server"

import { cookies } from "next/headers"
import { redirect } from "next/navigation"

export async function registerAction(formData: FormData) {
  const name = formData.get("name")?.toString()
  const email = formData.get("email")?.toString()
  const institution = formData.get("institution")?.toString()
  const password = formData.get("password")?.toString()

  if (!name || !email || !institution || !password) {
    throw new Error("Missing credentials")
  }

  const res = await fetch("https://TU_BACKEND/api/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, institution, password }),
  })

  if (!res.ok) {
    throw new Error("Invalid credentials")
  }

  const { token } = await res.json()

  const cookieStore = await cookies() 
  cookieStore.set("token", token, {
    httpOnly: true,
    secure: true,
    sameSite: "strict",
    path: "/",
  })

  // ✅ si todo está bien
  redirect("/login")
}