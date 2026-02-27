import Link from "next/link";
import { Card } from "@/app/components/ui/card";
import { Button } from "@/app/components/ui/button";
import { LucideIcon } from "lucide-react";
import type { ModuleColor  } from "../../types/module";

interface ModuleCardProps {
  id: string;
  title: string;
  description: string;
  icon: LucideIcon;
  color: ModuleColor;
}

const colorClasses = {
  blue: "bg-blue-100 text-blue-600",
  green: "bg-green-100 text-green-600",
  purple: "bg-purple-100 text-purple-600",
  amber: "bg-amber-100 text-amber-600",
};

const buttonClasses = {
  blue: "bg-blue-600 hover:bg-blue-700",
  green: "bg-green-600 hover:bg-green-700",
  purple: "bg-purple-600 hover:bg-purple-700",
  amber: "bg-amber-600 hover:bg-amber-700",
};

export function ModuleCard({
  id,
  title,
  description,
  icon: Icon,
  color,
}: ModuleCardProps) {
  return (
    <Link href={`${id}`} className="block">
      <Card className="hover:shadow-lg p-6 border-slate-200 transition-all cursor-pointer">
        <div className="flex gap-4">
          <div
            className={`w-12 h-12 rounded-lg flex items-center justify-center ${colorClasses[color]}`}
          >
            <Icon className="size-6" />
          </div>

          <div className="flex-1">
            <h3 className="mb-2 font-semibold text-lg">{title}</h3>
            <p className="mb-4 text-slate-600 text-sm">{description}</p>

            <Button size="sm" className={buttonClasses[color]}>
              Acceder
            </Button>
          </div>
        </div>
      </Card>
    </Link>
  );
}