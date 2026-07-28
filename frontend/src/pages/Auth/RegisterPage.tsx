// src/pages/Auth/RegisterPage.tsx
import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import styled from 'styled-components';
import { registerUser } from '../../api/auth';
import { theme } from '../../styles/theme';

const RegisterPage: React.FC = () => {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (password !== confirmPassword) {
            setError('Passwords do not match.');
            return;
        }

        setLoading(true);
        try {
            await registerUser(username, password);
            setSuccess('Registration successful! Redirecting to login…');
            setTimeout(() => navigate('/login'), 2000);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Registration failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <PageBg>
            <AuthCard>
                <CardHeader>
                    <LogoText><LogoAccent>Seat</LogoAccent>Flow</LogoText>
                    <CardTitle>Create account</CardTitle>
                    <CardSubtitle>Join SeatFlow to start booking</CardSubtitle>
                </CardHeader>

                <Form id="register-form" onSubmit={handleSubmit}>
                    <Field>
                        <Label htmlFor="register-username">Username</Label>
                        <Input
                            id="register-username"
                            type="text"
                            placeholder="Choose a username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            autoComplete="username"
                        />
                    </Field>
                    <Field>
                        <Label htmlFor="register-password">Password</Label>
                        <Input
                            id="register-password"
                            type="password"
                            placeholder="Create a password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            autoComplete="new-password"
                        />
                    </Field>
                    <Field>
                        <Label htmlFor="register-confirm-password">Confirm Password</Label>
                        <Input
                            id="register-confirm-password"
                            type="password"
                            placeholder="Repeat your password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                            autoComplete="new-password"
                        />
                    </Field>

                    {error && <ErrorMsg role="alert">{error}</ErrorMsg>}
                    {success && <SuccessMsg role="status">{success}</SuccessMsg>}

                    <SubmitBtn id="register-submit" type="submit" disabled={loading}>
                        {loading ? 'Creating account…' : 'Create Account'}
                    </SubmitBtn>
                </Form>

                <CardFooter>
                    Already have an account?{' '}
                    <FooterLink to="/login">Login here</FooterLink>
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

const SuccessMsg = styled.p`
  font-size: ${theme.font.size.sm};
  color: ${theme.colors.success};
  padding: ${theme.spacing.sm} ${theme.spacing.md};
  background: rgba(46, 204, 113, 0.08);
  border: 1px solid rgba(46, 204, 113, 0.3);
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

export default RegisterPage;
