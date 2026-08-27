import { Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import PantryPage from "./pages/PantryPage";
import StatsPage from "./pages/StatsPage";
import ProtectedRoute from "./routes/ProtectedRoute";

/**
 * Maps URLs to pages. /login and /register are public - anyone can
 * reach them. "/" is wrapped in ProtectedRoute, so it redirects to
 * /login unless the user already has a valid token saved.
 */
function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <PantryPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/stats"
        element={
          <ProtectedRoute>
            <StatsPage />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default App;
