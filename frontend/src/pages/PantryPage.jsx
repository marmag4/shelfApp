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
  };

  const handleSharingPointAdded = (newSharingPoint) => {
    setSharingPoints([...sharingPoints, newSharingPoint]);
  };

  if (loading) {
    return <p style={{ margin: 40, fontFamily: "sans-serif" }}>Loading...</p>;
  }

  return (
    <div style={{ maxWidth: 900, margin: "40px auto", fontFamily: "sans-serif", padding: "0 16px" }}>
      <NavBar />

      {error && <p style={{ color: "red" }}>{error}</p>}

      <NotificationsWidget key={notificationsRefreshKey} />
      <TipWidget />

      {categories.length === 0 ? (
        <AddCategoryForm onCategoryAdded={handleCategoryAdded} />
      ) : (
        <>
          <AddProductForm categories={categories} onProductAdded={handleProductAdded} />
          {sharingPoints.length === 0 && (
            <AddSharingPointForm onSharingPointAdded={handleSharingPointAdded} />
          )}
          <ProductList
            products={products}
            onChanged={handleProductChanged}
            sharingPoints={sharingPoints}
          />
        </>
      )}
    </div>
  );
}
