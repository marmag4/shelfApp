import { useState } from "react";
import apiClient from "../api/client";

const STATUS_COLORS = {
  ACTIVE: "#2563eb",
  CONSUMED: "#16a34a",
  WASTED: "#dc2626",
  DONATED: "#9333ea",
};

const WASTE_REASONS = ["EXPIRED", "SPOILED", "OVERBOUGHT", "OTHER"];

/** One row per product, with the "Consumed" / "Wasted" actions for active items. */
function ProductRow({ product, onChanged }) {
  const [showWasteForm, setShowWasteForm] = useState(false);
  const [reason, setReason] = useState(WASTE_REASONS[0]);
  const [busy, setBusy] = useState(false);

  const markConsumed = async () => {
    setBusy(true);
    try {
      const response = await apiClient.patch(`/products/${product.id}/status`, {
        status: "CONSUMED",
      });
      onChanged(response.data);
    } finally {
      setBusy(false);
    }
  };

  const confirmWaste = async () => {
    setBusy(true);
    try {
      await apiClient.post("/waste-logs", { productId: product.id, reason });
      // The backend already flips the product's status to WASTED as
      // part of recording the waste log - we mirror that locally
      // instead of doing a second request just to re-read it.
      onChanged({ ...product, status: "WASTED" });
      setShowWasteForm(false);
    } finally {
      setBusy(false);
    }
  };

  return (
    <tr style={{ borderBottom: "1px solid #eee" }}>
      <td style={{ padding: "8px 4px" }}>{product.name}</td>
      <td style={{ padding: "8px 4px" }}>
        {product.quantity} {product.unit}
      </td>
      <td style={{ padding: "8px 4px" }}>{product.categoryName}</td>
      <td style={{ padding: "8px 4px" }}>{product.expiryDate}</td>
      <td style={{ padding: "8px 4px", color: STATUS_COLORS[product.status], fontWeight: "bold" }}>
        {product.status}
      </td>
      <td style={{ padding: "8px 4px" }}>
        {product.status === "ACTIVE" && !showWasteForm && (
          <>
            <button onClick={markConsumed} disabled={busy}>
              Consumed
            </button>{" "}
            <button onClick={() => setShowWasteForm(true)} disabled={busy}>
              Wasted
            </button>
          </>
        )}
        {showWasteForm && (
          <>
            <select value={reason} onChange={(e) => setReason(e.target.value)}>
              {WASTE_REASONS.map((r) => (
                <option key={r} value={r}>
                  {r}
                </option>
              ))}
            </select>{" "}
            <button onClick={confirmWaste} disabled={busy}>
              Confirm
            </button>{" "}
            <button onClick={() => setShowWasteForm(false)} disabled={busy}>
              Cancel
            </button>
          </>
        )}
      </td>
    </tr>
  );
}

export default function ProductList({ products, onChanged }) {
  if (products.length === 0) {
    return <p>No products yet — add your first one above.</p>;
  }

  return (
    <table style={{ width: "100%", borderCollapse: "collapse" }}>
      <thead>
        <tr style={{ textAlign: "left", borderBottom: "2px solid #ccc" }}>
          <th style={{ padding: "8px 4px" }}>Name</th>
          <th style={{ padding: "8px 4px" }}>Quantity</th>
          <th style={{ padding: "8px 4px" }}>Category</th>
          <th style={{ padding: "8px 4px" }}>Expires</th>
          <th style={{ padding: "8px 4px" }}>Status</th>
          <th style={{ padding: "8px 4px" }}>Actions</th>
        </tr>
      </thead>
      <tbody>
        {products.map((p) => (
          <ProductRow key={p.id} product={p} onChanged={onChanged} />
        ))}
      </tbody>
    </table>
  );
}
