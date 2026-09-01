import api from "./axios";

export interface WithdrawRequest {
    amount: number;
}

export interface WithdrawResponse {
    id: number;
    balance: number;
}

export const withdrawMoney = async (
    userId: number,
    request: WithdrawRequest
): Promise<WithdrawResponse> => {
    const response = await api.post<WithdrawResponse>(
        `/wallet/${userId}/withdraw`,
        request
    );

    return response.data;
};