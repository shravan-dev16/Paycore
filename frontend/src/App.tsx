import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import ProtectedRoute from "./components/ProtectedRoute";
import DashboardLayout from "./layouts/DashboardLayout";
import Dashboard from "./pages/Dashboard";
function App() {
    return (
        <Routes>

            {/* Public */}
            <Route
                path="/"
                element={<Navigate to="/login" replace />}
            />

            <Route
                path="/login"
                element={<Login />}
            />

            <Route
                path="/register"
                element={<h1>Register</h1>}
            />

            {/* Protected */}
            <Route element={<ProtectedRoute />}>

                <Route element={<DashboardLayout />}>

                    <Route
                        path="/dashboard"
                        element={<Dashboard />}
                    />

                    <Route
                        path="/transfer"
                        element={
                            <h1 className="text-3xl font-bold">
                                Transfer
                            </h1>
                        }
                    />

                    <Route
                        path="/transactions"
                        element={
                            <h1 className="text-3xl font-bold">
                                Transactions
                            </h1>
                        }
                    />

                </Route>

            </Route>

        </Routes>
    );
}

export default App;