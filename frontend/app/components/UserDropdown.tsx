"use client";

import { useEffect, useState, useRef } from "react";
import { ChevronDown, LogOut } from "lucide-react";
import { useUserStore } from "@/app/store/user.store";
import { logoutAction } from "@/app/lib/actions/logout.action";

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
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (initialUser) {
      setUser(initialUser);
    }
  }, [initialUser, setUser]);

  const handleLogout = async () => {
    clearUser();
    await logoutAction();
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