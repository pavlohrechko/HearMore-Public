import React, { useState, useEffect } from 'react';
import { Box, Text, Image, SimpleGrid } from '@chakra-ui/react';
import { getSpeakers, Speaker } from "@/api_service/speakerService";

const SpeakerDemoPage: React.FC = () => {
    const [speakers, setSpeakers] = useState<Speaker[]>([]);
    const [selectedSpeaker, setSelectedSpeaker] = useState<Speaker | null>(null); // Correct destructuring

    // Fetch speakers when the component mounts
    useEffect(() => {
        const fetchSpeakers = async () => {
            try {
                const data = await getSpeakers();
                setSpeakers(data);
            } catch (error) {
                console.error('Error fetching speakers:', error);
            }
        };

        fetchSpeakers();
    }, []); // Empty dependency array to fetch only once on mount

    const handleSpeakerSelect = (speaker: Speaker) => {
        setSelectedSpeaker(speaker);
    };

    return (
        <Box padding="4">
            {/* Selected Speaker Display */}
            {selectedSpeaker && (
                <Box borderWidth="1px" borderRadius="md" boxShadow="md" p="4" mt="6">
                    <Text fontSize="lg" fontWeight="bold">Selected Speaker</Text>
                    <Image
                        src={`http://localhost:8080${selectedSpeaker.imageFilePath}`}
                        alt={selectedSpeaker.name}
                        boxSize="150px"
                        objectFit="cover"
                        mx="auto"
                        mt="4"
                    />
                    <Text textAlign="center" fontWeight="bold" mt="2">
                        {selectedSpeaker.name}
                    </Text>
                    <Text fontSize="sm" textAlign="center" mt="2" color="gray.500">
                        {selectedSpeaker.description}
                    </Text>
                </Box>
            )}

            {/* Display all speakers in a grid layout */}
            <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }}>
                {speakers.map((speaker) => (
                    <Box
                        key={speaker.id}
                        borderWidth="1px"
                        borderRadius="md"
                        boxShadow="md"
                        p="4"
                        cursor="pointer"
                        _hover={{ transform: 'scale(1.05)', transition: 'transform 0.3s ease' }}
                        onClick={() => handleSpeakerSelect(speaker)}
                    >
                        <Image
                            src={`http://localhost:8080${speaker.imageFilePath}`}
                            alt={speaker.name}
                            boxSize="150px"
                            objectFit="cover"
                            mx="auto"
                        />
                        <Text textAlign="center" fontWeight="bold" mt="2">
                            {speaker.name}
                        </Text>
                        <Text fontSize="sm" textAlign="center" mt="2" color="gray.500">
                            {speaker.description}
                        </Text>
                        <Box mt="3" textAlign="center">
                            <audio
                                controls
                                style={{ width: '100%', maxWidth: '400px', margin: '0 auto', display: 'block' }}
                            >
                                <source src={`http://localhost:8080${speaker.audioFilePath}`} type="audio/mpeg" />
                                Your browser does not support the audio element.
                            </audio>
                        </Box>
                    </Box>
                ))}
            </SimpleGrid>
        </Box>
    );
};

export default SpeakerDemoPage;
