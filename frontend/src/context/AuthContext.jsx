import { createContext, useContext, useEffect, useState } from "react";

import api from "../services/api";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [token, setToken] = useState(
    localStorage.getItem("token")
  );

  const [sessionExpiresAt, setSessionExpiresAt] = useState(null);

  const getTokenExpiry = (jwt) => {
    try {
      const payload = JSON.parse(atob(jwt.split(".")[1]));
      return payload.exp * 1000;
    } catch (error) {
      console.error("Invalid JWT:", error);
      return null;
    }
  };

  const login = async (email, password) => {
    const response = await api.post("/users/login", {
      email,
      password,
    });

    const jwt = response.data;

    localStorage.setItem("token", jwt);
    setToken(jwt);

    const expiry = getTokenExpiry(jwt);
    setSessionExpiresAt(expiry);

    return jwt;
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setSessionExpiresAt(null);
  };

  useEffect(() => {
    if (!token) {
      setSessionExpiresAt(null);
      return;
    }

    const expiry = getTokenExpiry(token);
    setSessionExpiresAt(expiry);
  }, [token]);

  useEffect(() => {
    if (!sessionExpiresAt) {
      return;
    }

    const checkExpiry = () => {
      if (Date.now() >= sessionExpiresAt) {
        logout();
      }
    };

    checkExpiry();

    const interval = setInterval(checkExpiry, 1000);

    return () => clearInterval(interval);
  }, [sessionExpiresAt]);

useEffect(() => {
  const handleAuthExpired = () => {
    setToken(null);
    setSessionExpiresAt(null);
  };

  window.addEventListener("auth-expired", handleAuthExpired);

  return () => {
    window.removeEventListener(
      "auth-expired",
      handleAuthExpired
    );
  };
}, []);

  return (
    <AuthContext.Provider
      value={{
        token,
        isAuthenticated: !!token,
        sessionExpiresAt,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}