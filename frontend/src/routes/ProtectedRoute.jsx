import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/**
 * Wraps a page that should only be visible to a logged-in user.
 *
 * This is the frontend half of "Authentication/Authorization on both
 * ends" that the assignment requires: the backend already rejects
 * requests without a valid JWT, and this component makes sure the
 * user never even sees the page (and never fires a doomed request)
 * if they aren't logged in - it sends them to /login instead.
 */
export default function ProtectedRoute({ children }) {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
}
