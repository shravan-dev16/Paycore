import api from "./axios";

export interface CurrentUser {
    id: number;
    name: string;
    email: string;
}

export const getCurrentUser = async (): Promise<CurrentUser> => {
    const response = await api.get<CurrentUser>("/users/me");

    return response.data;
};