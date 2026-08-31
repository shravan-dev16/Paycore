import { useQuery } from "@tanstack/react-query";
import { getWallet } from "../api/walletApi";

export const useWallet = () => {
    const userId = localStorage.getItem("userId");

    return useQuery({
        queryKey: ["wallet", userId],
        queryFn: () => getWallet(Number(userId)),
        enabled: !!userId,
    });
};