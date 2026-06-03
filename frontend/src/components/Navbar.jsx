import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">📚 Library</Link>
      </div>
      <div className="navbar-links">
        <Link to="/books">Books</Link>
        {user && <Link to="/orders">My Orders</Link>}
        {user && <Link to="/borrowings">My Borrowings</Link>}
        {user && isAdmin() && <Link to="/admin">Admin</Link>}
        {user ? (
          <>
            <span className="navbar-user">Hi, {user.name}</span>
            <button className="btn btn-sm" onClick={handleLogout}>
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}
