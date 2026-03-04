"use client";

import { useEffect, useState, useRef } from "react";
import { useRouter } from "next/navigation";
import { ChevronDown, LogOut } from "lucide-react";
import { useUserStore } from "@/app/store/user.store";

type Props = {
  initialUser: {
    username: string;
  };
};

export default function UserDropdown({ initialUser }: Props) {
  const [isOpen, setIsOpen] = useState(false);
  const setUser = useUserStore((s) => s.setUser);
  const clearUser = useUserStore((s) => s.clearUser);
  const username = useUserStore((s) => s.username);
  const router = useRouter();
  const dropdownRef = useRef<HTMLDivElement>(null);

  // 🔥 hidratar store una sola vez
  useEffect(() => {
    if (initialUser) {
      setUser(initialUser);
    }
  }, [initialUser, setUser]);

  const handleLogout = () => {
    clearUser();
    router.push("/login");
  };

  if (!username) return null;

  return (
    <div ref={dropdownRef}>
      <button onClick={() => setIsOpen(!isOpen)}>
        {username} <ChevronDown />
      </button>

      {isOpen && (
        <button onClick={handleLogout}>
          <LogOut /> Cerrar sesión
        </button>
      )}
    </div>
  );
}