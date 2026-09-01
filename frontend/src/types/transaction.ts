export interface Transaction {
    id: string;
    amount: number;
    type: string;
    status: string;
    senderId: number | null;
    receiverId: number | null;
    createdAt: string;
}

export interface TransactionPage {
    content: Transaction[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    first: boolean;
    last: boolean;
    numberOfElements: number;
    empty: boolean;
}