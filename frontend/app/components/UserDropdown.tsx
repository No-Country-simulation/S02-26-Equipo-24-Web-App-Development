"use client";
import { useState, useRef, useEffect } from "react";
import { useRouter } from "next/navigation";
import { ChevronDown, LogOut } from "lucide-react";
import { useUserStore } from "@/app/store/user.store";

export default function UserDropdown() {
  const [isOpen, setIsOpen] = useState(false);
  const username = useUserStore((s) => s.username);
  const clearUser = useUserStore((s) => s.clearUser);
  const router = useRouter();
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Cerrar dropdown cuando se hace clic fuera
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    clearUser();
    setIsOpen(false);
    router.push("/");
  };

  if (!username) return null;

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center gap-2 hover:bg-slate-100 px-4 py-2 rounded-lg transition-colors"
      >
        <span className="font-semibold text-slate-900">{username}</span>
        <ChevronDown
          className={`size-5 text-slate-600 transition-transform ${
            isOpen ? "rotate-180" : ""
          }`}
        />
      </button>

      {/* Dropdown Menu */}
      {isOpen && (
        <div className="right-0 z-50 absolute bg-white shadow-lg mt-2 py-1 border border-slate-200 rounded-lg w-48">
          <button
            onClick={handleLogout}
            className="flex items-center gap-3 hover:bg-red-50 px-4 py-2.5 w-full text-slate-900 text-left transition-colors"
          >
            <LogOut className="size-4 text-red-600" />
            <span className="font-medium text-sm">Cerrar Sesión</span>
          </button>
        </div>
      )}
    </div>
  );
}
