"use client";

import { useEffect } from "react";
import { useSearchParams } from "next/navigation";
import { useUserStore } from "@/app/store/user.store";

export default function UserHydrator() {
  const searchParams = useSearchParams();
  const setUsername = useUserStore((s) => s.setUsername);

  useEffect(() => {
    const username = searchParams.get("username");
    if (username) {
      setUsername(username);
    }
  }, [searchParams, setUsername]);

  return null; 
}