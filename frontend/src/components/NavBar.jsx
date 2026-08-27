import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/** Simple top navigation, shared by every logged-in page. */
export default function NavBar() {
  const { logout } = useAuth();
  const location = useLocation();

  return (
    <div className="navbar">
      <div className="navbar-brand">
        <h1>🥬 ShelfApp</h1>
        <div className="navbar-links">
          <Link to="/" className={`navbar-link ${location.pathname === "/" ? "active" : ""}`}>
            Pantry
          </Link>
          <Link
            to="/stats"
            className={`navbar-link ${location.pathname === "/stats" ? "active" : ""}`}
          >
            Statistics
          </Link>
          <Link
            to="/profile"
            className={`navbar-link ${location.pathname === "/profile" ? "active" : ""}`}
          >
            Profile
          </Link>
        </div>
      </div>
      <button className="btn btn-ghost" onClick={logout}>
        Log out
      </button>
    </div>
  );
}
