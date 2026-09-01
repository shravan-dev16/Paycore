import api from "./axios";

export interface RegisterRequest {
    name: string;
    email: string;
    password: string;
}

export interface RegisterResponse {
    id: number;
    name: string;
    email: string;
}

export const registerUser = async (
    request: RegisterRequest
): Promise<RegisterResponse> => {
    const response = await api.post<RegisterResponse>(
        "/users/register",
        request
    );

    return response.data;
};