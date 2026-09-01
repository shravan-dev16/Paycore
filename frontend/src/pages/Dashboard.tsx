import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { getWallet } from "../api/walletApi";
import { getTransactions } from "../api/transactionApi";
import { useAuth } from "../hooks/useAuth";

function Dashboard() {

    const navigate = useNavigate();

    const { data: user } = useAuth();

    const userId = user?.id;

    const {
        data: wallet,
        isLoading: walletLoading,
        isError: walletError,
    } = useQuery({
        queryKey: ["wallet", userId],
        queryFn: () => getWallet(userId!),
        enabled: !!userId,
    });

    const {
        data: transactions,
        isLoading: transactionsLoading,
    } = useQuery({
        queryKey: ["transactions", userId, 0],
        queryFn: () => getTransactions(userId!, 0, 5),
        enabled: !!userId,
    });

    if (walletLoading) {
        return <div>Loading wallet...</div>;
    }

    if (walletError) {
        return <div>Failed to load wallet.</div>;
    }

    return (
        <div>

            {/* Header */}

            <div className="mb-8">

                <h1 className="text-3xl font-bold">
                    Dashboard
                </h1>

                <p className="mt-2 text-gray-500">
                    Here's an overview of your PayCore wallet.
                </p>

            </div>


            {/* Balance */}

            <div className="w-full max-w-lg rounded-2xl bg-black p-7 text-white shadow-lg">

                <p className="text-sm text-gray-400">
                    Available Balance
                </p>

                <h2 className="mt-3 text-4xl font-bold">
                    ₹{wallet?.balance?.toFixed(2)}
                </h2>

                <p className="mt-5 text-sm text-gray-400">
                    Wallet ID: #{wallet?.id}
                </p>

            </div>


            {/* Quick Actions */}

            <div className="mt-10">

                <h2 className="text-xl font-bold">
                    Quick Actions
                </h2>

                <div className="mt-4 grid max-w-lg grid-cols-3 gap-4">

                    <button
                        onClick={() => navigate("/transfer")}
                        className="rounded-xl bg-black p-5 text-sm font-semibold text-white shadow-sm transition hover:opacity-80"
                    >
                        Send Money
                    </button>

                    <button
                        onClick={() => navigate("/deposit")}
                        className="rounded-xl bg-white p-5 text-sm font-semibold shadow-sm transition hover:bg-gray-50"
                    >
                        Deposit
                    </button>

                    <button
                        onClick={() => navigate("/withdraw")}
                        className="rounded-xl bg-white p-5 text-sm font-semibold shadow-sm transition hover:bg-gray-50"
                    >
                        Withdraw
                    </button>

                </div>

            </div>


            {/* Recent Transactions */}

            <div className="mt-10">

                <div className="flex max-w-3xl items-center justify-between">

                    <div>

                        <h2 className="text-xl font-bold">
                            Recent Transactions
                        </h2>

                        <p className="mt-1 text-sm text-gray-500">
                            Your latest wallet activity.
                        </p>

                    </div>

                    <button
                        onClick={() => navigate("/transactions")}
                        className="text-sm font-medium underline"
                    >
                        View all
                    </button>

                </div>


                <div className="mt-4 max-w-3xl overflow-hidden rounded-2xl bg-white shadow-sm">

                    {transactionsLoading ? (

                        <p className="p-6 text-gray-500">
                            Loading transactions...
                        </p>

                    ) : transactions?.empty ? (

                        <p className="p-6 text-gray-500">
                            No transactions yet.
                        </p>

                    ) : (

                        transactions?.content.map((transaction) => {

                            const isIncoming =
                                transaction.type === "DEPOSIT" ||
                                transaction.receiverId === userId;

                            const amountPrefix =
                                isIncoming ? "+" : "-";

                            let label = transaction.type;

                            if (transaction.type === "DEPOSIT") {
                                label = "Money added";
                            } else if (transaction.type === "WITHDRAWAL") {
                                label = "Money withdrawn";
                            } else if (transaction.type === "TRANSFER") {
                                label = isIncoming
                                    ? "Money received"
                                    : "Money sent";
                            }

                            return (
                                <div
                                    key={transaction.id}
                                    className="flex items-center justify-between border-b px-6 py-4 last:border-b-0"
                                >

                                    <div className="flex items-center gap-3">

                                        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-gray-100">
                                            {transaction.type === "DEPOSIT"
                                                ? "↓"
                                                : transaction.type === "WITHDRAWAL"
                                                    ? "↑"
                                                    : isIncoming
                                                        ? "←"
                                                        : "→"}
                                        </div>

                                        <div>

                                            <p className="text-sm font-medium">
                                                {label}
                                            </p>

                                            <p className="text-xs text-gray-500">
                                                {new Date(
                                                    transaction.createdAt
                                                ).toLocaleDateString("en-IN", {
                                                    day: "2-digit",
                                                    month: "short",
                                                    year: "numeric",
                                                })}
                                            </p>

                                        </div>

                                    </div>


                                    <span
                                        className={
                                            isIncoming
                                                ? "text-sm font-semibold text-green-600"
                                                : "text-sm font-semibold text-red-600"
                                        }
                                    >
                                        {amountPrefix}₹
                                        {transaction.amount.toFixed(2)}
                                    </span>

                                </div>
                            );
                        })

                    )}

                </div>

            </div>

        </div>
    );
}

export default Dashboard;