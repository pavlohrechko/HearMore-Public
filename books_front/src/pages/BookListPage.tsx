import React, { useEffect, useState } from "react";
import { Box, SimpleGrid, Text, Image } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom"; // Import useNavigate
import { Book } from "@/types";
import { bookService } from "@/api_service/bookService";

const BookListPage: React.FC = () => {
    const [books, setBooks] = useState<Book[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate(); // Initialize navigate

    useEffect(() => {
        const getBooks = async () => {
            try {
                setLoading(true);
                const fetchedBooks = await bookService();
                setBooks(fetchedBooks);
            } catch {
                setError("Failed to fetch books.");
            } finally {
                setLoading(false);
            }
        };

        getBooks();
    }, []);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <Box padding="4">
            <Text fontSize="2xl" fontWeight="bold" mb="4">
                Books
            </Text>

            {/* Display all books in a grid layout */}
            <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }}>
                {books.length === 0 ? (
                    <Text>No books available.</Text>
                ) : (
                    books.map((book) => (
                        <Box
                            key={book.id}
                            borderWidth="1px"
                            borderRadius="md"
                            boxShadow="md"
                            p="4"
                            cursor="pointer"
                            onClick={() => navigate(`/books/${book.id}`)} // Navigate to the book page on click
                            _hover={{ transform: "scale(1.05)", transition: "transform 0.3s ease" }}
                        >
                            {/* Book Image */}
                            <Image
                                src={`http://localhost:8080/${book.imageFilePath}`} // Fallback image if no image path
                                alt={book.title}
                                boxSize="150px"
                                objectFit="cover"
                                mx="auto"
                            />

                            {/* Book Title */}
                            <Text fontWeight="bold" mt="3" textAlign="center">
                                {book.title}
                            </Text>

                            {/* Book Author */}
                            <Text fontSize="sm" textAlign="center" color="gray.500">
                                {book.author}
                            </Text>

                            {/* Book Description */}
                            {book.description && (
                                <Text mt="2" fontSize="sm" textAlign="center" color="gray.600">
                                    {book.description}
                                </Text>
                            )}

                            {/* Publication Date */}
                            {book.publicationDate && (
                                <Text mt="2" fontSize="sm" textAlign="center" color="gray.500">
                                    Published on: {book.publicationDate}
                                </Text>
                            )}

                            {/* Audio Player */}
                            {book.audioPreviewFilePath && (
                                <Box mt="3" textAlign="center">
                                    <Text>Listen to the audiobook preview:</Text>
                                    <Box margin="6">
                                        <audio controls style={{ width: "100%", maxWidth: "300px", margin: "0 auto", display: "block" }}>
                                            <source src={`http://localhost:8080/${book.audioPreviewFilePath}`} type="audio/mpeg" />
                                            Your browser does not support the audio element.
                                        </audio>
                                    </Box>
                                </Box>
                            )}
                        </Box>
                    ))
                )}
            </SimpleGrid>
        </Box>
    );
};

export default BookListPage;
