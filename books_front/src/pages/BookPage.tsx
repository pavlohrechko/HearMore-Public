import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Box, Heading, HStack, VStack, Text, Stack } from "@chakra-ui/react";
import {
    Skeleton,
    SkeletonCircle,
    SkeletonText,
} from "@/components/ui/skeleton"
import { Book } from "@/types";
import { getBookById, getBookByIdWithSubscription, validateJwt } from "@/api_service/bookService";

const BookPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const [book, setBook] = useState<Book | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const [subscription, setSubscription] = useState<string | null>(null);

    useEffect(() => {
        const fetchBook = async () => {
            if (!id) {
                setError("Invalid book ID");
                setLoading(false);
                return;
            }

            try {
                setLoading(true);

                const jwt = localStorage.getItem("jwt") || "";
                const token = localStorage.getItem("token") || "";

                if (jwt && token) {
                    const isValid = await validateJwt(jwt, token);
                    if (isValid) {
                        const fetchedBook = await getBookByIdWithSubscription(
                            Number(id),
                            jwt,
                            token
                        );
                        setSubscription("basic");
                        setBook(fetchedBook);
                        return;
                    }
                }

                // Fallback to fetching book without subscription
                const fetchedBook = await getBookById(Number(id));
                setBook(fetchedBook);
                setSubscription(null);
            } catch {
                setError("Error fetching book details.");
            } finally {
                setLoading(false);
            }
        };

        fetchBook();
    }, [id]);

    if (loading) {
        return (
            <Box
                maxW="70%"
                maxH={["90%", "80%"]}
                borderWidth="5px"
                borderRadius="15px"
                margin="0 auto"
                marginTop="100px"
                padding="6"
                boxShadow="lg"
            >
                <HStack gap="6">
                    <HStack width="full">
                        <Skeleton maxWidth="300px" />
                        <SkeletonText noOfLines={2} spacing="4" flex="1" />
                    </HStack>
                    <Skeleton height="200px" />
                    <SkeletonText noOfLines={4} spacing="4" />
                </HStack>
            </Box>
        );
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    if (!book) {
        return <div>No book found.</div>;
    }

    const baseUrl = "http://localhost:8080/";

    return (
        <Box
            maxW="70%"
            maxH={["90%", "80%"]}
            borderWidth="5px"
            borderRadius="15px"
            margin="0 auto"
            marginTop="100px"
        >
            <HStack>
                <img
                    src={`${baseUrl}${book.imageFilePath}`}
                    alt={book.title}
                    style={{ maxWidth: "300px" }}
                />
                <VStack>
                    <Heading>{book.title}</Heading>
                    <Heading>By {book.author}</Heading>
                    <Text>
                        <strong>Genre:</strong> {book.genre || "Not specified"}
                    </Text>
                    <Text>
                        <strong>Published:</strong> {book.publicationDate || "Not specified"}
                    </Text>
                    <Text>
                        <strong>Description:</strong>{" "}
                        {book.description || "No description available."}
                    </Text>
                    {book.audioFilePath && (
                        <audio controls>
                            <source src={`${baseUrl}${book.audioFilePath}`} type="audio/mpeg" />
                            Your browser does not support the audio element.
                        </audio>
                    )}
                    {subscription ? (
                        <Text>You have access to this book with your {subscription} subscription.</Text>
                    ) : (
                        <Text>Subscribe to access this book.</Text>
                    )}
                </VStack>
            </HStack>
        </Box>
    );
};

export default BookPage;
