import api from "./axios";
import type { WalletResponse } from "../types/wallet";

export const getWallet = async (
    userId: number
): Promise<WalletResponse> => {
    const response = await api.get<WalletResponse>(
        `/wallet/${userId}`
    );

    return response.data;
};