import Navbar from "../components/nav";
import { Card } from "@/app/components/ui/card";
import { Activity } from "lucide-react";
import { modules } from "./module.data";
import { ModuleCard } from "../components/dashboard/ModuleCard";
import Footer from "@/app/components/footer";

export default function Dashboard() {
  return (
    <div className="bg-linear-to-b from-slate-50 to-white min-h-screen">
      <Navbar />

      <main className="mx-auto px-6 py-12 container">
        <div className="mb-12">
          <h1 className="mb-2 font-bold text-3xl">Bienvenido a RoboSim</h1>
          <p className="text-slate-600 text-lg">
            Selecciona un módulo para comenzar
          </p>
        </div>

        <div className="bg-blue-50 mb-8 p-6 border border-blue-200 rounded-lg">
          <div className="flex gap-4">
            <div className="bg-blue-600 p-2 rounded-full">
              <Activity className="size-5 text-white" />
            </div>
            <div>
              <h3 className="font-semibold text-blue-900">
                Sistema Operativo
              </h3>
              <p className="text-blue-800">
                Todos los módulos están disponibles.
              </p>
            </div>
          </div>
        </div>

        <div className="gap-6 grid md:grid-cols-2 mb-12">
          {modules.map((module) => (
            <ModuleCard key={module.id} {...module} />
          ))}
        </div>

        <Card className="bg-slate-50 p-6">
          <h3 className="mb-4 font-semibold">Acerca de la Plataforma</h3>
          <p className="text-slate-700">
            RoboSim permite simular procedimientos quirúrgicos y validar
            decisiones antes de construir hardware costoso.
          </p>
        </Card>
      </main>
      <Footer />
    </div>
  );
}