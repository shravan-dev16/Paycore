import { useState } from "react";
import type { FormEvent } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { transferMoney } from "../api/transferApi";
import { useAuth } from "../hooks/useAuth";

function Transfer() {

    const queryClient = useQueryClient();

    const { data: user } = useAuth();

    const userId = user?.id;

    const [receiverId, setReceiverId] = useState("");
    const [amount, setAmount] = useState("");

    const transferMutation = useMutation({
        mutationFn: () =>
            transferMoney(
                userId!,
                {
                    receiverId: Number(receiverId),
                    amount: Number(amount),
                }
            ),

        onSuccess: () => {

            setReceiverId("");
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

        if (
            !receiverId ||
            Number(receiverId) <= 0 ||
            !amount ||
            Number(amount) <= 0
        ) {
            return;
        }

        if (Number(receiverId) === userId) {
            return;
        }

        transferMutation.mutate();
    };

    return (
        <div className="max-w-2xl">

            <div className="mb-8">

                <h1 className="text-3xl font-bold">
                    Transfer Money
                </h1>

                <p className="mt-2 text-gray-500">
                    Send money to another PayCore user.
                </p>

            </div>


            <div className="rounded-2xl bg-white p-8 shadow-sm">

                <form
                    onSubmit={handleSubmit}
                    className="space-y-6"
                >

                    {/* Receiver */}

                    <div>

                        <label className="mb-2 block text-sm font-medium">
                            Receiver ID
                        </label>

                        <input
                            type="number"
                            min="1"
                            value={receiverId}
                            onChange={(e) =>
                                setReceiverId(e.target.value)
                            }
                            placeholder="Enter receiver ID"
                            required
                            className="w-full rounded-lg border border-gray-300 p-3 outline-none focus:ring-2"
                        />

                        <p className="mt-2 text-xs text-gray-500">
                            Enter the PayCore user ID of the person you
                            want to send money to.
                        </p>

                    </div>


                    {/* Amount */}

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
                                required
                                className="w-full rounded-lg border border-gray-300 p-3 pl-9 outline-none focus:ring-2"
                            />

                        </div>

                    </div>


                    {/* Same user */}

                    {receiverId &&
                        Number(receiverId) === userId && (
                            <div className="rounded-lg bg-red-50 p-3 text-sm text-red-600">
                                You cannot transfer money to yourself.
                            </div>
                        )}


                    {/* Error */}

                    {transferMutation.isError && (
                        <div className="rounded-lg bg-red-50 p-3 text-sm text-red-600">
                            Transfer failed. Please check the receiver
                            and available balance.
                        </div>
                    )}


                    {/* Success */}

                    {transferMutation.isSuccess && (
                        <div className="rounded-lg bg-green-50 p-3 text-sm text-green-700">
                            Transfer completed successfully.
                        </div>
                    )}


                    {/* Submit */}

                    <button
                        type="submit"
                        disabled={
                            transferMutation.isPending ||
                            !userId ||
                            !receiverId ||
                            Number(receiverId) <= 0 ||
                            Number(receiverId) === userId ||
                            !amount ||
                            Number(amount) <= 0
                        }
                        className="w-full rounded-lg bg-black p-3 font-semibold text-white transition hover:bg-gray-800 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        {transferMutation.isPending
                            ? "Processing..."
                            : "Send Money"}
                    </button>

                </form>

            </div>

        </div>
    );
}

export default Transfer;