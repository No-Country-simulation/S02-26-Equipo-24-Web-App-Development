import { create } from "zustand";

type UserState = {
  username: string | null;
  setUsername: (username: string) => void;
  clearUser: () => void;
};

export const useUserStore = create<UserState>((set) => ({
  username: null,
  setUsername: (username) => set({ username }),
  clearUser: () => set({ username: null }),
}));