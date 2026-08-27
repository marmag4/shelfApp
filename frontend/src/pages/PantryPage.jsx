import { useEffect, useState } from "react";
import apiClient from "../api/client";
import { useAuth } from "../context/AuthContext";
import AddCategoryForm from "../components/AddCategoryForm";
import AddProductForm from "../components/AddProductForm";
import ProductList from "../components/ProductList";

/** The main screen: your pantry, what's in it, and what to do with each item. */
export default function PantryPage() {
  const { logout } = useAuth();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Loads both lists in parallel when the page first opens.
  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [productsRes, categoriesRes] = await Promise.all([
        apiClient.get("/products"),
        apiClient.get("/categories"),
      ]);
      setProducts(productsRes.data);
      setCategories(categoriesRes.data);
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
  };

  const handleProductChanged = (updatedProduct) => {
    setProducts(products.map((p) => (p.id === updatedProduct.id ? updatedProduct : p)));
  };

  const handleCategoryAdded = (newCategory) => {
    setCategories([...categories, newCategory]);
  };

  if (loading) {
    return <p style={{ margin: 40, fontFamily: "sans-serif" }}>Loading...</p>;
  }

  return (
    <div style={{ maxWidth: 900, margin: "40px auto", fontFamily: "sans-serif", padding: "0 16px" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h1>ShelfApp — Your Pantry</h1>
        <button onClick={logout}>Log out</button>
      </div>

      {error && <p style={{ color: "red" }}>{error}</p>}

      {categories.length === 0 ? (
        <AddCategoryForm onCategoryAdded={handleCategoryAdded} />
      ) : (
        <>
          <AddProductForm categories={categories} onProductAdded={handleProductAdded} />
          <ProductList products={products} onChanged={handleProductChanged} />
        </>
      )}
    </div>
  );
}
