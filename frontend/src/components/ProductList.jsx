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
  const [showRecipes, setShowRecipes] = useState(false);
  const [recipes, setRecipes] = useState(null);
  const [recipesLoading, setRecipesLoading] = useState(false);

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

  // Feature #2: recipe ideas for this product's category. Fetched once,
  // the first time you open the panel - toggling it closed and back
  // open again just re-shows what we already have.
  const toggleRecipes = async () => {
    const willShow = !showRecipes;
    setShowRecipes(willShow);
    if (willShow && recipes === null) {
      setRecipesLoading(true);
      try {
        const response = await apiClient.get(`/products/${product.id}/recipes`);
        setRecipes(response.data);
      } finally {
        setRecipesLoading(false);
      }
    }
  };

  return (
    <>
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
        )}{" "}
        <button onClick={toggleRecipes}>{showRecipes ? "Hide recipes" : "Recipes"}</button>
      </td>
    </tr>
    {showRecipes && (
      <tr>
        <td colSpan={6} style={{ padding: "8px 4px 16px", background: "#fafafa" }}>
          {recipesLoading && <p>Loading recipes...</p>}
          {!recipesLoading && recipes && recipes.length === 0 && (
            <p>No recipe suggestions for this category yet.</p>
          )}
          {!recipesLoading &&
            recipes &&
            recipes.map((recipe) => (
              <div key={recipe.title} style={{ marginBottom: 12 }}>
                <strong>{recipe.title}</strong> — {recipe.description}
                <br />
                <em>Ingredients:</em> {recipe.ingredients.join(", ")}
                <br />
                <em>Instructions:</em> {recipe.instructions}
              </div>
            ))}
        </td>
      </tr>
    )}
    </>
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
