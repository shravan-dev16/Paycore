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

export const depositMoney = async (
    userId: number,
    amount: number
): Promise<WalletResponse> => {

    const response = await api.post<WalletResponse>(
        `/wallet/${userId}/deposit`,
        {
            amount,
        }
    );

    return response.data;
};

export const withdrawMoney = async (
    userId: number,
    amount: number
): Promise<WalletResponse> => {

    const response = await api.post<WalletResponse>(
        `/wallet/${userId}/withdraw`,
        {
            amount,
        }
    );

    return response.data;
};