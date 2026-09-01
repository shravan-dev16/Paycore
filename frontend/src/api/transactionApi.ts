import api from "./axios";
import type { TransactionPage } from "../types/transaction";

export const getTransactions = async (
    userId: number,
    page: number = 0,
    size: number = 10
): Promise<TransactionPage> => {

    const response = await api.get<TransactionPage>(
        `/transactions/user/${userId}`,
        {
            params: {
                page,
                size,
            },
        }
    );

    return response.data;
};