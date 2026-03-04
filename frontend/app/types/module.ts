import { LucideIcon } from "lucide-react";

export type ModuleColor = "blue" | "green" | "purple" | "amber";

export interface Module {
  id: string;
  title: string;
  description: string;
  icon: LucideIcon;
  color: ModuleColor;
  available: boolean;
}