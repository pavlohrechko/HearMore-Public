import React, { useState } from "react";
import { uploadBook, Metadata } from "@/api_service/bookService";
import { Box, Button, Flex, Heading, Image, Input, Text, Textarea } from "@chakra-ui/react";

const AdminPage: React.FC = () => {
    const [pdfFile, setPdfFile] = useState<File | null>(null);
    const [imageFile, setImageFile] = useState<File | null>(null); // State for image
    const [metadata, setMetadata] = useState<Metadata>({
        title: "",
        author: "",
        genre: "",
        publication_date: "",
        description: "",
        voice: "alloy",
    });

    const speakers = [
        { value: "alloy", label: "Alloy" },
        { value: "joanna", label: "Joanna" },
        { value: "shimmer", label: "Shimmer" },
        { value: "onyx", label: "Onyx" },
        { value: "fable", label: "Fable" },
        { value: "nova", label: "Nova" }
    ];

    const handleVoiceChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setMetadata((prev) => ({
            ...prev,
            voice: e.target.value as Metadata['voice'],
        }));
    };

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files) {
            setPdfFile(event.target.files[0]);
        }
    };

    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files) {
            setImageFile(event.target.files[0]); // Set image file
        }
    };

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = event.target;
        setMetadata((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();

        if (!pdfFile || !imageFile) {
            alert("Please select both a PDF and an image file.");
            return;
        }

        try {
            await uploadBook(pdfFile, metadata, imageFile);
            alert("Book added successfully!");
        } catch (error) {
            console.error("Failed to add book:", error);
            alert("Failed to add book.");
        }
    };

    return (
        <Flex justify="center" align="center" height="100vh">
            <Box width="500px" borderRadius="lg" p="6" bg="#40445a" boxShadow="md">
                <Heading textAlign="center" mb="6" color="white">Add New Book</Heading>
                <form onSubmit={handleSubmit}>
                    <Box mb="4">
                        <Text color="white">Title:</Text>
                        <Input
                            value={metadata.title}
                            onChange={handleInputChange}
                            placeholder="Book Title"
                            type="text"
                            name="title"
                            size="sm"
                            bg="white"
                            color="black"
                        />
                    </Box>
                    <Box mb="4">
                        <Text color="white">Author:</Text>
                        <Input
                            value={metadata.author}
                            onChange={handleInputChange}
                            placeholder="Book Author"
                            type="text"
                            name="author"
                            size="sm"
                            bg="white"
                            color="black"
                        />
                    </Box>
                    <Box mb="4">
                        <Text color="white">Genre:</Text>
                        <Input
                            value={metadata.genre}
                            onChange={handleInputChange}
                            placeholder="Book Genre"
                            type="text"
                            name="genre"
                            size="sm"
                            bg="white"
                            color="black"
                        />
                    </Box>
                    <Box mb="4">
                        <Text color="white">Publication Date:</Text>
                        <Input
                            value={metadata.publication_date}
                            onChange={handleInputChange}
                            placeholder="Book Publication Date"
                            type="date"
                            name="publication_date"
                            size="sm"
                            bg="white"
                            color="black"
                        />
                    </Box>
                    <Box mb="4">
                        <Text color="white">Description:</Text>
                        <Textarea
                            value={metadata.description}
                            onChange={handleInputChange}
                            placeholder="Book Description"
                            name="description"
                            size="sm"
                            bg="white"
                            color="black"
                        />
                    </Box>
                    <Box mb="4">
                        <Text color="white">Voice:</Text>
                        <select
                            name="voice"
                            value={metadata.voice}
                            onChange={handleVoiceChange}
                            // bg="white"
                            color="black"
                        >
                            {speakers.map((speaker) => (
                                <option key={speaker.value} value={speaker.value}>
                                    {speaker.label}
                                </option>
                            ))}
                        </select>
                    </Box>
                    <Box mb="4">
                        <Text color="white">Upload PDF:</Text>
                        <Input type="file" accept="application/pdf" onChange={handleFileChange} size="sm" bg="white" />
                    </Box>
                    <Box mb="4">
                        <Text color="white">Upload Book Image:</Text>
                        <Input type="file" accept="image/*" onChange={handleImageChange} size="sm" bg="white" />
                        {imageFile && (
                            <Box mt="4" textAlign="center">
                                <Text color="white" mb="2">Preview Image:</Text>
                                <Image
                                    src={URL.createObjectURL(imageFile)}
                                    alt="Book Cover Preview"
                                    width="100px"
                                    height="150px"
                                    objectFit="cover"
                                />
                            </Box>
                        )}
                    </Box>
                    <Button type="submit" colorScheme="teal" width="full" mt="4">Add Book</Button>
                </form>
            </Box>
        </Flex>
    );
};

export default AdminPage;
