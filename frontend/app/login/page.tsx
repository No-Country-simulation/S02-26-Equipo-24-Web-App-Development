"use client";

import { Activity, ArrowLeft } from "lucide-react";
import { Button } from "@/app/components/ui/button";
import { Card } from "@/app/components/ui/card";
import { Input } from "@/app/components/ui/input";
import { Label } from "@/app/components/ui/label";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function Login() {
  const router = useRouter();
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);
    setLoading(true);

    const formData = new FormData(e.currentTarget);
    const username = formData.get("username");
    const password = formData.get("password");

    const res = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/v1/auth/login/`,
      {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username, password }),
      }
    );

    if (!res.ok) {
      setError("Usuario o contraseña incorrectos");
      setLoading(false);
      return;
    }

    router.push("/dashboard");
  }

  return (
    <div className="flex justify-center items-center bg-linear-to-br from-slate-50 via-blue-50 to-slate-100 p-6 min-h-screen">
      <div className="w-full max-w-md">
        <div className="mb-8 text-center">
          <div className="flex justify-center items-center gap-2 mb-4">
            <Activity className="size-10 text-blue-600" />
            <span className="font-bold text-slate-900 text-2xl">RoboSim</span>
          </div>
          <h1 className="mb-2 font-semibold text-slate-900 text-2xl">
            Acceso a la Plataforma
          </h1>
          <p className="text-slate-600">
            Ingresa tus credenciales para acceder al sistema de simulación
          </p>
        </div>

        <Card className="shadow-xl p-8 border-slate-200">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="username">Nombre de usuario</Label>
              <Input
                id="username"
                name="username"
                required
                className="border-slate-300"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="password">Contraseña</Label>
              <Input
                id="password"
                name="password"
                type="password"
                required
                className="border-slate-300"
              />
            </div>

            {error && <p className="text-red-600 text-sm">{error}</p>}

            <Button
              type="submit"
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 w-full"
            >
              {loading ? "Ingresando..." : "Iniciar Sesión"}
            </Button>
          </form>
        </Card>

        <Link href="/">
          <Button variant="ghost" className="mt-4 w-full">
            <ArrowLeft className="mr-2 size-4" />
            Volver al inicio
          </Button>
        </Link>
      </div>
    </div>
  );
}