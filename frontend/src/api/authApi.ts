import api from "./axios";
import type { LoginRequest, LoginResponse } from "../types/auth";

export const loginUser = async (
    credentials: LoginRequest
): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>(
        "/users/login",
        credentials
    );

    return response.data;
};