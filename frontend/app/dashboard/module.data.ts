import { Play, FileText, Settings, BarChart3 } from "lucide-react";
import { Module } from "../types/module";

export const modules: Module[] = [
  {
    id: "simulator",
    title: "Nueva Simulación",
    description: "Inicia una nueva simulación de procedimiento quirúrgico robótico",
    icon: Play,
    color: "blue",
    available: true,
  },
  {
    id: "reports",
    title: "Reportes",
    description: "Visualiza y analiza los resultados de simulaciones anteriores",
    icon: FileText,
    color: "green",
    available: true,
  },
  {
    id: "analytics",
    title: "Análisis de Datos",
    description: "Métricas y estadísticas de rendimiento de los robots",
    icon: BarChart3,
    color: "purple",
    available: true,
  },
  {
    id: "config",
    title: "Configuración",
    description: "Ajusta parámetros de simulación y configuración del sistema",
    icon: Settings,
    color: "amber",
    available: true,
  },
];