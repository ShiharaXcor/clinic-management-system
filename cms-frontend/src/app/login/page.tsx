"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Eye, EyeOff } from "lucide-react";
import { api } from "@/lib/api";
import { useAuthStore } from "@/lib/authStore";
import { ThemeToggle } from "../components/ThemeToggle";

function isValidEmail(value: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
}

export default function LoginPage() {
  const router = useRouter();
  const setAuth = useAuthStore((state) => state.setAuth);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [serverError, setServerError] = useState("");
  const [loading, setLoading] = useState(false);

  const [touched, setTouched] = useState({ email: false, password: false });
  const [fieldErrors, setFieldErrors] = useState({ email: "", password: "" });

  const validate = () => {
    const errors = { email: "", password: "" };

    if (!email) {
      errors.email = "Email is required";
    } else if (!isValidEmail(email)) {
      errors.email = "Enter a valid email address";
    }

    if (!password) {
      errors.password = "Password is required";
    } else if (password.length < 8) {
      errors.password = "Password must be at least 8 characters";
    }

    setFieldErrors(errors);
    return !errors.email && !errors.password;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setServerError("");
    setTouched({ email: true, password: true });

    if (!validate()) {
      return;
    }

    setLoading(true);
    try {
      const res = await api.post("/auth/login", { email, password });
      const { accessToken, refreshToken, email: userEmail, fullName, role } = res.data;

      setAuth({ email: userEmail, fullName, role }, accessToken, refreshToken);
      router.push("/dashboard");
    } catch (err: any) {
      setServerError(err.response?.data?.error || "Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="flex min-h-screen items-center justify-center bg-white dark:bg-[#0B1220]">
      <div className="absolute right-6 top-6">
        <ThemeToggle />
      </div>

      <form
        onSubmit={handleSubmit}
        noValidate
        className="w-full max-w-sm space-y-4 rounded-xl border border-border bg-surface p-8 shadow-sm dark:border-border-dark dark:bg-surface-dark"
      >
        <div className="mb-2 flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-md bg-primary dark:bg-primary-dark">
            <span className="text-sm font-bold text-white">C</span>
          </div>
          <h1 className="text-lg font-semibold text-slate-900 dark:text-slate-100">
            Clinic Management System
          </h1>
        </div>
        <p className="text-sm text-slate-500 dark:text-slate-400">Sign in to continue</p>

        {serverError && (
          <div className="rounded-md bg-danger/10 px-3 py-2 text-sm text-danger dark:bg-danger-dark/10 dark:text-danger-dark">
            {serverError}
          </div>
        )}

        {/* Email field */}
        <div>
          <label className="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-300">
            Email
          </label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onBlur={() => {
              setTouched((t) => ({ ...t, email: true }));
              validate();
            }}
            className={`w-full rounded-md border bg-white px-3 py-2 text-sm text-slate-900 focus:outline-none focus:ring-1 dark:bg-[#0B1220] dark:text-slate-100 ${
              touched.email && fieldErrors.email
                ? "border-danger focus:border-danger focus:ring-danger dark:border-danger-dark"
                : "border-border focus:border-primary focus:ring-primary dark:border-border-dark dark:focus:border-primary-dark dark:focus:ring-primary-dark"
            }`}
          />
          {touched.email && fieldErrors.email && (
            <p className="mt-1 text-xs text-danger dark:text-danger-dark">{fieldErrors.email}</p>
          )}
        </div>

        {/* Password field */}
        <div>
          <label className="mb-1 block text-sm font-medium text-slate-700 dark:text-slate-300">
            Password
          </label>
          <div className="relative">
            <input
              type={showPassword ? "text" : "password"}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              onBlur={() => {
                setTouched((t) => ({ ...t, password: true }));
                validate();
              }}
              className={`w-full rounded-md border bg-white px-3 py-2 pr-10 text-sm text-slate-900 focus:outline-none focus:ring-1 dark:bg-[#0B1220] dark:text-slate-100 ${
                touched.password && fieldErrors.password
                  ? "border-danger focus:border-danger focus:ring-danger dark:border-danger-dark"
                  : "border-border focus:border-primary focus:ring-primary dark:border-border-dark dark:focus:border-primary-dark dark:focus:ring-primary-dark"
              }`}
            />
            <button
              type="button"
              onClick={() => setShowPassword((v) => !v)}
              tabIndex={-1}
              className="absolute inset-y-0 right-0 flex items-center px-3 text-slate-400 hover:text-slate-600 dark:text-slate-500 dark:hover:text-slate-300"
              aria-label={showPassword ? "Hide password" : "Show password"}
            >
              {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          </div>
          {touched.password && fieldErrors.password && (
            <p className="mt-1 text-xs text-danger dark:text-danger-dark">{fieldErrors.password}</p>
          )}
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-md bg-primary px-3 py-2 text-sm font-medium text-white hover:opacity-90 disabled:opacity-50 dark:bg-primary-dark"
        >
          {loading ? "Signing in..." : "Sign in"}
        </button>
      </form>
    </main>
  );
}