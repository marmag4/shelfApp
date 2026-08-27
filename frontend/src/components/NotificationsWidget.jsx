import { useEffect, useState } from "react";
import apiClient from "../api/client";

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
    <div className="card">
      <p className="card-title">⏰ Expiring soon</p>
      {notifications.map((n) => (
        <div key={n.productId} className={`notification-row notification-${n.urgency}`}>
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
      ))}
    </div>
  );
}
