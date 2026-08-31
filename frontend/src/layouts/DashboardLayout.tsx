import { Link, Outlet, useNavigate } from "react-router-dom";

function DashboardLayout() {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/login", { replace: true });
    };

    return (
        <div className="min-h-screen bg-gray-100">

            {/* Sidebar */}
            <aside className="fixed left-0 top-0 h-screen w-64 bg-black text-white p-6">

                <h1 className="text-2xl font-bold mb-10">
                    PayCore
                </h1>

                <nav className="space-y-3">

                    <Link
                        to="/dashboard"
                        className="block rounded-lg px-4 py-3 hover:bg-gray-800"
                    >
                        Dashboard
                    </Link>

                    <Link
                        to="/transfer"
                        className="block rounded-lg px-4 py-3 hover:bg-gray-800"
                    >
                        Transfer
                    </Link>

                    <Link
                        to="/transactions"
                        className="block rounded-lg px-4 py-3 hover:bg-gray-800"
                    >
                        Transactions
                    </Link>

                </nav>

                <button
                    onClick={handleLogout}
                    className="absolute bottom-6 left-6 right-6 rounded-lg bg-gray-800 px-4 py-3 hover:bg-gray-700"
                >
                    Logout
                </button>

            </aside>

            {/* Main content */}
            <main className="ml-64 min-h-screen p-8">
                <Outlet />
            </main>

        </div>
    );
}

export default DashboardLayout;