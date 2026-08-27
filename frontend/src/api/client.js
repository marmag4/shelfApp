import axios from "axios";

/**
 * A single, shared way for the whole app to talk to the Spring Boot
 * backend. Every page imports this instead of calling axios directly,
 * so the base URL and the "attach my login token" behavior only have
 * to be set up once, here.
 */
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api",
});

/**
 * This runs right before every single request leaves the browser.
 * If we have a saved JWT (because the user already logged in), we
 * attach it as an "Authorization: Bearer <token>" header - exactly
 * what the backend's JwtAuthFilter expects to see.
 *
 * Without this, the very first request after logging in - e.g. "get
 * my products" - would be rejected as unauthenticated, because the
 * backend has no memory of who logged in (JWT is stateless).
 */
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default apiClient;
