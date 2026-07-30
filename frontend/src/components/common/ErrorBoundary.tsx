// src/components/common/ErrorBoundary.tsx
import React, { Component, ReactNode } from 'react';

interface Props {
    children: ReactNode;
}

interface State {
    hasError: boolean;
    error: Error | null;
}

class ErrorBoundary extends Component<Props, State> {

    constructor(props: Props) {
        super(props);
        this.state = { hasError: false, error: null };
    }

    static getDerivedStateFromError(error: Error): State {
        return { hasError: true, error };
    }

    componentDidCatch(error: Error, info: React.ErrorInfo) {
        console.error('[ErrorBoundary] Caught error:', error, info);
    }

    render() {
        if (this.state.hasError) {
            return (
                <div style={{
                    minHeight: '100vh',
                    background: '#0d0d0d',
                    color: '#fff',
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    padding: '32px',
                    fontFamily: 'monospace',
                }}>
                    <div style={{
                        background: '#1c1c1e',
                        border: '1px solid #e50914',
                        borderRadius: '12px',
                        padding: '32px',
                        maxWidth: '700px',
                        width: '100%',
                    }}>
                        <h2 style={{ color: '#f84464', marginBottom: '16px' }}>⚠ Something crashed</h2>
                        <p style={{ color: '#a0a0a0', marginBottom: '12px' }}>
                            A JavaScript error occurred. Open DevTools Console for details.
                        </p>
                        <pre style={{
                            background: '#111',
                            padding: '16px',
                            borderRadius: '8px',
                            fontSize: '0.75rem',
                            color: '#ff6b6b',
                            overflowX: 'auto',
                            whiteSpace: 'pre-wrap',
                        }}>
                            {this.state.error?.toString()}
                            {'\n'}
                            {this.state.error?.stack?.split('\n').slice(0, 8).join('\n')}
                        </pre>
                        <button
                            onClick={() => window.location.href = '/movies'}
                            style={{
                                marginTop: '20px',
                                padding: '10px 24px',
                                background: '#f84464',
                                color: '#fff',
                                border: 'none',
                                borderRadius: '8px',
                                cursor: 'pointer',
                                fontFamily: 'sans-serif',
                                fontWeight: 600,
                            }}
                        >
                            ← Back to Movies
                        </button>
                    </div>
                </div>
            );
        }

        return this.props.children;
    }

}

export default ErrorBoundary;
