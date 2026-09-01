import api from "./axios";

export interface WalletResponse {
    id: number;
    balance: number;
}

export const getWallet = async (
    userId: number
): Promise<WalletResponse> => {
    const response = await api.get<WalletResponse>(
        `/wallet/${userId}`
    );

    return response.data;
};