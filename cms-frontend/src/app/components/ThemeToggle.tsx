"use client";

import { useEffect } from "react";
import { useThemeStore } from "@/lib/themeStore";

export function ThemeToggle() {
  const { isDark, toggle, hydrate } = useThemeStore();

  useEffect(() => {
    hydrate();
  }, [hydrate]);

  return (
    <button
      onClick={toggle}
      className="rounded-md border border-border px-3 py-1.5 text-sm text-slate-600 hover:bg-surface dark:border-border-dark dark:text-slate-300 dark:hover:bg-surface-dark"
      aria-label="Toggle dark mode"
    >
      {isDark ? "☀️ Light" : "🌙 Dark"}
    </button>
  );
}