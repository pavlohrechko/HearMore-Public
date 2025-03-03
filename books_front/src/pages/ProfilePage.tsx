import React, { useEffect, useState } from "react";
import { getUserProfile } from "@/api_service/profileService";
import { User } from "@/types";

const ProfilePage: React.FC = () => {
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        const fetchUser = async () => {
            try {
                setLoading(true);
                const response = await getUserProfile();
                // Extract `data` from response if user data is nested
                setUser(response.data);
            } catch (err) {
                setError("Error fetching user profile");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchUser();
    }, []); // Dependency array ensures the effect runs only once

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    if (!user) {
        return <div>No user data available.</div>;
    }

    return (
        <div>
            <h1>Profile Page</h1>
            <p><strong>Username:</strong> {user.username}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <p>
                <strong>Subscription:</strong>{" "}
                {user.subscription === "ultimate"
                    ? "Ultimate"
                    : user.subscription === "basic"
                        ? "Basic"
                        : "None"}
            </p>
            {user.imagePath && (
                <img
                    src={`http://localhost:8080${user.imagePath}`}
                    alt="User profile"
                    style={{maxWidth: "150px", borderRadius: "50%"}}
                />
            )}
        </div>
    );
};

export default ProfilePage;
