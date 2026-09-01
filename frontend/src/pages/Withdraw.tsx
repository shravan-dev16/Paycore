import { useState } from "react";
import type { FormEvent } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { withdrawMoney } from "../api/walletApi";
import { useAuth } from "../hooks/useAuth";

function Withdraw() {

    const queryClient = useQueryClient();

    const { data: user } = useAuth();

    const userId = user?.id;

    const [amount, setAmount] = useState("");

    const withdrawMutation = useMutation({
        mutationFn: () =>
            withdrawMoney(
                userId!,
                Number(amount)
            ),

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

        if (!userId) {
            return;
        }

        if (!amount || Number(amount) <= 0) {
            return;
        }

        withdrawMutation.mutate();
    };

    return (
        <div>

            <div className="mb-8">

                <h1 className="text-3xl font-bold">
                    Withdraw Money
                </h1>

                <p className="mt-2 text-gray-500">
                    Withdraw money from your PayCore wallet.
                </p>

            </div>


            <div className="w-full max-w-xl rounded-2xl bg-white p-8 shadow-sm">

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
                                className="w-full rounded-lg border p-3 pl-9 outline-none focus:ring-2"
                                placeholder="Enter amount"
                                required
                            />

                        </div>

                    </div>


                    {withdrawMutation.isSuccess && (
                        <div className="rounded-lg bg-green-50 p-4 text-sm text-green-600">
                            Withdrawal completed successfully.
                        </div>
                    )}


                    {withdrawMutation.isError && (
                        <div className="rounded-lg bg-red-50 p-4 text-sm text-red-600">
                            Withdrawal failed. Please check your balance
                            and try again.
                        </div>
                    )}


                    <button
                        type="submit"
                        disabled={
                            withdrawMutation.isPending ||
                            !userId ||
                            !amount ||
                            Number(amount) <= 0
                        }
                        className="w-full rounded-lg bg-black p-3 font-semibold text-white disabled:cursor-not-allowed disabled:opacity-40"
                    >
                        {withdrawMutation.isPending
                            ? "Processing..."
                            : "Withdraw Money"}
                    </button>

                </form>

            </div>

        </div>
    );
}

export default Withdraw;