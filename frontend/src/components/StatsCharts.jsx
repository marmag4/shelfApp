/**
 * Simple, dependency-free charts for the Statistics page - a horizontal
 * bar chart, a donut chart, a 6-month trend chart, and a waste-by-reason
 * chart, all built with plain divs/SVG so we don't need to add a charting
 * library just for this.
 */

const STATUS_ROWS = [
  { key: "activeProducts", label: "Active", color: "var(--color-active)" },
  { key: "consumedProducts", label: "Consumed", color: "var(--color-consumed)" },
  { key: "donatedProducts", label: "Donated", color: "var(--color-donated)" },
  { key: "wastedProducts", label: "Wasted", color: "var(--color-wasted)" },
];

const REASON_STYLES = {
  EXPIRED: { label: "Expired", color: "var(--color-wasted)" },
  SPOILED: { label: "Spoiled", color: "var(--color-overdue)" },
  OVERBOUGHT: { label: "Overbought", color: "var(--color-warning)" },
  OTHER: { label: "Other", color: "var(--color-neutral)" },
};

export function StatusBarChart({ stats }) {
  const max = Math.max(1, ...STATUS_ROWS.map((r) => stats[r.key] ?? 0));

  return (
    <div className="card">
      <p className="card-title">Products by status</p>
      {STATUS_ROWS.map((row) => {
        const value = stats[row.key] ?? 0;
        return (
          <div className="chart-bar-row" key={row.key}>
            <span className="chart-bar-label">{row.label}</span>
            <div className="chart-bar-track">
              <div
                className="chart-bar-fill"
                style={{ width: `${(value / max) * 100}%`, background: row.color }}
              />
            </div>
            <span className="chart-bar-value" style={{ color: row.color }}>
              {value}
            </span>
          </div>
        );
      })}
    </div>
  );
}

export function StatusDonutChart({ stats }) {
  const total = STATUS_ROWS.reduce((sum, row) => sum + (stats[row.key] ?? 0), 0);
  const radius = 66;
  const circumference = 2 * Math.PI * radius;

  // Each slice is a dash on the same circle, offset by the sum of the
  // slices drawn before it - a common trick for donut charts in pure SVG.
  let offsetSoFar = 0;
  const slices = STATUS_ROWS.map((row) => {
    const value = stats[row.key] ?? 0;
    const fraction = total > 0 ? value / total : 0;
    const dash = fraction * circumference;
    const slice = { ...row, value, dash, offset: offsetSoFar };
    offsetSoFar += dash;
    return slice;
  });

  return (
    <div className="card">
      <p className="card-title">Status breakdown</p>
      {total === 0 ? (
        <p className="table-empty">No products yet - add some to see this chart.</p>
      ) : (
        <div className="donut-wrap">
          <svg width={176} height={176} viewBox="0 0 176 176">
            <circle cx={88} cy={88} r={radius} fill="none" stroke="var(--color-bg)" strokeWidth={20} />
            {slices.map(
              (slice) =>
                slice.value > 0 && (
                  <circle
                    key={slice.key}
                    cx={88}
                    cy={88}
                    r={radius}
                    fill="none"
                    stroke={slice.color}
                    strokeWidth={20}
                    strokeLinecap="butt"
                    strokeDasharray={`${slice.dash} ${circumference - slice.dash}`}
                    strokeDashoffset={-slice.offset}
                    transform="rotate(-90 88 88)"
                  />
                ),
            )}
            <text x={88} y={84} textAnchor="middle" fontSize={26} fontWeight={800} fill="var(--color-text)">
              {total}
            </text>
            <text x={88} y={102} textAnchor="middle" fontSize={11.5} fill="var(--color-text-muted)">
              products
            </text>
          </svg>
          <div className="chart-legend">
            {slices.map((slice) => (
              <div className="chart-legend-item" key={slice.key}>
                <span className="chart-legend-dot" style={{ background: slice.color }} />
                <span>{slice.label}</span>
                <strong className="chart-legend-value">
                  {slice.value} ({total > 0 ? Math.round((slice.value / total) * 100) : 0}%)
                </strong>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

/**
 * The "over time" chart the README promises - waste vs. donations per
 * month, for the last 6 months (always all 6, zero-filled by the backend,
 * so the x-axis stays evenly spaced even for a brand-new account).
 */
export function MonthlyTrendChart({ stats }) {
  const trend = stats.monthlyTrend ?? [];
  const max = Math.max(1, ...trend.flatMap((m) => [m.wastedCount, m.donatedCount]));
  const hasActivity = trend.some((m) => m.wastedCount > 0 || m.donatedCount > 0);

  return (
    <div className="card">
      <p className="card-title">Waste vs. donations - last 6 months</p>
      {!hasActivity ? (
        <p className="table-empty">No waste or donations logged in the last 6 months yet.</p>
      ) : (
        <>
          <div className="trend-chart">
            {trend.map((m) => (
              <div className="trend-month" key={m.month}>
                <div className="trend-bars">
                  <div className="trend-bar-col">
                    {m.wastedCount > 0 && <span className="trend-bar-value">{m.wastedCount}</span>}
                    <div
                      className="trend-bar"
                      style={{
                        // capped at 80% (not 100%) so the tallest bar always leaves
                        // headroom for its value label instead of colliding with it
                        height: `${(m.wastedCount / max) * 80}%`,
                        background: "var(--color-wasted)",
                      }}
                      title={`${m.wastedCount} wasted in ${m.month}`}
                    />
                  </div>
                  <div className="trend-bar-col">
                    {m.donatedCount > 0 && <span className="trend-bar-value">{m.donatedCount}</span>}
                    <div
                      className="trend-bar"
                      style={{
                        height: `${(m.donatedCount / max) * 80}%`,
                        background: "var(--color-donated)",
                      }}
                      title={`${m.donatedCount} donated in ${m.month}`}
                    />
                  </div>
                </div>
                <span className="trend-month-label">{m.month}</span>
              </div>
            ))}
          </div>
          <div className="chart-legend" style={{ flexDirection: "row", gap: 20, marginTop: 14 }}>
            <span className="chart-legend-item">
              <span className="chart-legend-dot" style={{ background: "var(--color-wasted)" }} />
              Wasted
            </span>
            <span className="chart-legend-item">
              <span className="chart-legend-dot" style={{ background: "var(--color-donated)" }} />
              Donated
            </span>
          </div>
        </>
      )}
    </div>
  );
}

/**
 * The "why" behind the waste rate - how many WasteLogs fall under each
 * reason, most common first (StatsService already sorts it that way, so
 * this just renders the list in the order it comes back).
 */
export function WasteReasonChart({ stats }) {
  const rows = stats.wasteByReason ?? [];
  const total = rows.reduce((sum, row) => sum + row.count, 0);
  const max = Math.max(1, ...rows.map((row) => row.count));

  return (
    <div className="card">
      <p className="card-title">Why food gets wasted</p>
      {total === 0 ? (
        <p className="table-empty">No waste logged yet - hopefully it stays that way.</p>
      ) : (
        rows.map((row) => {
          const style = REASON_STYLES[row.reason] ?? { label: row.reason, color: "var(--color-neutral)" };
          return (
            <div className="chart-bar-row" key={row.reason}>
              <span className="chart-bar-label">{style.label}</span>
              <div className="chart-bar-track">
                <div
                  className="chart-bar-fill"
                  style={{ width: `${(row.count / max) * 100}%`, background: style.color }}
                />
              </div>
              <span className="chart-bar-value" style={{ color: style.color }}>
                {row.count}
              </span>
            </div>
          );
        })
      )}
    </div>
  );
}
