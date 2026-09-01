import { useState } from "react";
import type { FormEvent } from "react";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../api/registerApi";

function Register() {
    const navigate = useNavigate();

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const registerMutation = useMutation({
        mutationFn: registerUser,

        onSuccess: () => {
            navigate("/login");
        },
    });

    const handleSubmit = (event: FormEvent) => {
        event.preventDefault();

        registerMutation.mutate({
            name,
            email,
            password,
        });
    };

    return (
        <div className="flex min-h-screen items-center justify-center bg-gray-100">

            <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow-lg">

                <h1 className="mb-2 text-3xl font-bold">
                    Create your PayCore account
                </h1>

                <p className="mb-6 text-gray-500">
                    Register to create your wallet.
                </p>

                <form
                    onSubmit={handleSubmit}
                    className="space-y-4"
                >

                    <div>
                        <label className="mb-1 block text-sm font-medium">
                            Name
                        </label>

                        <input
                            type="text"
                            value={name}
                            onChange={(e) =>
                                setName(e.target.value)
                            }
                            className="w-full rounded-lg border p-3 outline-none focus:ring-2"
                            placeholder="Your name"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-1 block text-sm font-medium">
                            Email
                        </label>

                        <input
                            type="email"
                            value={email}
                            onChange={(e) =>
                                setEmail(e.target.value)
                            }
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
                            onChange={(e) =>
                                setPassword(e.target.value)
                            }
                            className="w-full rounded-lg border p-3 outline-none focus:ring-2"
                            placeholder="••••••••"
                            required
                        />
                    </div>

                    {registerMutation.isError && (
                        <p className="text-sm text-red-600">
                            Registration failed. Please check your details.
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={registerMutation.isPending}
                        className="w-full rounded-lg bg-black p-3 font-semibold text-white disabled:opacity-50"
                    >
                        {registerMutation.isPending
                            ? "Creating account..."
                            : "Create Account"}
                    </button>

                </form>

                <p className="mt-6 text-center text-sm text-gray-500">
                    Already have an account?{" "}
                    <button
                        type="button"
                        onClick={() => navigate("/login")}
                        className="font-semibold text-black hover:underline"
                    >
                        Login
                    </button>
                </p>

            </div>

        </div>
    );
}

export default Register;