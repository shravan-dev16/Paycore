import { useState } from "react";
import type { FormEvent } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { depositMoney } from "../api/depositApi";

function Deposit() {

    const userId = Number(localStorage.getItem("userId"));

    const queryClient = useQueryClient();

    const [amount, setAmount] = useState("");

    const depositMutation = useMutation({
        mutationFn: () =>
            depositMoney(userId, {
                amount: Number(amount),
            }),

        onSuccess: () => {
            setAmount("");

            queryClient.invalidateQueries({
                queryKey: ["wallet", userId],
            });

            queryClient.invalidateQueries({
                queryKey: ["transactions", userId],
            });
        },
    });

    const handleSubmit = (event: FormEvent) => {
        event.preventDefault();

        if (Number(amount) <= 0) {
            return;
        }

        depositMutation.mutate();
    };

    return (
        <div>

            <div className="mb-8">

                <h1 className="text-3xl font-bold">
                    Deposit Money
                </h1>

                <p className="mt-2 text-gray-500">
                    Add money to your PayCore wallet.
                </p>

            </div>

            <div className="max-w-xl rounded-2xl bg-white p-8 shadow-sm">

                <form
                    onSubmit={handleSubmit}
                    className="space-y-6"
                >

                    <div>

                        <label className="mb-2 block text-sm font-medium">
                            Amount
                        </label>

                        <div className="relative">

                            <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500">
                                ₹
                            </span>

                            <input
                                type="number"
                                min="0.01"
                                step="0.01"
                                value={amount}
                                onChange={(e) =>
                                    setAmount(e.target.value)
                                }
                                placeholder="Enter amount"
                                className="w-full rounded-lg border p-3 pl-9 outline-none focus:ring-2"
                                required
                            />

                        </div>

                    </div>


                    {depositMutation.isError && (
                        <div className="rounded-lg bg-red-50 p-4 text-sm text-red-600">
                            Deposit failed. Please try again.
                        </div>
                    )}


                    {depositMutation.isSuccess && (
                        <div className="rounded-lg bg-green-50 p-4 text-sm text-green-600">
                            Deposit completed successfully.
                        </div>
                    )}


                    <button
                        type="submit"
                        disabled={
                            depositMutation.isPending ||
                            !amount ||
                            Number(amount) <= 0
                        }
                        className="w-full rounded-lg bg-black p-3 font-semibold text-white disabled:cursor-not-allowed disabled:opacity-40"
                    >
                        {depositMutation.isPending
                            ? "Processing..."
                            : "Deposit Money"}
                    </button>

                </form>

            </div>

        </div>
    );
}

export default Deposit;