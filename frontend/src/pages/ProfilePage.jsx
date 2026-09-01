import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../api/client";
import NavBar from "../components/NavBar";
import { useAuth } from "../context/AuthContext";

/**
 * Shows the logged-in user's own account details (GET /api/users/me),
 * lets them edit those details (PUT /api/users/me), and lets them
 * permanently delete their account (DELETE /api/users/me).
 */
export default function ProfilePage() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [editing, setEditing] = useState(false);
  const [editForm, setEditForm] = useState(null);
  const [editError, setEditError] = useState(null);
  const [saving, setSaving] = useState(false);

  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState(null);

  const { logout } = useAuth();
  const navigate = useNavigate();

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

  const startEditing = () => {
    setEditForm({
      firstName: user.firstName,
      lastName: user.lastName,
      birthDate: user.birthDate || "",
      city: user.city || "",
      street: user.street || "",
      streetNumber: user.streetNumber || "",
      postalCode: user.postalCode || "",
    });
    setEditError(null);
    setEditing(true);
  };

  const handleEditChange = (event) => {
    setEditForm({ ...editForm, [event.target.name]: event.target.value });
  };

  const submitEdit = async (event) => {
    event.preventDefault();
    setEditError(null);
    setSaving(true);
    try {
      const response = await apiClient.put("/users/me", {
        ...editForm,
        birthDate: editForm.birthDate || null,
        city: editForm.city || null,
        street: editForm.street || null,
        streetNumber: editForm.streetNumber || null,
        postalCode: editForm.postalCode || null,
      });
      setUser(response.data);
      setEditing(false);
    } catch (err) {
      const fields = err.response?.data?.fields;
      setEditError(
        fields ? Object.values(fields).join(" ") : err.response?.data?.error || "Could not save changes.",
      );
    } finally {
      setSaving(false);
    }
  };

  const confirmDelete = async () => {
    setDeleteError(null);
    setDeleting(true);
    try {
      await apiClient.delete("/users/me");
      // The account (and everything in it) is gone - there's nothing left
      // to be logged into, so clear the token and send them to /login.
      logout();
      navigate("/login");
    } catch (err) {
      setDeleteError(err.response?.data?.error || "Could not delete your account.");
      setDeleting(false);
    }
  };

  return (
    <div className="page">
      <NavBar />

      <h2>Your profile</h2>
      <p className="page-subtitle">Manage your account details.</p>

      {loading && <p style={{ color: "var(--color-text-muted)" }}>Loading...</p>}
      {error && <p className="form-error">{error}</p>}

      {!loading && user && !editing && (
        <div className="card" style={{ maxWidth: 480 }}>
          <ProfileRow label="Name" value={`${user.firstName} ${user.lastName}`} />
          <ProfileRow label="Email" value={user.email} />
          <ProfileRow label="Birth date" value={user.birthDate || "—"} />
          <ProfileRow label="Address" value={address || "—"} last />
          <div style={{ marginTop: 16 }}>
            <button className="btn btn-primary btn-sm" onClick={startEditing}>
              ✏️ Edit profile
            </button>
          </div>
        </div>
      )}

      {!loading && user && editing && editForm && (
        <form onSubmit={submitEdit} className="card" style={{ maxWidth: 480 }}>
          <p className="card-title">Edit profile</p>

          <div style={{ marginBottom: 14 }}>
            <label className="field-label">First name</label>
            <input
              className="input"
              name="firstName"
              value={editForm.firstName}
              onChange={handleEditChange}
              required
            />
          </div>
          <div style={{ marginBottom: 14 }}>
            <label className="field-label">Last name</label>
            <input
              className="input"
              name="lastName"
              value={editForm.lastName}
              onChange={handleEditChange}
              required
            />
          </div>
          <div style={{ marginBottom: 14 }}>
            <label className="field-label">Birth date</label>
            <input
              className="input"
              type="date"
              name="birthDate"
              value={editForm.birthDate}
              onChange={handleEditChange}
            />
          </div>
          <div style={{ marginBottom: 14 }}>
            <label className="field-label">City</label>
            <input className="input" name="city" value={editForm.city} onChange={handleEditChange} />
          </div>
          <div style={{ marginBottom: 14 }}>
            <label className="field-label">Street</label>
            <input className="input" name="street" value={editForm.street} onChange={handleEditChange} />
          </div>
          <div style={{ marginBottom: 14 }}>
            <label className="field-label">Street number</label>
            <input
              className="input"
              name="streetNumber"
              value={editForm.streetNumber}
              onChange={handleEditChange}
            />
          </div>

          <div style={{ marginBottom: 14 }}>
            <label className="field-label">Postal code</label>
            <input
              className="input"
              name="postalCode"
              value={editForm.postalCode}
              onChange={handleEditChange}
            />
          </div>

          {editError && <p className="form-error">{editError}</p>}

          <div style={{ display: "flex", gap: 8 }}>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? "Saving..." : "Save changes"}
            </button>
            <button
              type="button"
              className="btn btn-ghost"
              onClick={() => setEditing(false)}
              disabled={saving}
            >
              Cancel
            </button>
          </div>
        </form>
      )}

      {!loading && user && (
        <div className="card" style={{ maxWidth: 480, borderColor: "#f3c6a8" }}>
          <p className="card-title" style={{ color: "var(--color-wasted)" }}>
            Delete account
          </p>
          {!showDeleteConfirm ? (
            <>
              <p style={{ color: "var(--color-text-muted)", fontSize: 13.5, margin: "0 0 12px" }}>
                Permanently deletes your account and everything in it - all your products, waste
                logs, and donations. This cannot be undone.
              </p>
              <button className="btn btn-danger btn-sm" onClick={() => setShowDeleteConfirm(true)}>
                Delete account
              </button>
            </>
          ) : (
            <>
              <p style={{ fontSize: 13.5, margin: "0 0 12px" }}>
                Are you sure? This will permanently delete your account and all of your data.
              </p>
              {deleteError && <p className="form-error">{deleteError}</p>}
              <div style={{ display: "flex", gap: 8 }}>
                <button className="btn btn-danger btn-sm" onClick={confirmDelete} disabled={deleting}>
                  {deleting ? "Deleting..." : "Yes, delete my account"}
                </button>
                <button
                  className="btn btn-ghost btn-sm"
                  onClick={() => setShowDeleteConfirm(false)}
                  disabled={deleting}
                >
                  Cancel
                </button>
              </div>
            </>
          )}
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
