import { NavLink, Outlet, useNavigate } from "react-router-dom";

function MainLayout() {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("userId");
        navigate("/login");
    };

    const linkClass = ({ isActive }: { isActive: boolean }) =>
        `block rounded-lg px-4 py-3 text-sm font-medium transition ${
            isActive
                ? "bg-white text-black"
                : "text-gray-300 hover:bg-gray-800 hover:text-white"
        }`;

    return (
        <div className="min-h-screen bg-gray-100">

            <aside className="fixed left-0 top-0 flex h-screen w-64 flex-col bg-black p-6 text-white">

                <h1 className="mb-10 text-2xl font-bold">
                    PayCore
                </h1>

                <nav className="space-y-2">

                    <NavLink
                        to="/dashboard"
                        className={linkClass}
                    >
                        Dashboard
                    </NavLink>

                    <NavLink
                        to="/deposit"
                        className={linkClass}
                    >
                        Deposit
                    </NavLink>

                    <NavLink
                        to="/withdraw"
                        className={linkClass}
                    >
                        Withdraw
                    </NavLink>

                    <NavLink
                        to="/transfer"
                        className={linkClass}
                    >
                        Transfer
                    </NavLink>

                    <NavLink
                        to="/transactions"
                        className={linkClass}
                    >
                        Transactions
                    </NavLink>

                </nav>


                <button
                    onClick={handleLogout}
                    className="mt-auto rounded-lg px-4 py-3 text-left text-sm font-medium text-gray-300 transition hover:bg-red-600 hover:text-white"
                >
                    Logout
                </button>

            </aside>

            <main className="ml-64 min-h-screen p-8">
                <Outlet />
            </main>

        </div>
    );
}

export default MainLayout;