import { useAuth } from "../context/AuthContext";

/**
 * Placeholder home page for now - just proves that login works and that
 * this page is only reachable when logged in (thanks to ProtectedRoute).
 * We'll replace this with the real pantry screen (list of products,
 * add/consume/waste/donate) in the next step.
 */
export default function PantryPage() {
  const { logout } = useAuth();

  return (
    <div style={{ maxWidth: 480, margin: "80px auto", fontFamily: "sans-serif" }}>
      <h1>ShelfApp</h1>
      <p>You're logged in! This is where your pantry will live.</p>
      <button onClick={logout} style={{ padding: 10 }}>
        Log out
      </button>
    </div>
  );
}
