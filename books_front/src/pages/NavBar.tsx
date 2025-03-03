import React, { useState } from "react";
import {
    Box,
    Flex,
    Image,
    Button,
    Text,
    VStack,
} from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";

interface NavBarProps {
    imageSrcPath: string;
    navItems: { name: string; path: string }[];
    username: string | null;
    setUsername: (username: string | null) => void;
}

function NavBar({ imageSrcPath, navItems, username, setUsername }: NavBarProps) {
    const [selectedIndex, setSelectedIndex] = useState<number>(-1);
    const [isLogoutPopupOpen, setLogoutPopupOpen] = useState(false);
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("jwt");
        localStorage.removeItem("token");
        setUsername(null);
        setLogoutPopupOpen(false);
        navigate("/login");
    };

    const handleNavigateLogin = () => {
        navigate("/login");
    };

    const handleOpenLogoutPopup = () => {
        setLogoutPopupOpen(true);
    };

    const handleCloseLogoutPopup = () => {
        setLogoutPopupOpen(false);
    };

    const handleNavigateProfile = () => {
        navigate("/profile"); // Navigate to the profile page
    };

    return (
        <Box bg="blackAlpha.50" boxShadow="md">
            <Flex
                align="center"
                justify="space-between"
                padding="1rem"
                wrap="wrap"
                direction={{ base: "column", md: "row" }}
            >
                <Flex align="center">
                    <Image src={imageSrcPath} width="200px" mr="2" />
                </Flex>

                <Flex align="center" justify="space-between" gap="4" wrap="wrap">
                    {navItems.map((item, index) => (
                        <Button
                            key={index}
                            fontWeight={selectedIndex === index ? "bold" : "normal"}
                            colorScheme={selectedIndex === index ? "teal" : "gray"}
                            onClick={() => {
                                setSelectedIndex(index);
                                navigate(item.path); // Use navigate for routing
                            }}
                        >
                            {item.name}
                        </Button>
                    ))}
                </Flex>

                <Flex position="relative">
                    {username ? (
                        <>
                            <Text
                                fontSize="md"
                                fontWeight="bold"
                                color="teal.500"
                                onClick={handleNavigateProfile} // Navigate to profile page
                                cursor="pointer"
                            >
                                Welcome, {username}
                            </Text>
                            {isLogoutPopupOpen && (
                                <Box
                                    position="absolute"
                                    top="30px"
                                    right="0"
                                    bg="white"
                                    p="4"
                                    boxShadow="lg"
                                    borderRadius="md"
                                    zIndex="10"
                                >
                                    <Text mb="2">Are you sure you want to log out?</Text>
                                    <VStack>
                                        <Button size="sm" onClick={handleCloseLogoutPopup}>
                                            Cancel
                                        </Button>
                                        <Button
                                            size="sm"
                                            colorScheme="red"
                                            onClick={handleLogout}
                                        >
                                            Logout
                                        </Button>
                                    </VStack>
                                </Box>
                            )}
                        </>
                    ) : (
                        <Button size="md" colorScheme="teal" onClick={handleNavigateLogin}>
                            Login/Register
                        </Button>
                    )}
                </Flex>
            </Flex>
        </Box>
    );
}

export default NavBar;
