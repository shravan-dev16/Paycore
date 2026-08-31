import { useWallet } from "../hooks/useWallet";

function Dashboard() {
    const { data, isLoading, isError } = useWallet();

    if (isLoading) {
        return <p className="text-gray-500">Loading wallet...</p>;
    }

    if (isError) {
        return (
            <div className="rounded-lg bg-red-50 p-4 text-red-600">
                Failed to load wallet.
            </div>
        );
    }

    return (
        <div>
            <div className="mb-8">
                <h1 className="text-3xl font-bold">Dashboard</h1>
                <p className="mt-1 text-gray-500">
                    Here's an overview of your PayCore wallet.
                </p>
            </div>

            <div className="max-w-md rounded-2xl bg-black p-6 text-white shadow-lg">
                <p className="text-sm text-gray-400">
                    Available Balance
                </p>

                <p className="mt-2 text-4xl font-bold">
                    ₹{data?.balance.toFixed(2)}
                </p>

                <p className="mt-4 text-sm text-gray-400">
                    Wallet ID: #{data?.id}
                </p>
            </div>
        </div>
    );
}

export default Dashboard;