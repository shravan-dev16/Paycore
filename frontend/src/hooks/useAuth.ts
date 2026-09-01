import { useQuery } from "@tanstack/react-query";
import { getCurrentUser } from "../api/auth";

export const useAuth = () => {

    const token = localStorage.getItem("token");

    return useQuery({
        queryKey: ["currentUser"],
        queryFn: getCurrentUser,
        enabled: !!token,
        retry: false,
    });
};