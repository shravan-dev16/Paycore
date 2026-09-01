import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function ProtectedRoute() {

    const token = localStorage.getItem("token");

    const {
        data: user,
        isLoading,
        isError,
    } = useAuth();

    if (!token) {
        return <Navigate to="/login" replace />;
    }

    if (isLoading) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <p className="text-gray-500">
                    Checking authentication...
                </p>
            </div>
        );
    }

    if (isError || !user) {
        localStorage.removeItem("token");
        localStorage.removeItem("userId");

        return <Navigate to="/login" replace />;
    }

    return <Outlet />;
}

export default ProtectedRoute;