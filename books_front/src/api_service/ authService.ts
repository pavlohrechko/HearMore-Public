import axios from "axios";

const BASE_URL = "http://localhost:8080/api/auth";

interface LoginRequest {
    username: string;
    password: string;
}

interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export const login = async (data: LoginRequest) => {
    try {
        const response = await axios.post(`${BASE_URL}/login`, data);
        return response.data;
    } catch (error: any) {
        throw new Error(error.response?.data?.message || "Login failed.");
    }
};

export const register = async (data: RegisterRequest) => {
    try {
        const response = await axios.post(`${BASE_URL}/register`, data);
        return response.data;
    } catch (error: any) {
        throw new Error(error.response?.data?.message || "Registration failed.");
    }
};
