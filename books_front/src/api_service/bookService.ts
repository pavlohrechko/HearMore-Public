import { Book } from "@/types";
import axios from "axios";

const API_URL = "http://localhost:8080/api/books";

export interface Metadata {
    title: string;
    author: string;
    genre?: string;
    publication_date?: string;
    description?: string;
    voice: "alloy" | "joanna" | "shimmer" | "nova" | "onyx" | "fable"; // Add other possible voices if needed
}

export const bookService = async (): Promise<Book[]> => {
    try {
        const response = await axios.get<Book[]>(API_URL);
        return response.data;
    } catch (error) {
        console.error("Error fetching books:", error);
        throw error;
    }
};

export const uploadBook = async (
    pdfFile: File,
    metadata: Metadata,
    image: File
): Promise<void> => {
    const formData = new FormData();

    // Add metadata as a JSON blob
    formData.append(
        "metadata",
        new Blob([JSON.stringify(metadata)], { type: "application/json" })
    );

    // Add the PDF file
    formData.append("file", pdfFile);

    // Add the image file
    formData.append("image", image);

    try {
        const response = await axios.post(API_URL, formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });
        console.log("Book added successfully:", response.data);
    } catch (error) {
        console.error("Error uploading book:", error);
        throw error;
    }
};

export const getBookByIdWithSubscription = async (
    id: number,
    jwt: string,
    token: string
): Promise<Book> => {
    try {
        const response = await axios.get<Book>(`${API_URL}/auth/${id}`, {
            headers: {
                Authorization: `Bearer ${jwt}`,
                Authentication: token,
            },
        });
        return response.data.data;
    } catch (error) {
        console.error("Error fetching book by ID with subscription:", error);
        throw error;
    }
};


export const validateJwt = async (jwt: string, token: string): Promise<boolean> => {
    try {
        const response = await axios.get("http://localhost:8080/api/auth/validate", {
            headers: {
                Authorization: `Bearer ${jwt}`,
                Authentication: token,
            },
        });
        return response.data;
    } catch (error) {
        console.error("JWT validation failed:", error);
        return false;
    }
};

export const getBookById = async (id: number): Promise<Book> => {
    try {
        const response = await axios.get<Book>(`${API_URL}/${id}`);
        return response.data;
    } catch (error) {
        console.error("Error fetching book by ID:", error);
        throw error;
    }
};



