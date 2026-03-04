import { create } from "zustand";

type UserState = {
  username: string | null;
  setUser: (user: { username: string }) => void;
  clearUser: () => void;
};

export const useUserStore = create<UserState>((set) => ({
  username: null,
  setUser: (user) => set({ username: user.username }),
  clearUser: () => set({ username: null }),
}));