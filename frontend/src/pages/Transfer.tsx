import { useState } from "react";
import type { FormEvent } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { transferMoney } from "../api/transferApi";

function Transfer() {

    const queryClient = useQueryClient();

    const userId = Number(localStorage.getItem("userId"));

    const [receiverId, setReceiverId] = useState("");
    const [amount, setAmount] = useState("");

    const transferMutation = useMutation({
        mutationFn: () =>
            transferMoney(userId, {
                receiverId: Number(receiverId),
                amount: Number(amount),
            }),

        onSuccess: () => {
            setReceiverId("");
            setAmount("");

            queryClient.invalidateQueries({
                queryKey: ["wallet", userId],
            });
        },
    });

    const handleSubmit = (event: FormEvent) => {
        event.preventDefault();

        transferMutation.mutate();
    };

    return (
        <div className="max-w-2xl">

            <h1 className="text-3xl font-bold">
                Transfer Money
            </h1>

            <p className="mt-2 text-gray-500">
                Send money to another PayCore user.
            </p>

            <div className="mt-8 rounded-2xl bg-white p-8 shadow-sm">

                <form
                    onSubmit={handleSubmit}
                    className="space-y-6"
                >

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
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-medium">
                            Amount
                        </label>

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
                            className="w-full rounded-lg border border-gray-300 p-3 outline-none focus:ring-2"
                        />
                    </div>

                    {transferMutation.isError && (
                        <div className="rounded-lg bg-red-50 p-3 text-sm text-red-600">
                            Transfer failed. Please check the receiver and
                            available balance.
                        </div>
                    )}

                    {transferMutation.isSuccess && (
                        <div className="rounded-lg bg-green-50 p-3 text-sm text-green-700">
                            Transfer completed successfully.
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={transferMutation.isPending}
                        className="w-full rounded-lg bg-black p-3 font-semibold text-white transition hover:bg-gray-800 disabled:opacity-50"
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