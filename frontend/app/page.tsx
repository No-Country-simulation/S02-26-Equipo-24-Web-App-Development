import Image from "next/image";
import Navbar from "./components/nav";
import { Button } from "./components/ui/button";
import { Card } from "./components/ui/card";
import { Brain, Target, Activity, Shield } from "lucide-react";
import Footer from "./components/footer";
import Link from "next/link";

export default function Home() {
  return (
    <div className="flex flex-col min-h-screen font-sans">
      <Navbar />
       <section className="mx-auto px-6 py-16 md:py-24 container">
        <div className="items-center gap-12 grid md:grid-cols-2">
          <div>
            <h1 className="mb-6 font-bold text-slate-900 text-4xl md:text-5xl">
              Simulación Digital para Robots Quirúrgicos
            </h1>
            <p className="mb-8 text-slate-600 text-lg leading-relaxed">
              Reduce el riesgo técnico, clínico y económico en el desarrollo de robots quirúrgicos mediante 
              simulaciones digitales avanzadas. Evalúa la interacción humano-robot y valida decisiones de diseño 
              antes de construir hardware complejo.
            </p>
            <Link href="/simulator">
              <Button  
                size="lg" 
                className="bg-blue-600 hover:bg-blue-700 px-8 text-lg"
              >
                Comenzar Simulación
              </Button>
            </Link>
          </div>
          <div className="relative">
            <Image
              src="https://images.unsplash.com/photo-1691935152212-596d5ee37383?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtZWRpY2FsJTIwcm9ib3QlMjBzdXJnZXJ5JTIwdGVjaG5vbG9neXxlbnwxfHx8fDE3NzA5MzQ0Njd8MA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral"
              alt="Tecnología médica robótica"
              className="shadow-2xl rounded-lg w-full"
              width={600}
              height={400}
            />
          </div>
        </div>
      </section>
       <section className="mx-auto px-6 py-16 container">
        <h2 className="mb-12 font-bold text-slate-900 text-3xl text-center">
          Beneficios de la Simulación Digital
        </h2>
        <div className="gap-6 grid md:grid-cols-2 lg:grid-cols-4">
          <Card className="hover:shadow-lg p-6 border-slate-200 transition-shadow">
            <div className="flex justify-center items-center bg-blue-100 mb-4 rounded-lg w-12 h-12">
              <Brain className="size-6 text-blue-600" />
            </div>
            <h3 className="mb-2 font-semibold text-slate-900">Validación de Diseño</h3>
            <p className="text-slate-600">
              Valida decisiones de diseño antes de construir prototipos físicos costosos.
            </p>
          </Card>

          <Card className="hover:shadow-lg p-6 border-slate-200 transition-shadow">
            <div className="flex justify-center items-center bg-green-100 mb-4 rounded-lg w-12 h-12">
              <Target className="size-6 text-green-600" />
            </div>
            <h3 className="mb-2 font-semibold text-slate-900">Evaluación de Interacción</h3>
            <p className="text-slate-600">
              Analiza la interacción humano-robot en entornos quirúrgicos simulados.
            </p>
          </Card>

          <Card className="hover:shadow-lg p-6 border-slate-200 transition-shadow">
            <div className="flex justify-center items-center bg-purple-100 mb-4 rounded-lg w-12 h-12">
              <Activity className="size-6 text-purple-600" />
            </div>
            <h3 className="mb-2 font-semibold text-slate-900">Simulación de Procedimientos</h3>
            <p className="text-slate-600">
              Simula procedimientos quirúrgicos complejos de forma segura y repetible.
            </p>
          </Card>

          <Card className="hover:shadow-lg p-6 border-slate-200 transition-shadow">
            <div className="flex justify-center items-center bg-amber-100 mb-4 rounded-lg w-12 h-12">
              <Shield className="size-6 text-amber-600" />
            </div>
            <h3 className="mb-2 font-semibold text-slate-900">Reducción de Riesgo</h3>
            <p className="text-slate-600">
              Minimiza riesgos técnicos, clínicos y económicos en las etapas tempranas.
            </p>
          </Card>
        </div>
      </section>
       <Footer />
    </div>
  );
}
