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
    <div className="page">
      <NavBar />

      <h2 style={{ marginBottom: 16 }}>Your statistics</h2>

      {loading && <p style={{ color: "var(--color-text-muted)" }}>Loading...</p>}

      {!loading && stats && (
        <>
          <div className="stat-grid">
            <StatTile label="Total products" value={stats.totalProducts} />
            <StatTile label="Active" value={stats.activeProducts} />
            <StatTile label="Consumed" value={stats.consumedProducts} />
            <StatTile label="Donated" value={stats.donatedProducts} />
            <StatTile label="Wasted" value={stats.wastedProducts} />
            <StatTile label="Total donations" value={stats.totalDonations} />
            <StatTile label="Total waste logs" value={stats.totalWasteLogs} />
          </div>

          <div className="card">
            <p style={{ margin: "0 0 4px" }}>
              <strong>Waste rate:</strong> {stats.wastePercentage}% of the products you've
              finished with ended up wasted (the rest were consumed or donated).
            </p>
            {/* A simple visual bar - a fuller chart library can replace this later. */}
            <div className="progress-track">
              <div className="progress-fill" style={{ width: `${Math.min(stats.wastePercentage, 100)}%` }} />
            </div>
          </div>
        </>
      )}
    </div>
  );
}

function StatTile({ label, value }) {
  return (
    <div className="stat-tile">
      <div className="stat-tile-value">{value}</div>
      <div className="stat-tile-label">{label}</div>
    </div>
  );
}
