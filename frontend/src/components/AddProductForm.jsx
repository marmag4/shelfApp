import { useState } from "react";
import apiClient from "../api/client";

/** The form for adding a new item to the pantry. */
export default function AddProductForm({ categories, onProductAdded }) {
  const [form, setForm] = useState({
    name: "",
    quantity: "",
    unit: "",
    expiryDate: "",
    categoryId: categories[0]?.id ?? "",
  });
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  // One handler for every field - it uses the input's "name" attribute
  // to know which piece of form state to update.
  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError(null);
    setSubmitting(true);

    try {
      const response = await apiClient.post("/products", {
        name: form.name,
        quantity: Number(form.quantity),
        unit: form.unit,
        expiryDate: form.expiryDate,
        categoryId: Number(form.categoryId),
      });
      onProductAdded(response.data);
      // Reset the form, but keep the same category selected - handy
      // when you're adding several products of the same kind in a row.
      setForm({ name: "", quantity: "", unit: "", expiryDate: "", categoryId: form.categoryId });
    } catch (err) {
      const fields = err.response?.data?.fields;
      setError(
        fields
          ? Object.values(fields).join(" ")
          : err.response?.data?.error || "Could not add product.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="card">
      <p className="card-title">Add a product</p>
      <div className="form-grid">
        <div>
          <label className="field-label">Name</label>
          <input className="input" name="name" value={form.name} onChange={handleChange} required />
        </div>
        <div style={{ width: 90 }}>
          <label className="field-label">Quantity</label>
          <input
            className="input"
            type="number"
            step="0.5"
            min="0"
            name="quantity"
            value={form.quantity}
            onChange={handleChange}
            required
          />
        </div>
        <div style={{ width: 100 }}>
          <label className="field-label">Unit</label>
          <input
            className="input"
            name="unit"
            value={form.unit}
            onChange={handleChange}
            placeholder="pcs, kg, l..."
            required
          />
        </div>
        <div>
          <label className="field-label">Expiry date</label>
          <input
            className="input"
            type="date"
            name="expiryDate"
            value={form.expiryDate}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <label className="field-label">Category</label>
          <select
            className="input"
            name="categoryId"
            value={form.categoryId}
            onChange={handleChange}
            required
          >
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </div>
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? "Adding..." : "Add product"}
        </button>
      </div>
      {error && <p className="form-error">{error}</p>}
    </form>
  );
}
