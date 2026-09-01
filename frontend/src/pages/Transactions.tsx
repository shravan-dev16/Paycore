import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { getTransactions } from "../api/transactionApi";

function Transactions() {

    const userId = Number(localStorage.getItem("userId"));

    const [page, setPage] = useState(0);

    const transactionsQuery = useQuery({
        queryKey: ["transactions", userId, page],

        queryFn: () =>
            getTransactions(userId, page, 10),

        enabled: !!userId,
    });

    if (transactionsQuery.isLoading) {
        return (
            <div>
                <h1 className="text-3xl font-bold">
                    Transactions
                </h1>

                <p className="mt-4 text-gray-500">
                    Loading transactions...
                </p>
            </div>
        );
    }

    if (transactionsQuery.isError) {
        return (
            <div>
                <h1 className="text-3xl font-bold">
                    Transactions
                </h1>

                <p className="mt-4 text-red-600">
                    Failed to load transactions.
                </p>
            </div>
        );
    }

    const data = transactionsQuery.data;
    if (!data) {
        return (
            <div>
                <h1 className="text-3xl font-bold">
                    Transactions
                </h1>

                <p className="mt-4 text-gray-500">
                    No transaction data available.
                </p>
            </div>
        );
    }
    return (
        <div>

            <div className="mb-8">
                <h1 className="text-3xl font-bold">
                    Transactions
                </h1>

                <p className="mt-2 text-gray-500">
                    View your wallet transaction history.
                </p>
            </div>

            <div className="overflow-hidden rounded-2xl bg-white shadow-sm">

                <table className="w-full">

                    <thead className="border-b bg-gray-50">

                    <tr>

                        <th className="px-6 py-4 text-left text-sm font-semibold">
                            Transaction
                        </th>

                        <th className="px-6 py-4 text-left text-sm font-semibold">
                            Amount
                        </th>

                        <th className="px-6 py-4 text-left text-sm font-semibold">
                            Status
                        </th>

                        <th className="px-6 py-4 text-left text-sm font-semibold">
                            Date
                        </th>

                    </tr>

                    </thead>

                    <tbody>

                    {data.content.map((transaction) => {

                        const isIncoming =
                            transaction.type === "DEPOSIT" ||
                            transaction.receiverId === userId;

                        const amountPrefix =
                            isIncoming ? "+" : "-";

                        let transactionLabel = transaction.type;

                        if (transaction.type === "DEPOSIT") {
                            transactionLabel = "Money added";
                        } else if (transaction.type === "WITHDRAWAL") {
                            transactionLabel = "Money withdrawn";
                        } else if (transaction.type === "TRANSFER") {
                            transactionLabel = isIncoming
                                ? "Money received"
                                : "Money sent";
                        }

                        return (
                            <tr
                                key={transaction.id}
                                className="border-b last:border-b-0 hover:bg-gray-50"
                            >

                                <td className="px-6 py-5">

                                    <div className="flex items-center gap-3">

                                        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gray-100 text-lg">
                                            {transaction.type === "DEPOSIT"
                                                ? "↓"
                                                : transaction.type === "WITHDRAWAL"
                                                    ? "↑"
                                                    : isIncoming
                                                        ? "←"
                                                        : "→"}
                                        </div>

                                        <div>

                                            <p className="font-medium">
                                                {transactionLabel}
                                            </p>

                                            <p className="text-sm text-gray-500">
                                                {transaction.type === "TRANSFER"
                                                    ? isIncoming
                                                        ? `From user ${transaction.senderId}`
                                                        : `To user ${transaction.receiverId}`
                                                    : "Wallet transaction"}
                                            </p>

                                        </div>

                                    </div>

                                </td>

                                <td className="px-6 py-5">

                                        <span
                                            className={
                                                isIncoming
                                                    ? "font-semibold text-green-600"
                                                    : "font-semibold text-red-600"
                                            }
                                        >
                                            {amountPrefix}₹
                                            {transaction.amount.toFixed(2)}
                                        </span>

                                </td>

                                <td className="px-6 py-5">

                                        <span className="rounded-full bg-green-100 px-3 py-1 text-xs font-medium text-green-700">
                                            {transaction.status}
                                        </span>

                                </td>

                                <td className="px-6 py-5 text-sm text-gray-500">

                                    {new Date(
                                        transaction.createdAt
                                    ).toLocaleString("en-IN", {
                                        day: "2-digit",
                                        month: "short",
                                        year: "numeric",
                                        hour: "2-digit",
                                        minute: "2-digit",
                                    })}

                                </td>

                            </tr>
                        );
                    })}

                    </tbody>

                </table>

                {data.empty && (
                    <div className="p-10 text-center text-gray-500">
                        No transactions found.
                    </div>
                )}

            </div>

            <div className="mt-6 flex items-center justify-between">

                <button
                    onClick={() =>
                        setPage((current) => current - 1)
                    }
                    disabled={data.first}
                    className="rounded-lg bg-black px-5 py-2 text-sm font-medium text-white disabled:cursor-not-allowed disabled:opacity-30"
                >
                    Previous
                </button>

                <span className="text-sm text-gray-500">
                    Page {data.number + 1} of {data.totalPages}
                </span>

                <button
                    onClick={() =>
                        setPage((current) => current + 1)
                    }
                    disabled={data.last}
                    className="rounded-lg bg-black px-5 py-2 text-sm font-medium text-white disabled:cursor-not-allowed disabled:opacity-30"
                >
                    Next
                </button>

            </div>

        </div>
    );
}

export default Transactions;