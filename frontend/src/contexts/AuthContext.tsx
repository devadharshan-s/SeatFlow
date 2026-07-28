import React, { createContext, useContext, useState, ReactNode } from 'react';

interface AuthContextType {
  isAuthenticated: boolean;
  user: any; // Replace with actual user type
  token: string | null;
  login: (token: string, user: any) => void;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const initialToken = localStorage.getItem('jwt_token');
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!initialToken);
  const [user, setUser] = useState<any>(null); // You might want to store/fetch user data from token
  const [token, setToken] = useState<string | null>(initialToken);

  const login = (newToken: string, newUser: any) => {
    setToken(newToken);
    setUser(newUser);
    setIsAuthenticated(true);
    // Store token in localStorage or sessionStorage
    localStorage.setItem('jwt_token', newToken);
    // For simplicity, user data can also be stored or fetched after login
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    setIsAuthenticated(false);
    localStorage.removeItem('jwt_token');
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
