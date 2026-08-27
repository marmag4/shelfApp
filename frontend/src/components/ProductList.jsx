import { useState } from "react";
import apiClient from "../api/client";

const WASTE_REASONS = ["EXPIRED", "SPOILED", "OVERBOUGHT", "OTHER"];

/** One row per product, with the "Consumed" / "Wasted" / "Donate" actions for active items. */
function ProductRow({ product, onChanged, sharingPoints }) {
  const [showWasteForm, setShowWasteForm] = useState(false);
  const [reason, setReason] = useState(WASTE_REASONS[0]);
  const [busy, setBusy] = useState(false);
  const [showRecipes, setShowRecipes] = useState(false);
  const [recipes, setRecipes] = useState(null);
  const [recipesLoading, setRecipesLoading] = useState(false);
  const [showDonateForm, setShowDonateForm] = useState(false);
  const [sharingPointId, setSharingPointId] = useState(sharingPoints[0]?.id ?? "");

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

  // Feature #4: donate the product to a sharing point instead of wasting it.
  const confirmDonate = async () => {
    setBusy(true);
    try {
      await apiClient.post("/donations", { productId: product.id, sharingPointId });
      // Same idea as waste: the backend already flips the product's
      // status to DONATED, so we mirror that locally.
      onChanged({ ...product, status: "DONATED" });
      setShowDonateForm(false);
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
      <tr>
        <td>{product.name}</td>
        <td>
          {product.quantity} {product.unit}
        </td>
        <td>{product.categoryName}</td>
        <td>{product.expiryDate}</td>
        <td>
          <span className={`badge badge-${product.status.toLowerCase()}`}>{product.status}</span>
        </td>
        <td>
          <div style={{ display: "flex", gap: 6, flexWrap: "wrap", alignItems: "center" }}>
            {product.status === "ACTIVE" && !showWasteForm && !showDonateForm && (
              <>
                <button className="btn btn-sm btn-primary" onClick={markConsumed} disabled={busy}>
                  Consumed
                </button>
                <button className="btn btn-sm btn-danger" onClick={() => setShowWasteForm(true)} disabled={busy}>
                  Wasted
                </button>
                {sharingPoints.length > 0 && (
                  <button className="btn btn-sm" onClick={() => setShowDonateForm(true)} disabled={busy}>
                    Donate
                  </button>
                )}
              </>
            )}
            {showWasteForm && (
              <>
                <select className="input" style={{ width: 130 }} value={reason} onChange={(e) => setReason(e.target.value)}>
                  {WASTE_REASONS.map((r) => (
                    <option key={r} value={r}>
                      {r}
                    </option>
                  ))}
                </select>
                <button className="btn btn-sm btn-danger" onClick={confirmWaste} disabled={busy}>
                  Confirm
                </button>
                <button className="btn btn-sm btn-ghost" onClick={() => setShowWasteForm(false)} disabled={busy}>
                  Cancel
                </button>
              </>
            )}
            {showDonateForm && (
              <>
                <select
                  className="input"
                  style={{ width: 180 }}
                  value={sharingPointId}
                  onChange={(e) => setSharingPointId(e.target.value)}
                >
                  {sharingPoints.map((sp) => (
                    <option key={sp.id} value={sp.id}>
                      {sp.name} ({sp.city})
                    </option>
                  ))}
                </select>
                <button className="btn btn-sm btn-primary" onClick={confirmDonate} disabled={busy}>
                  Confirm
                </button>
                <button className="btn btn-sm btn-ghost" onClick={() => setShowDonateForm(false)} disabled={busy}>
                  Cancel
                </button>
              </>
            )}
            <button className="btn btn-sm btn-ghost" onClick={toggleRecipes}>
              {showRecipes ? "Hide recipes" : "🍳 Recipes"}
            </button>
          </div>
        </td>
      </tr>
      {showRecipes && (
        <tr>
          <td colSpan={6} style={{ padding: "0 10px 16px", border: "none" }}>
            <div className="recipe-panel">
              {recipesLoading && <p style={{ margin: 0 }}>Loading recipes...</p>}
              {!recipesLoading && recipes && recipes.length === 0 && (
                <p style={{ margin: 0 }}>No recipe suggestions for this category yet.</p>
              )}
              {!recipesLoading &&
                recipes &&
                recipes.map((recipe) => (
                  <div key={recipe.title} className="recipe-item">
                    <strong>{recipe.title}</strong> — {recipe.description}
                    <br />
                    <em>Ingredients:</em> {recipe.ingredients.join(", ")}
                    <br />
                    <em>Instructions:</em> {recipe.instructions}
                  </div>
                ))}
            </div>
          </td>
        </tr>
      )}
    </>
  );
}

export default function ProductList({ products, onChanged, sharingPoints }) {
  return (
    <div className="card">
      <p className="card-title">Your pantry</p>
      {products.length === 0 ? (
        <p className="table-empty">No products yet — add your first one above.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Quantity</th>
              <th>Category</th>
              <th>Expires</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <ProductRow key={p.id} product={p} onChanged={onChanged} sharingPoints={sharingPoints} />
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
