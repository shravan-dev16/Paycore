import { useState } from "react";
import type { FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
import { loginUser } from "../api/authApi";

function Login() {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const loginMutation = useMutation({
        mutationFn: loginUser,

        onSuccess: (data) => {
            localStorage.setItem("token", data.token);
            localStorage.setItem("userId", data.userId.toString());

            navigate("/dashboard");
        },
    });

    const handleSubmit = (event: FormEvent) => {
        event.preventDefault();

        loginMutation.mutate({
            email,
            password,
        });
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow-lg">

                <h1 className="mb-2 text-3xl font-bold">
                    Welcome to PayCore
                </h1>

                <p className="mb-6 text-gray-500">
                    Login to your wallet
                </p>

                <form onSubmit={handleSubmit} className="space-y-4">

                    <div>
                        <label className="mb-1 block text-sm font-medium">
                            Email
                        </label>

                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="w-full rounded-lg border p-3 outline-none focus:ring-2"
                            placeholder="you@example.com"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-sm font-medium">
                            Password
                        </label>

                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full rounded-lg border p-3 outline-none focus:ring-2"
                            placeholder="••••••••"
                            required
                        />
                    </div>

                    {loginMutation.isError && (
                        <p className="text-sm text-red-600">
                            Login failed. Please check your email and password.
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={loginMutation.isPending}
                        className="w-full rounded-lg bg-black p-3 font-semibold text-white disabled:opacity-50"
                    >
                        {loginMutation.isPending ? "Logging in..." : "Login"}
                    </button>

                </form>

            </div>
        </div>
    );
}

export default Login;