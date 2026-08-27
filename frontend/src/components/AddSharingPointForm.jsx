import { useState } from "react";
import apiClient from "../api/client";

/**
 * Feature #4 from the project idea: somewhere to donate a product instead
 * of throwing it away. A sharing point (food bank, charity, etc.) has to
 * exist before you can donate to one - this form only shows up when the
 * list is empty, normally just the first time you use donations.
 */
export default function AddSharingPointForm({ onSharingPointAdded }) {
  const [form, setForm] = useState({ name: "", city: "", phone: "" });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const response = await apiClient.post("/sharing-points", form);
      onSharingPointAdded(response.data);
      setForm({ name: "", city: "", phone: "" });
    } catch (err) {
      const fields = err.response?.data?.fields;
      setError(
        fields
          ? Object.values(fields).join(" ")
          : err.response?.data?.error || "Could not add sharing point.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ margin: "16px 0", padding: 16, border: "1px solid #ddd", borderRadius: 6 }}>
      <p>
        No donation points yet — add one to get started (e.g. a local food bank or charity):
      </p>
      <form onSubmit={handleSubmit} style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
        <input
          name="name"
          value={form.name}
          onChange={handleChange}
          required
          placeholder="Name"
        />
        <input
          name="city"
          value={form.city}
          onChange={handleChange}
          required
          placeholder="City"
        />
        <input
          name="phone"
          value={form.phone}
          onChange={handleChange}
          placeholder="Phone (optional)"
        />
        <button type="submit" disabled={submitting}>
          {submitting ? "Adding..." : "Add sharing point"}
        </button>
      </form>
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}
