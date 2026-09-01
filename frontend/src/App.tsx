import { Routes, Route, Navigate } from "react-router-dom";
import Deposit from "./pages/Deposit";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Transfer from "./pages/Transfer";
import Withdraw from "./pages/Withdraw";
import Transactions from "./pages/Transactions";
import Register from "./pages/Register";
import MainLayout from "./layouts/MainLayout";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
    return (
        <Routes>

            {/* Public routes */}

            <Route
                path="/login"
                element={<Login />}
            />

            <Route
                path="/register"
                element={<Register />}
            />


            {/* Protected routes */}

            <Route element={<ProtectedRoute />}>

                <Route element={<MainLayout />}>

                    <Route
                        path="/dashboard"
                        element={<Dashboard />}
                    />

                    <Route
                        path="/deposit"
                        element={<Deposit />}
                    />

                    <Route
                        path="/transfer"
                        element={<Transfer />}
                    />

                    <Route
                        path="/withdraw"
                        element={<Withdraw />}
                    />

                    <Route
                        path="/transactions"
                        element={<Transactions />}
                    />

                </Route>

            </Route>


            {/* Default route */}

            <Route
                path="*"
                element={<Navigate to="/dashboard" replace />}
            />

        </Routes>
    );
}

export default App;