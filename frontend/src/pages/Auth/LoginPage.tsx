// src/pages/Auth/LoginPage.tsx
import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import styled from 'styled-components';
import { AuthContext } from '../../contexts/AuthContext';
import { loginUser } from '../../api/auth';
import { theme } from '../../styles/theme';

const LoginPage: React.FC = () => {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const authContext = useContext(AuthContext);

    if (!authContext) throw new Error('AuthContext must be used within an AuthProvider');
    const { login } = authContext;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            const response = await loginUser(username, password);
            login(response.token, response.user);
            navigate('/movies');
        } catch (err: any) {
            setError(err.response?.data?.message || 'Login failed. Please check your credentials.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <PageBg>
            <AuthCard>
                <CardHeader>
                    <LogoText><LogoAccent>Seat</LogoAccent>Flow</LogoText>
                    <CardTitle>Welcome back</CardTitle>
                    <CardSubtitle>Sign in to your account to continue</CardSubtitle>
                </CardHeader>

                <Form id="login-form" onSubmit={handleSubmit}>
                    <Field>
                        <Label htmlFor="login-username">Username</Label>
                        <Input
                            id="login-username"
                            type="text"
                            placeholder="Enter your username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            autoComplete="username"
                        />
                    </Field>
                    <Field>
                        <Label htmlFor="login-password">Password</Label>
                        <Input
                            id="login-password"
                            type="password"
                            placeholder="Enter your password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            autoComplete="current-password"
                        />
                    </Field>

                    {error && <ErrorMsg role="alert">{error}</ErrorMsg>}

                    <SubmitBtn id="login-submit" type="submit" disabled={loading}>
                        {loading ? 'Signing in…' : 'Sign In'}
                    </SubmitBtn>
                </Form>

                <CardFooter>
                    Don't have an account?{' '}
                    <FooterLink to="/register">Register here</FooterLink>
                </CardFooter>
            </AuthCard>
        </PageBg>
    );

};

const PageBg = styled.div`
  min-height: 100vh;
  background: ${theme.colors.base};
  display: flex;
  align-items: center;
  justify-content: center;
  padding: ${theme.spacing.xl};
`;

const AuthCard = styled.div`
  background: ${theme.colors.surfaceElevated};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.card};
  padding: 40px;
  width: 100%;
  max-width: 420px;
  animation: fadeIn 250ms ease both;
`;

const CardHeader = styled.div`
  text-align: center;
  margin-bottom: ${theme.spacing.xxl};
`;

const LogoText = styled.div`
  font-size: ${theme.font.size.xxl};
  font-weight: ${theme.font.weight.bold};
  margin-bottom: ${theme.spacing.lg};
`;

const LogoAccent = styled.span`
  color: ${theme.colors.accent};
`;

const CardTitle = styled.h1`
  font-size: ${theme.font.size.xxl};
  font-weight: ${theme.font.weight.bold};
  color: ${theme.colors.textPrimary};
  margin-bottom: ${theme.spacing.sm};
`;

const CardSubtitle = styled.p`
  font-size: ${theme.font.size.sm};
  color: ${theme.colors.textSecondary};
`;

const Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: ${theme.spacing.lg};
`;

const Field = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${theme.spacing.sm};
`;

const Label = styled.label`
  font-size: ${theme.font.size.sm};
  font-weight: ${theme.font.weight.medium};
  color: ${theme.colors.textSecondary};
`;

const Input = styled.input`
  width: 100%;
  padding: 10px 14px;
  background: ${theme.colors.surface};
  border: 1px solid ${theme.colors.surfaceBorder};
  border-radius: ${theme.radius.button};
  color: ${theme.colors.textPrimary};
  font-size: ${theme.font.size.md};
  transition: border-color ${theme.transition.fast};

  &::placeholder {
    color: ${theme.colors.textMuted};
  }

  &:focus {
    outline: none;
    border-color: ${theme.colors.accent};
  }
`;

const ErrorMsg = styled.p`
  font-size: ${theme.font.size.sm};
  color: ${theme.colors.error};
  padding: ${theme.spacing.sm} ${theme.spacing.md};
  background: rgba(229, 9, 20, 0.08);
  border: 1px solid rgba(229, 9, 20, 0.3);
  border-radius: ${theme.radius.button};
`;

const SubmitBtn = styled.button`
  width: 100%;
  padding: 12px;
  background: ${theme.colors.accent};
  color: ${theme.colors.textPrimary};
  font-size: ${theme.font.size.md};
  font-weight: ${theme.font.weight.semibold};
  border-radius: ${theme.radius.button};
  margin-top: ${theme.spacing.sm};
  transition: background ${theme.transition.fast}, box-shadow ${theme.transition.fast};

  &:hover:not(:disabled) {
    background: ${theme.colors.accentHover};
    box-shadow: ${theme.shadow.button};
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  &:focus-visible {
    outline: 2px solid ${theme.colors.accent};
    outline-offset: 3px;
  }
`;

const CardFooter = styled.p`
  text-align: center;
  margin-top: ${theme.spacing.xl};
  font-size: ${theme.font.size.sm};
  color: ${theme.colors.textSecondary};
`;

const FooterLink = styled(Link)`
  color: ${theme.colors.accent} !important;
  font-weight: ${theme.font.weight.medium};
`;

export default LoginPage;
