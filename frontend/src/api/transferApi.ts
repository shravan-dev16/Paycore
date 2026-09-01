import api from "./axios";

export interface TransferRequest {
    receiverId: number;
    amount: number;
}

export interface TransferResponse {
    id: number;
    balance: number;
}

export const transferMoney = async (
    userId: number,
    request: TransferRequest
): Promise<TransferResponse> => {

    const idempotencyKey = crypto.randomUUID();

    const response = await api.post<TransferResponse>(
        `/wallet/${userId}/transfer`,
        request,
        {
            headers: {
                "Idempotency-Key": idempotencyKey,
            },
        }
    );

    return response.data;
};