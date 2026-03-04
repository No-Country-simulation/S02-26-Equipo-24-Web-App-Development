"use server"

import { cookies } from "next/headers";
import { redirect } from "next/navigation";
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

  const responseData = res as any;
  if (responseData.token) {
    (await cookies()).set({
      name: "jwt-token",
      value: responseData.token,
      httpOnly: false,
      secure: process.env.NODE_ENV === "production",
      sameSite: "lax",
      path: "/",
      maxAge: 86400, // 24 hours
    });
  }

  redirect(`/dashboard?username=${username}`);
}