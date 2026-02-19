import { Activity, ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import Link from "next/link";

export default function Login() {
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
            Acceso a la Plataforma
          </h1>
          <p className="text-slate-600">
            Ingresa tus credenciales para acceder al sistema de simulación
          </p>
        </div>

        {/* Login Card */}
        <Card className="shadow-xl p-8 border-slate-200">
          <form className="space-y-6">
            <div className="space-y-2">
              <Label htmlFor="email">Correo Electrónico</Label>
              <Input
                id="email"
                type="email"
                placeholder="usuario@ejemplo.com"
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
                required
                className="border-slate-300"
              />
            </div>

            <Button 
              type="submit" 
              className="bg-blue-600 hover:bg-blue-700 w-full"
            
            >Iniciar Sesión
            </Button>
          </form>

          <div className="mt-6 text-slate-600 text-sm text-center">
            <p>¿Olvidaste tu contraseña? <button className="text-blue-600 hover:underline">Recuperar</button></p>
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

        {/* Demo Info */}
        <div className="bg-blue-50 mt-8 p-4 border border-blue-200 rounded-lg">
          <p className="text-blue-900 text-sm text-center">
            <strong>Modo Demostración:</strong> Ingresa cualquier correo y contraseña para acceder
          </p>
        </div>
      </div>
    </div>
    );   
}