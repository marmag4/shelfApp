import { useEffect, useState } from "react";
import apiClient from "../api/client";

/**
 * Feature #3 from the project idea: a small motivating message, so using
 * up your food (instead of wasting it) feels a bit more rewarding.
 * Pulls GET /api/tips/random - a fresh random tip from the app's curated
 * set every time "New tip" is pressed.
 */
export default function TipWidget() {
  const [tip, setTip] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchTip = () => {
    setLoading(true);
    apiClient
      .get("/tips/random")
      .then((response) => setTip(response.data))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchTip();
  }, []);

  return (
    <div
      style={{
        margin: "16px 0",
        padding: 16,
        border: "1px solid #ddd",
        borderRadius: 6,
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        gap: 12,
      }}
    >
      {loading || !tip ? (
        <span>Loading a tip...</span>
      ) : (
        <span>
          {/* General tips (no specific product category) come back with category: null - just show the message then. */}
          {tip.category && <strong>{tip.category}: </strong>}
          {tip.message}
        </span>
      )}
      <button onClick={fetchTip} disabled={loading}>
        New tip
      </button>
    </div>
  );
}
