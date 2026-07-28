// src/components/layout/Navbar.tsx
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { theme } from '../../styles/theme';
import { useAuth } from '../../contexts/AuthContext';

const Navbar: React.FC = () => {

    const { isAuthenticated, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <Nav>
            <NavInner>
                <Logo to="/">
                    <LogoAccent>Seat</LogoAccent>Flow
                </Logo>

                <NavLinks>
                    <NavLink to="/movies">Movies</NavLink>
                    <NavLink to="/shows">Shows</NavLink>
                    {isAuthenticated ? (
                        <LogoutButton onClick={handleLogout}>Logout</LogoutButton>
                    ) : (
                        <>
                            <NavLink to="/login">Login</NavLink>
                            <CTALink to="/register">Register</CTALink>
                        </>
                    )}
                </NavLinks>
            </NavInner>
        </Nav>
    );

};

const Nav = styled.nav`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: ${theme.colors.surface};
  border-bottom: 1px solid ${theme.colors.surfaceBorder};
  height: 60px;
`;

const NavInner = styled.div`
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 ${theme.spacing.xl};
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

const Logo = styled(Link)`
  font-size: ${theme.font.size.xl};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.textPrimary} !important;
  letter-spacing: -0.5px;
`;

const LogoAccent = styled.span`
  color: ${theme.colors.accent};
`;

const NavLinks = styled.div`
  display: flex;
  align-items: center;
  gap: ${theme.spacing.xl};
`;

const NavLink = styled(Link)`
  font-size: ${theme.font.size.sm};
  font-weight: ${theme.font.weight.medium};
  color: ${theme.colors.textSecondary} !important;
  transition: color ${theme.transition.fast};

  &:hover {
    color: ${theme.colors.textPrimary} !important;
  }
`;

const CTALink = styled(Link)`
  font-size: ${theme.font.size.sm};
  font-weight: ${theme.font.weight.semibold};
  color: ${theme.colors.textPrimary} !important;
  background: ${theme.colors.accent};
  padding: 6px 16px;
  border-radius: ${theme.radius.button};
  transition: background ${theme.transition.fast}, box-shadow ${theme.transition.fast};

  &:hover {
    background: ${theme.colors.accentHover};
    box-shadow: ${theme.shadow.button};
  }
`;

const LogoutButton = styled.button`
  font-size: ${theme.font.size.sm};
  font-weight: ${theme.font.weight.medium};
  color: ${theme.colors.textSecondary};
  transition: color ${theme.transition.fast};

  &:hover {
    color: ${theme.colors.accent};
  }
`;

export default Navbar;
