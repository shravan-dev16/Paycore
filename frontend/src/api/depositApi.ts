import api from "./axios";
import type { WalletResponse } from "../types/wallet";

export interface DepositRequest {
    amount: number;
}

export const depositMoney = async (
    userId: number,
    request: DepositRequest
): Promise<WalletResponse> => {

    const response = await api.post<WalletResponse>(
        `/wallet/${userId}/deposit`,
        request
    );

    return response.data;
};