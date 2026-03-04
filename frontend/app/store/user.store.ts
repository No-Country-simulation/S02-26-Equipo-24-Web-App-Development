import { create } from "zustand";

type UserState = {
  username: string | null;
  token: string | null;
  id: string | null;
  setUsername: (username: string) => void;
  setToken: (token: string) => void;
  setId: (id: string) => void;
  setUser: (username: string, token: string, id: string) => void;
  clearUser: () => void;
};

export const useUserStore = create<UserState>((set) => ({
  username: null,
  token: null,
  id: null,
  setUsername: (username) => set({ username }),
  setToken: (token) => set({ token }),
  setId: (id) => set({ id }),
  setUser: (username, token, id) => set({ username, token, id }),
  clearUser: () => set({ username: null, token: null, id: null }),
}));