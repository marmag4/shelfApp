/**
 * The ShelfApp brand icon - a simple line-art fridge, used instead of an
 * emoji so it renders identically everywhere (emoji fonts vary a lot
 * across OSes/browsers). Inherits its color via `currentColor`, so it
 * picks up whatever text color it's placed in.
 */
export default function FridgeIcon({ size = 20, className }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      className={className}
      aria-hidden="true"
    >
      <rect x="5" y="2" width="14" height="20" rx="2.5" stroke="currentColor" strokeWidth="1.8" />
      <line x1="5" y1="8.7" x2="19" y2="8.7" stroke="currentColor" strokeWidth="1.8" />
      <line x1="15.2" y1="4.3" x2="15.2" y2="6.5" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
      <line x1="15.2" y1="11.3" x2="15.2" y2="15.3" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
    </svg>
  );
}
