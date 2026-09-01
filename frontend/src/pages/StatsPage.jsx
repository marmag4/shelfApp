import { useEffect, useState } from "react";
import apiClient from "../api/client";
import NavBar from "../components/NavBar";
import { StatusBarChart, StatusDonutChart, MonthlyTrendChart } from "../components/StatsCharts";

/**
 * Feature #5 from the project idea: shows the user, over time, how their
 * own habits are trending. Pulls GET /api/stats, which the backend
 * computes from all of the user's products (counts by status, plus a
 * headline waste percentage).
 *
 * Has two inner tabs: "Numbers" (a headline waste-rate card, then tiles
 * grouped into "status breakdown" vs. "lifetime totals") and "Charts"
 * (the same data, visualized) - both read from the same `stats` object,
 * so switching tabs doesn't refetch anything.
 */
export default function StatsPage() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [view, setView] = useState("numbers");

  useEffect(() => {
    apiClient
      .get("/stats")
      .then((response) => setStats(response.data))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="page">
      <NavBar />

      <h2>Your statistics</h2>
      <p className="page-subtitle">How your habits are trending over time.</p>

      {loading && <p style={{ color: "var(--color-text-muted)" }}>Loading...</p>}

      {!loading && stats && (
        <>
          <div className="tabs">
            <button
              className={`tab ${view === "numbers" ? "active" : ""}`}
              onClick={() => setView("numbers")}
            >
              Numbers
            </button>
            <button
              className={`tab ${view === "charts" ? "active" : ""}`}
              onClick={() => setView("charts")}
            >
              Charts
            </button>
          </div>

          {view === "numbers" && (
            <>
              <WasteRateHero percentage={stats.wastePercentage} />

              <p className="section-label">Status breakdown</p>
              <div className="stat-grid">
                <StatTile label="Active" value={stats.activeProducts} tone="active" />
                <StatTile label="Consumed" value={stats.consumedProducts} tone="consumed" />
                <StatTile label="Donated" value={stats.donatedProducts} tone="donated" />
                <StatTile label="Wasted" value={stats.wastedProducts} tone="wasted" />
              </div>

              <p className="section-label" style={{ marginTop: 24 }}>
                Lifetime totals
              </p>
              <div className="stat-grid">
                <StatTile label="Total products" value={stats.totalProducts} />
                <StatTile label="Total donations" value={stats.totalDonations} />
                <StatTile label="Total waste logs" value={stats.totalWasteLogs} />
              </div>
            </>
          )}

          {view === "charts" && (
            <div className="charts-grid">
              <div className="chart-span-full">
                <MonthlyTrendChart stats={stats} />
              </div>
              <StatusBarChart stats={stats} />
              <StatusDonutChart stats={stats} />
            </div>
          )}
        </>
      )}
    </div>
  );
}

/**
 * The headline number from StatsService - of everything you've finished
 * with, what share ended up wasted. Color/tone shifts with how you're
 * doing (green when low, amber, then red) so it reads at a glance instead
 * of needing to parse the percentage.
 */
function WasteRateHero({ percentage }) {
  const tone = percentage <= 15 ? "good" : percentage <= 35 ? "okay" : "bad";

  return (
    <div className={`card waste-hero waste-hero-${tone}`}>
      <p className="card-title">Waste rate</p>
      <div className="waste-hero-body">
        <div className="waste-hero-number">{percentage}%</div>
        <div className="waste-hero-details">
          <p className="waste-hero-copy">
            of the products you've finished with ended up wasted - the rest were consumed or
            donated.
          </p>
          <div className="progress-track">
            <div className="progress-fill" style={{ width: `${Math.min(percentage, 100)}%` }} />
          </div>
        </div>
      </div>
    </div>
  );
}

function StatTile({ label, value, tone }) {
  return (
    <div className={`stat-tile ${tone ? `stat-tile-${tone}` : ""}`}>
      <div className="stat-tile-value">{value}</div>
      <div className="stat-tile-label">{label}</div>
    </div>
  );
}
