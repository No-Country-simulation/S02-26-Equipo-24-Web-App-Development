"use server"

import { redirect } from "next/navigation"

export async function loginAction(formData: FormData) {
  const email = formData.get("email")?.toString()
  const password = formData.get("password")?.toString()

  if (!email || !password) {
    throw new Error("Missing credentials")
  }

  // 👉 aquí luego llamas al backend real
  console.log("EMAIL:", email)
  console.log("PASSWORD:", password)

  // ✅ si todo está bien
  redirect("/dashboard")
}