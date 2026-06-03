import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function AdminRoute({ children }) {
  const { user, isAdmin, loading } = useAuth();

  if (loading) return <div className="loading">Loading...</div>;
  if (!user || !isAdmin()) return <Navigate to="/" />;

  return children;
}
