import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    // Stops the browser from doing a full page reload on submit - we
    // want to handle the login ourselves, with JavaScript.
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      await login(email, password);
      // Login succeeded (the token is now saved) - send the user to
      // the main pantry page.
      navigate("/");
    } catch (err) {
      // Our GlobalExceptionHandler on the backend sends back a clean
      // { error: "..." } body for wrong credentials - we show that
      // message directly if it's there.
      const message = err.response?.data?.error || "Something went wrong. Please try again.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-auth">
      <div className="card">
        <h1 style={{ marginBottom: 4 }}>🥬 ShelfApp</h1>
        <p style={{ color: "var(--color-text-muted)", marginTop: 0, marginBottom: 20 }}>
          Log in to your pantry
        </p>

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: 14 }}>
            <label className="field-label">Email</label>
            <input
              type="email"
              className="input"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div style={{ marginBottom: 14 }}>
            <label className="field-label">Password</label>
            <input
              type="password"
              className="input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {error && <p className="form-error">{error}</p>}

          <button type="submit" className="btn btn-primary" disabled={loading} style={{ width: "100%", justifyContent: "center", marginTop: 6 }}>
            {loading ? "Logging in..." : "Log in"}
          </button>
        </form>

        <p style={{ marginTop: 18, marginBottom: 0, fontSize: 14 }}>
          No account yet? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}
