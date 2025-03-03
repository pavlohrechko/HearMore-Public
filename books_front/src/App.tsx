import React, { useState } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import NavBar from "@/pages/NavBar";
import BookListPage from "@/pages/BookListPage";
import AdminPage from "@/pages/AdminPage";
import SpeakerDemoPage from "@/pages/SpeakerDemo";
import LoginRegisterPage from "@/pages/LoginRegisterPage";
import imagePath from "@/assets/logo.png";
import BookPage from "@/pages/BookPage";
import ProfilePage from "@/pages/ProfilePage";

const App: React.FC = () => {
    const [username, setUsername] = useState<string | null>(null);

    const navItems = [
        { name: "Home", path: "/" },
        { name: "Admin", path: "/admin" },
        { name: "Speakers", path: "/speakers" },
    ];

    return (
        <Router>
            <div>
                <NavBar
                    imageSrcPath={imagePath}
                    navItems={navItems}
                    username={username}
                    setUsername={setUsername}
                />
                <Routes>
                    <Route path="/" element={<BookListPage />} />
                    <Route path="/admin" element={<AdminPage />} />
                    <Route path="/speakers" element={<SpeakerDemoPage />} />
                    <Route path="/login" element={<LoginRegisterPage setUsername={setUsername} />} />
                    <Route path="/books/:id" element={<BookPage />} />
                    <Route path="/profile" element={<ProfilePage />} />
                </Routes>
            </div>
        </Router>
    );
};

export default App;
