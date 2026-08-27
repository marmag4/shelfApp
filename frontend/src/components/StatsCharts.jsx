/**
 * Simple, dependency-free charts for the Statistics page - a horizontal
 * bar chart and a donut chart, both built with plain divs/SVG so we don't
 * need to add a charting library just for this.
 */

const STATUS_ROWS = [
  { key: "activeProducts", label: "Active", color: "var(--color-active)" },
  { key: "consumedProducts", label: "Consumed", color: "var(--color-consumed)" },
  { key: "donatedProducts", label: "Donated", color: "var(--color-donated)" },
  { key: "wastedProducts", label: "Wasted", color: "var(--color-wasted)" },
];

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
            <span className="chart-bar-value">{value}</span>
          </div>
        );
      })}
    </div>
  );
}

export function StatusDonutChart({ stats }) {
  const total = STATUS_ROWS.reduce((sum, row) => sum + (stats[row.key] ?? 0), 0);
  const radius = 60;
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
          <svg width={160} height={160} viewBox="0 0 160 160">
            <circle cx={80} cy={80} r={radius} fill="none" stroke="var(--color-bg)" strokeWidth={20} />
            {slices.map(
              (slice) =>
                slice.value > 0 && (
                  <circle
                    key={slice.key}
                    cx={80}
                    cy={80}
                    r={radius}
                    fill="none"
                    stroke={slice.color}
                    strokeWidth={20}
                    strokeDasharray={`${slice.dash} ${circumference - slice.dash}`}
                    strokeDashoffset={-slice.offset}
                    transform="rotate(-90 80 80)"
                  />
                ),
            )}
            <text x={80} y={85} textAnchor="middle" fontSize={22} fontWeight={800} fill="var(--color-text)">
              {total}
            </text>
          </svg>
          <div className="chart-legend">
            {slices.map((slice) => (
              <div className="chart-legend-item" key={slice.key}>
                <span className="chart-legend-dot" style={{ background: slice.color }} />
                {slice.label}: {slice.value} ({total > 0 ? Math.round((slice.value / total) * 100) : 0}%)
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
