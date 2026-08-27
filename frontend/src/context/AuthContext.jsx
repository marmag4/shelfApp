import { createContext, useContext, useState } from "react";
import apiClient from "../api/client";

/**
 * Keeps track, in one place, of "is someone logged in right now, and
 * with which token". Any page or component in the app can ask this
 * context via useAuth() instead of each one independently reading
 * localStorage and managing its own copy of the login state.
 */
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // Read any token already saved from a previous visit, so refreshing
  // the page doesn't log the user out.
  const [token, setToken] = useState(() => localStorage.getItem("token"));

  /** Calls POST /api/auth/login and, if it succeeds, saves the token. */
  const login = async (email, password) => {
    const response = await apiClient.post("/auth/login", { email, password });
    const newToken = response.data.token;
    localStorage.setItem("token", newToken);
    setToken(newToken);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
  };

  const value = {
    token,
    isAuthenticated: Boolean(token),
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/** Small convenience hook so pages just call useAuth() instead of useContext(AuthContext). */
export function useAuth() {
  return useContext(AuthContext);
}
