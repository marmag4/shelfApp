import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import FridgeIcon from "./icons/FridgeIcon";

/** Simple top navigation, shared by every logged-in page. */
export default function NavBar() {
  const { logout } = useAuth();
  const location = useLocation();

  return (
    <div className="navbar">
      <div className="navbar-brand">
        <h1 className="brand-title">
          <span className="brand-icon-badge">
            <FridgeIcon size={18} />
          </span>
          <span className="brand-name">
            Shelf<span className="brand-accent">App</span>
          </span>
        </h1>
        <div className="navbar-links">
          <Link
            to="/profile"
            className={`navbar-link ${location.pathname === "/profile" ? "active" : ""}`}
          >
            Profile
          </Link>
          <Link to="/" className={`navbar-link ${location.pathname === "/" ? "active" : ""}`}>
            Pantry
          </Link>
          <Link
            to="/stats"
            className={`navbar-link ${location.pathname === "/stats" ? "active" : ""}`}
          >
            Statistics
          </Link>
        </div>
      </div>
      <button className="btn btn-ghost" onClick={logout}>
        Log out
      </button>
    </div>
  );
}
