import { createContext, useContext, useState, useEffect } from 'react';
import { login as apiLogin, getUsers } from '../api/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      const stored = localStorage.getItem('user');
      if (stored) {
        setUser(JSON.parse(stored));
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const res = await apiLogin(email, password);
    const jwt = res.data.token;
    localStorage.setItem('token', jwt);

    const payload = JSON.parse(atob(jwt.split('.')[1]));
    const userId = payload.sub;

    const usersRes = await getUsers();
    const found = usersRes.data.find((u) => u.email === userId);

    if (found) {
      localStorage.setItem('user', JSON.stringify(found));
      setUser(found);
    }

    return found;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  const isAdmin = () => {
    if (!user || !user.userTypes) return false;
    return user.userTypes.some(
      (t) => t.type === 'ADMIN' || t.type === 'admin'
    );
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout, isAdmin }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
