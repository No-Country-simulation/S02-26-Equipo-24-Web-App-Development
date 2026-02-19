"use server"

export async function loginAction(formData: FormData) {
  const email = formData.get("email")
  const password = formData.get("password")

  console.log("EMAIL:", email)
  console.log("PASSWORD:", password)


}