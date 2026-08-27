import { useEffect, useState } from "react";
import apiClient from "../api/client";
import NavBar from "../components/NavBar";

/**
 * Feature #5 from the project idea: shows the user, over time, how their
 * own habits are trending. Pulls GET /api/stats, which the backend
 * computes from all of the user's products (counts by status, plus a
 * headline waste percentage).
 */
export default function StatsPage() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient
      .get("/stats")
      .then((response) => setStats(response.data))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div style={{ maxWidth: 900, margin: "40px auto", fontFamily: "sans-serif", padding: "0 16px" }}>
      <NavBar />

      <h2>Your statistics</h2>

      {loading && <p>Loading...</p>}

      {!loading && stats && (
        <>
          <div
            style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(140px, 1fr))",
              gap: 12,
              margin: "16px 0",
            }}
          >
            <StatTile label="Total products" value={stats.totalProducts} />
            <StatTile label="Active" value={stats.activeProducts} />
            <StatTile label="Consumed" value={stats.consumedProducts} />
            <StatTile label="Donated" value={stats.donatedProducts} />
            <StatTile label="Wasted" value={stats.wastedProducts} />
            <StatTile label="Total donations" value={stats.totalDonations} />
            <StatTile label="Total waste logs" value={stats.totalWasteLogs} />
          </div>

          <div style={{ border: "1px solid #ddd", borderRadius: 6, padding: 16 }}>
            <p style={{ margin: "0 0 8px" }}>
              <strong>Waste rate:</strong> {stats.wastePercentage}% of the products you've
              finished with ended up wasted (the rest were consumed or donated).
            </p>
            {/* A simple visual bar - a fuller chart can replace this in the
                visual design pass, once every screen exists. */}
            <div style={{ background: "#eee", borderRadius: 4, height: 20, overflow: "hidden" }}>
              <div
                style={{
                  width: `${Math.min(stats.wastePercentage, 100)}%`,
                  background: "#dc2626",
                  height: "100%",
                }}
              />
            </div>
          </div>
        </>
      )}
    </div>
  );
}

function StatTile({ label, value }) {
  return (
    <div style={{ border: "1px solid #ddd", borderRadius: 6, padding: 12, textAlign: "center" }}>
      <div style={{ fontSize: 24, fontWeight: "bold" }}>{value}</div>
      <div style={{ fontSize: 13, color: "#666" }}>{label}</div>
    </div>
  );
}
