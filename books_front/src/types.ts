// src/types.ts
export interface Book {
    data: Book | PromiseLike<Book>;
    id: number;
    title: string;
    author: string;
    genre?: string;
    image?: string;
    description?: string;
    publicationDate?: string;
    audioFilePath?: string;
    imageFilePath?: string;
    audioPreviewFilePath?: string;
}

export interface User {
    username: string;
    email: string;
    imagePath?: string;
    subscription: string;
}
