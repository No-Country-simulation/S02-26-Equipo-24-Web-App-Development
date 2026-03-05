import Link from "next/link";
import { Button } from "./ui/button";
import { Activity } from "lucide-react";
import UserDropdown from "./UserDropdown";
import { meAction } from "@/app/lib/actions/me.action";

type User = {
  username: string;
  // otros campos si tienes
};

export default async function Navbar() {
  
  const user = await meAction();
  return (
    <header className="top-0 z-50 sticky bg-white/80 backdrop-blur-sm border-b">
      <div className="flex justify-between items-center mx-auto px-6 py-4 container">
        <Link href="/">
        <div className="flex items-center gap-2">
          <Activity className="size-8 text-blue-600" />
          <span className="font-semibold text-slate-900 text-xl">
            RoboSim
          </span>
        </div>
        </Link>
          {user ? (
        <UserDropdown initialUser={user} />
        ) : ( <Link href="/login">
          <Button className="bg-blue-600 hover:bg-blue-700">
            Iniciar Sesión
          </Button>
        </Link>
        )}
        
      </div>
    </header>
  );
}