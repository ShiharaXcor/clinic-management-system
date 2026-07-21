import { create } from "zustand";

interface ThemeState {
  isDark: boolean;
  toggle: () => void;
  hydrate: () => void;
}

export const useThemeStore = create<ThemeState>((set, get) => ({
  isDark: false,

  toggle: () => {
    const newValue = !get().isDark;
    if (newValue) {
      document.documentElement.classList.add("dark");
    } else {
      document.documentElement.classList.remove("dark");
    }
    localStorage.setItem("theme", newValue ? "dark" : "light");
    set({ isDark: newValue });
  },

  hydrate: () => {
    const saved = localStorage.getItem("theme");
    const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
    const isDark = saved ? saved === "dark" : prefersDark;

    if (isDark) {
      document.documentElement.classList.add("dark");
    }
    set({ isDark });
  },
}));