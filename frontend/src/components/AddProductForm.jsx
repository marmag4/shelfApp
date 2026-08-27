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
    <form
      onSubmit={handleSubmit}
      style={{
        display: "flex",
        gap: 12,
        flexWrap: "wrap",
        alignItems: "flex-end",
        margin: "16px 0 24px",
        padding: 16,
        border: "1px solid #ddd",
        borderRadius: 6,
      }}
    >
      <div>
        <label>Name</label>
        <br />
        <input name="name" value={form.name} onChange={handleChange} required />
      </div>
      <div>
        <label>Quantity</label>
        <br />
        <input
          type="number"
          step="0.01"
          min="0.01"
          name="quantity"
          value={form.quantity}
          onChange={handleChange}
          required
          style={{ width: 80 }}
        />
      </div>
      <div>
        <label>Unit</label>
        <br />
        <input
          name="unit"
          value={form.unit}
          onChange={handleChange}
          placeholder="pcs, kg, l..."
          required
          style={{ width: 90 }}
        />
      </div>
      <div>
        <label>Expiry date</label>
        <br />
        <input
          type="date"
          name="expiryDate"
          value={form.expiryDate}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <label>Category</label>
        <br />
        <select name="categoryId" value={form.categoryId} onChange={handleChange} required>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>
              {c.name}
            </option>
          ))}
        </select>
      </div>
      <button type="submit" disabled={submitting}>
        {submitting ? "Adding..." : "Add product"}
      </button>
      {error && <p style={{ color: "red", width: "100%", margin: 0 }}>{error}</p>}
    </form>
  );
}
