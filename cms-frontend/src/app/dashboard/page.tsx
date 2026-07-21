"use client";

import { useRouter } from "next/navigation";
import { useAuthStore } from "@/lib/authStore";
import { ThemeToggle } from "../components/ThemeToggle";
import { ProtectedRoute } from "../components/ProtectedRoute";

function DashboardContent() {
  const router = useRouter();
  const { user, logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    router.push("/login");
  };

  return (
    <main className="min-h-screen bg-white p-8 dark:bg-[#0B1220]">
      <div className="mx-auto max-w-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h1 className="text-xl font-semibold text-slate-900 dark:text-slate-100">Dashboard</h1>
          <ThemeToggle />
        </div>

        <div className="rounded-xl border border-border bg-surface p-6 shadow-sm dark:border-border-dark dark:bg-surface-dark">
          {user && (
            <p className="text-sm text-slate-600 dark:text-slate-300">
              Welcome, <span className="font-medium text-slate-900 dark:text-slate-100">{user.fullName}</span>{" "}
              <span className="rounded-full bg-primary/10 px-2 py-0.5 text-xs font-medium text-primary dark:bg-primary-dark/10 dark:text-primary-dark">
                {user.role}
              </span>
            </p>
          )}
          <button
            onClick={handleLogout}
            className="mt-4 rounded-md bg-danger px-3 py-2 text-sm font-medium text-white hover:opacity-90 dark:bg-danger-dark"
          >
            Logout
          </button>
        </div>
      </div>
    </main>
  );
}

export default function DashboardPage() {
  return (
    <ProtectedRoute>
      <DashboardContent />
    </ProtectedRoute>
  );
}