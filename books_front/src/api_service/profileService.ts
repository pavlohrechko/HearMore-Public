import axios from "axios";
import { User } from "@/types";

const API_URL = "http://localhost:8080/api/user/full_profile";

export const getUserProfile = async (): Promise<User> => {
    try {
        const jwt = localStorage.getItem("jwt");
        const token = localStorage.getItem("token");

        const response = await axios.get<User>(API_URL, {
            headers: {
                Authorization: `Bearer ${jwt}`,
                Authentication: token,
            },
        });
        console.log("User profile fetched successfully:", response.data);
        return response.data;
    } catch (error: any) {
        console.error("Error fetching user profile:", error.response?.status, error.message);
        throw error;
    }
};
