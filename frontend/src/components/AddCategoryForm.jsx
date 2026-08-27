import { useState } from "react";
import apiClient from "../api/client";

/**
 * A product always belongs to a category (Dairy, Fruits, ...). PantryPage
 * shows this form in two situations: forced, the very first time (no
 * categories exist yet at all), and afterwards on demand, behind a small
 * "+ Add category" toggle - so you're never stuck with only the starter
 * set of categories.
 */
export default function AddCategoryForm({ onCategoryAdded, introText }) {
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
    <div className="card">
      <p style={{ marginTop: 0 }}>
        {introText || 'Add a new category (e.g. "Snacks", "Frozen foods"):'}
      </p>
      <form onSubmit={handleSubmit} className="form-grid">
        <input
          className="input"
          style={{ maxWidth: 220 }}
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
          placeholder="Category name"
        />
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? "Adding..." : "Add category"}
        </button>
      </form>
      {error && <p className="form-error">{error}</p>}
    </div>
  );
}
