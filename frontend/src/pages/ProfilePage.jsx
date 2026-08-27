import { useEffect, useState } from "react";
import apiClient from "../api/client";
import NavBar from "../components/NavBar";

/** Shows the logged-in user's own account details - GET /api/users/me. */
export default function ProfilePage() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    apiClient
      .get("/users/me")
      .then((response) => setUser(response.data))
      .catch(() => setError("Could not load your profile."))
      .finally(() => setLoading(false));
  }, []);

  const address = user
    ? [user.street, user.streetNumber, user.city, user.postalCode].filter(Boolean).join(", ")
    : "";

  return (
    <div className="page">
      <NavBar />

      <h2 style={{ marginBottom: 16 }}>Your profile</h2>

      {loading && <p style={{ color: "var(--color-text-muted)" }}>Loading...</p>}
      {error && <p className="form-error">{error}</p>}

      {!loading && user && (
        <div className="card" style={{ maxWidth: 480 }}>
          <ProfileRow label="Name" value={`${user.firstName} ${user.lastName}`} />
          <ProfileRow label="Email" value={user.email} />
          <ProfileRow label="Birth date" value={user.birthDate || "—"} />
          <ProfileRow label="Address" value={address || "—"} last />
        </div>
      )}
    </div>
  );
}

function ProfileRow({ label, value, last }) {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        gap: 16,
        padding: "10px 0",
        borderBottom: last ? "none" : "1px solid var(--color-border)",
      }}
    >
      <span style={{ color: "var(--color-text-muted)", fontSize: 14 }}>{label}</span>
      <span style={{ fontWeight: 600, fontSize: 14 }}>{value}</span>
    </div>
  );
}
