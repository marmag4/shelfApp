import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import apiClient from "../api/client";

export default function RegisterPage() {
  const [form, setForm] = useState({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
  });
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  // One handler for every field, instead of writing a separate
  // onChange function per input - it uses the input's "name" attribute
  // to know which piece of the form state to update.
  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // POST /api/users is the registration endpoint - it's public
      // (no token needed yet, see SecurityConfig on the backend).
      await apiClient.post("/users", form);
      // Registration worked - send the new user to the login page to
      // sign in with their new account.
      navigate("/login");
    } catch (err) {
      // If validation fails (e.g. password too short), the backend's
      // GlobalExceptionHandler sends back a "fields" object with one
      // message per invalid field - we join them into one readable line.
      const fields = err.response?.data?.fields;
      if (fields) {
        setError(Object.values(fields).join(" "));
      } else {
        setError(err.response?.data?.error || "Something went wrong. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-auth">
      <div className="card">
        <h1 style={{ marginBottom: 4 }}>🥬 ShelfApp</h1>
        <p style={{ color: "var(--color-text-muted)", marginTop: 0, marginBottom: 20 }}>
          Create an account
        </p>

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: 14 }}>
            <label className="field-label">First name</label>
            <input
              name="firstName"
              className="input"
              value={form.firstName}
              onChange={handleChange}
              required
            />
          </div>

          <div style={{ marginBottom: 14 }}>
            <label className="field-label">Last name</label>
            <input
              name="lastName"
              className="input"
              value={form.lastName}
              onChange={handleChange}
              required
            />
          </div>

          <div style={{ marginBottom: 14 }}>
            <label className="field-label">Email</label>
            <input
              type="email"
              name="email"
              className="input"
              value={form.email}
              onChange={handleChange}
              required
            />
          </div>

          <div style={{ marginBottom: 14 }}>
            <label className="field-label">Password</label>
            <input
              type="password"
              name="password"
              className="input"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>

          {error && <p className="form-error">{error}</p>}

          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
            style={{ width: "100%", justifyContent: "center", marginTop: 6 }}
          >
            {loading ? "Creating account..." : "Register"}
          </button>
        </form>

        <p style={{ marginTop: 18, marginBottom: 0, fontSize: 14 }}>
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </div>
    </div>
  );
}
