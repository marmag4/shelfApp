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
    <div style={{ maxWidth: 360, margin: "80px auto", fontFamily: "sans-serif" }}>
      <h1>ShelfApp</h1>
      <h2>Login</h2>

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 12 }}>
          <label>Email</label>
          <br />
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            style={{ width: "100%", padding: 8 }}
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label>Password</label>
          <br />
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            style={{ width: "100%", padding: 8 }}
          />
        </div>

        {error && <p style={{ color: "red" }}>{error}</p>}

        <button type="submit" disabled={loading} style={{ width: "100%", padding: 10 }}>
          {loading ? "Logging in..." : "Log in"}
        </button>
      </form>

      <p style={{ marginTop: 16 }}>
        No account yet? <Link to="/register">Register</Link>
      </p>
    </div>
  );
}
