import { useEffect, useState } from "react";
import apiClient from "../api/client";

const URGENCY_STYLES = {
  OVERDUE: { background: "#fee2e2", color: "#991b1b" },
  URGENT: { background: "#ffedd5", color: "#9a3412" },
  WARNING: { background: "#fef9c3", color: "#854d0e" },
};

/**
 * Feature #1 from the project idea: warns you before food goes bad.
 * Pulls GET /api/notifications, which the backend already sorts by how
 * soon each product expires (OVERDUE first).
 */
export default function NotificationsWidget() {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient
      .get("/notifications")
      .then((response) => setNotifications(response.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return null;
  if (notifications.length === 0) return null;

  return (
    <div style={{ margin: "16px 0", padding: 16, border: "1px solid #ddd", borderRadius: 6 }}>
      <h3 style={{ marginTop: 0 }}>Expiring soon</h3>
      {notifications.map((n) => {
        const style = URGENCY_STYLES[n.urgency] || {};
        return (
          <div
            key={n.productId}
            style={{
              ...style,
              padding: "8px 12px",
              borderRadius: 4,
              marginBottom: 6,
              display: "flex",
              justifyContent: "space-between",
            }}
          >
            <span>
              <strong>{n.productName}</strong> — expires {n.expiryDate}
            </span>
            <span>
              {n.daysUntilExpiry < 0
                ? `${Math.abs(n.daysUntilExpiry)} day(s) overdue`
                : n.daysUntilExpiry === 0
                  ? "expires today"
                  : `in ${n.daysUntilExpiry} day(s)`}
            </span>
          </div>
        );
      })}
    </div>
  );
}
