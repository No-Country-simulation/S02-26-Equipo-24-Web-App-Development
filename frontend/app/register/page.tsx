import { Button } from "@/app/components/ui/button";
import { Input } from "@/app/components/ui/input";
import { Label } from "@/app/components/ui/label";
import { Card } from "@/app/components/ui/card";
import { Activity, ArrowLeft } from "lucide-react";
import Link from "next/link";
import { registerAction } from "./register.actions";



export default async function Login({
  searchParams,
}: {
  searchParams: Promise<{ error?: string }>;
}) {
  const params = await searchParams;

  const errorMessage =
    params.error === "invalid_credentials"
      ? "Correo o contraseña incorrectos"
      : null;

  return (
        <div className="flex justify-center items-center bg-linear-to-br from-slate-50 via-blue-50 to-slate-100 p-6 min-h-screen">
            <div className="w-full max-w-md">
                {/* Header */}
                <div className="mb-8 text-center">
                    <div className="flex justify-center items-center gap-2 mb-4">
                        <Activity className="size-10 text-blue-600" />
                        <span className="font-bold text-slate-900 text-2xl">RoboSim</span>
                    </div>
                    <h1 className="mb-2 font-semibold text-slate-900 text-2xl">
                        Crear Cuenta
                    </h1>
                    <p className="text-slate-600">
                        Regístrate para acceder al sistema de simulación quirúrgica
                    </p>
                </div>

                {/* Register Card */}
                <Card className="shadow-xl p-8 border-slate-200">
                    <form action={registerAction } className="space-y-5">
                        <div className="space-y-2">
                            <Label htmlFor="name">Nombre Completo</Label>
                            <Input
                                id="name"
                                name="name"
                                type="text"
                                placeholder="Dr. Juan Pérez"
                                required
                                className="border-slate-300"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="email">Correo Electrónico</Label>
                            <Input
                                id="email"
                                type="email"
                                placeholder="usuario@ejemplo.com"
                                name="email"
                                required
                                className="border-slate-300"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="institution">Institución</Label>
                            <Input
                                id="institution"
                                type="text"
                                placeholder="Hospital o Universidad"
                                name="institution"
                                required
                                className="border-slate-300"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="password">Contraseña</Label>
                            <Input
                                id="password"
                                type="password"
                                placeholder="••••••••"
                                name="password"
                                required
                                className="border-slate-300"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="confirmPassword">Confirmar Contraseña</Label>
                            <Input
                                id="confirmPassword"
                                type="password"
                                placeholder="••••••••"
                                name="confirmPassword"
                                required
                                className="border-slate-300"
                            />
                        </div>
                        {errorMessage && (
                            <p className="text-red-600 text-sm">{errorMessage}</p>
                        )}
                        <Button
                            type="submit"
                            className="bg-blue-600 hover:bg-blue-700 w-full"
                        >Crear Cuenta
                        </Button>
                    </form>

                    <div className="mt-6 text-slate-600 text-sm text-center">
                        <p>
                            ¿Ya tienes cuenta?{" "}
                            <Link href="/login">
                                <button
                                    className="font-medium text-blue-600 hover:underline"
                                >
                                    Iniciar Sesión
                                </button>
                            </Link>
                        </p>
                    </div>
                </Card>

                {/* Back Button */}
                <Link href="/">
                    <Button
                        variant="ghost"
                        className="mt-4 w-full text-slate-600 hover:text-slate-900"
                    >
                        <ArrowLeft className="mr-2 size-4" />
                        Volver al inicio
                    </Button>
                </Link>
            </div>
        </div>
    );
}