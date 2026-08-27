import { useState } from "react";
import apiClient from "../api/client";

/**
 * A product always belongs to a category (Dairy, Fruits, ...), so at
 * least one category has to exist before the "add product" form makes
 * sense. This tiny form only shows up when the list is empty - normally
 * you'd only see it once, the very first time you use the app.
 */
export default function AddCategoryForm({ onCategoryAdded }) {
  const [name, setName] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const response = await apiClient.post("/categories", { name });
      onCategoryAdded(response.data);
      setName("");
    } catch (err) {
      setError(err.response?.data?.error || "Could not add category.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ margin: "24px 0", padding: 16, border: "1px solid #ddd", borderRadius: 6 }}>
      <p>
        You don't have any categories yet. Add one to get started (e.g. "Dairy", "Fruits",
        "Vegetables"):
      </p>
      <form onSubmit={handleSubmit} style={{ display: "flex", gap: 8 }}>
        <input
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
          placeholder="Category name"
        />
        <button type="submit" disabled={submitting}>
          {submitting ? "Adding..." : "Add category"}
        </button>
      </form>
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}
