import { useEffect, useState } from "react";
import apiClient from "../api/client";
import AddCategoryForm from "../components/AddCategoryForm";
import AddProductForm from "../components/AddProductForm";
import ProductList from "../components/ProductList";
import NotificationsWidget from "../components/NotificationsWidget";
import TipWidget from "../components/TipWidget";
import AddSharingPointForm from "../components/AddSharingPointForm";
import NavBar from "../components/NavBar";

/** The main screen: your pantry, what's in it, and what to do with each item. */
export default function PantryPage() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [sharingPoints, setSharingPoints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  // Bumped every time a product changes status, so <NotificationsWidget>
  // (given this as its key) remounts and re-fetches - e.g. a product
  // that just got marked WASTED should drop off the "expiring soon" list.
  const [notificationsRefreshKey, setNotificationsRefreshKey] = useState(0);
  // Whether the "add a category" form is open - only relevant once at
  // least one category already exists (otherwise it's always shown,
  // forced, since you can't add a product with zero categories).
  const [showAddCategory, setShowAddCategory] = useState(false);

  // Loads both lists in parallel when the page first opens.
  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [productsRes, categoriesRes, sharingPointsRes] = await Promise.all([
        apiClient.get("/products"),
        apiClient.get("/categories"),
        apiClient.get("/sharing-points"),
      ]);
      setProducts(productsRes.data);
      setCategories(categoriesRes.data);
      setSharingPoints(sharingPointsRes.data);
    } catch (err) {
      setError("Could not load your pantry. Try refreshing the page.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleProductAdded = (newProduct) => {
    setProducts([...products, newProduct]);
    // A newly added product might already be expiring soon - refresh
    // the notifications widget too, not just the product list.
    setNotificationsRefreshKey((key) => key + 1);
  };

  const handleProductChanged = (updatedProduct) => {
    setProducts(products.map((p) => (p.id === updatedProduct.id ? updatedProduct : p)));
    setNotificationsRefreshKey((key) => key + 1);
  };

  const handleCategoryAdded = (newCategory) => {
    setCategories([...categories, newCategory]);
    setShowAddCategory(false);
  };

  const handleSharingPointAdded = (newSharingPoint) => {
    setSharingPoints([...sharingPoints, newSharingPoint]);
  };

  return (
    <div className="page">
      <NavBar />

      {error && <p className="form-error">{error}</p>}

      {loading ? (
        <p style={{ color: "var(--color-text-muted)" }}>Loading...</p>
      ) : (
        <>
          <NotificationsWidget key={notificationsRefreshKey} />
          <TipWidget />

          {categories.length === 0 ? (
            <AddCategoryForm onCategoryAdded={handleCategoryAdded} />
          ) : (
            <>
              <AddProductForm categories={categories} onProductAdded={handleProductAdded} />

              <div style={{ marginBottom: 20 }}>
                {!showAddCategory ? (
                  <button className="btn btn-ghost btn-sm" onClick={() => setShowAddCategory(true)}>
                    + Add category
                  </button>
                ) : (
                  <AddCategoryForm
                    onCategoryAdded={handleCategoryAdded}
                    introText='Add a new category (e.g. "Snacks", "Frozen foods"):'
                  />
                )}
              </div>

              {sharingPoints.length === 0 && (
                <AddSharingPointForm onSharingPointAdded={handleSharingPointAdded} />
              )}
              <ProductList
                products={products}
                onChanged={handleProductChanged}
                sharingPoints={sharingPoints}
                categories={categories}
              />
            </>
          )}
        </>
      )}
    </div>
  );
}
