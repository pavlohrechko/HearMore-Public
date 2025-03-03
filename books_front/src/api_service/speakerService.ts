// services/speakerService.ts
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/speakers'; // Your API endpoint

export interface Speaker {
    id: number;
    name: string;
    description: string;
    audioFilePath: string;
    imageFilePath: string;
}

export const getSpeakers = async (): Promise<Speaker[]> => {
    try {
        const response = await axios.get<Speaker[]>(API_URL);
        return response.data;
    } catch (error) {
        console.error('Error fetching speakers:', error);
        throw error;
    }
};
