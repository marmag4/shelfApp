import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/** Simple top navigation, shared by every logged-in page. */
export default function NavBar() {
  const { logout } = useAuth();

  return (
    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
      <div>
        <h1 style={{ display: "inline", marginRight: 24 }}>ShelfApp</h1>
        <Link to="/" style={{ marginRight: 12 }}>
          Pantry
        </Link>
        <Link to="/stats">Statistics</Link>
      </div>
      <button onClick={logout}>Log out</button>
    </div>
  );
}
