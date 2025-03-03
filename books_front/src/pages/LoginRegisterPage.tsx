import React, { useState } from "react";
import {
    Box,
    Button,
    Input,
    Stack,
    Text,
    Heading,
} from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import {login, register} from "@/api_service/ authService";

interface LoginRegisterPageProps {
    setUsername: (username: string | null) => void;
}

const LoginRegisterPage: React.FC<LoginRegisterPageProps> = ({ setUsername }) => {
    const [localUsername, setLocalUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoginMode, setIsLoginMode] = useState(true);
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const handleSubmit = async () => {
        try {
            if (isLoginMode) {
                const response = await login({ username: localUsername, password });
                localStorage.setItem("jwt", response.jwt);
                localStorage.setItem("token", response.token);

                // Update global username
                setUsername(response.username);
                navigate("/");
            } else {
                const response = await register({ username: localUsername, email, password });
                localStorage.setItem("jwt", response.jwt);
                localStorage.setItem("token", response.token);

                // Update global username
                setUsername(response.username);
                navigate("/");
            }
        } catch {
            setMessage("Something went wrong!");
        }
    };

    return (
        <Box maxW="400px" mx="auto" mt="50px" p="20px" boxShadow="md" borderRadius="md">
            <Heading size="lg" textAlign="center" mb={6}>
                {isLoginMode ? "Login" : "Register"}
            </Heading>
            <Stack>
                {!isLoginMode && (
                    <Box>
                        <Text>Email</Text>
                        <Input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Enter your email"
                        />
                    </Box>
                )}
                <Box>
                    <Text>Username</Text>
                    <Input
                        type="text"
                        value={localUsername}
                        onChange={(e) => setLocalUsername(e.target.value)}
                        placeholder="Enter your username"
                    />
                </Box>
                <Box>
                    <Text>Password</Text>
                    <Input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter your password"
                    />
                </Box>
                <Button colorScheme="blue" onClick={handleSubmit}>
                    {isLoginMode ? "Login" : "Register"}
                </Button>

                {message && (
                    <Text color="red.500" textAlign="center" mt={4}>
                        {message}
                    </Text>
                )}

                <Text textAlign="center">
                    {isLoginMode ? "Don't have an account?" : "Already have an account?"}{" "}
                    <Button
                        colorScheme="blue"
                        onClick={() => setIsLoginMode(!isLoginMode)}
                    >
                        {isLoginMode ? "Register here" : "Login here"}
                    </Button>
                </Text>
            </Stack>
        </Box>
    );
};

export default LoginRegisterPage;
