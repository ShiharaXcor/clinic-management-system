"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/lib/authStore";

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles?: string[]; // optional - if omitted, any authenticated user can access
}

export function ProtectedRoute({ children, allowedRoles }: ProtectedRouteProps) {
  const router = useRouter();
  const { user, isAuthenticated, hydrate } = useAuthStore();
  const [checked, setChecked] = useState(false);

  useEffect(() => {
    hydrate();
    setChecked(true);
  }, [hydrate]);

  useEffect(() => {
    if (!checked) return;

    const token = localStorage.getItem("accessToken");
    if (!token) {
      router.push("/login");
      return;
    }

    if (allowedRoles && user && !allowedRoles.includes(user.role)) {
      router.push("/dashboard"); // logged in, but wrong role -> bounce to dashboard
    }
  }, [checked, user, allowedRoles, router]);

  if (!checked) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-white dark:bg-[#0B1220]">
        <p className="text-sm text-slate-500 dark:text-slate-400">Loading...</p>
      </div>
    );
  }

  return <>{children}</>;
}