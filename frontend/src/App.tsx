// src/App.tsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { GlobalStyle } from './styles/GlobalStyle';
import { AuthProvider } from './contexts/AuthContext';

import HomePage from './pages/HomePage';
import LoginPage from './pages/Auth/LoginPage';
import RegisterPage from './pages/Auth/RegisterPage';
import MovieListPage from './pages/Movies/MovieListPage';
import MovieDetailPage from './pages/Movies/MovieDetailPage';
import ShowListPage from './pages/Shows/ShowListPage';
import ShowBookingPage from './pages/Shows/ShowBookingPage';
import PaymentStatusPage from './pages/Payments/PaymentStatusPage';
import NotFoundPage from './pages/NotFoundPage';

function App() {

    return (
        <AuthProvider>
            <GlobalStyle />
            <Router>
                <Routes>
                    <Route path="/"                     element={<HomePage />} />
                    <Route path="/login"                element={<LoginPage />} />
                    <Route path="/register"             element={<RegisterPage />} />
                    <Route path="/movies"               element={<MovieListPage />} />
                    {/* Route uses :id to match MovieDetailPage's useParams({ id }) */}
                    <Route path="/movies/:id"           element={<MovieDetailPage />} />
                    {/* ShowListPage reads movieId as a query param: /shows?movieId=X */}
                    <Route path="/shows"                element={<ShowListPage />} />
                    <Route path="/shows/:showId/book"   element={<ShowBookingPage />} />
                    <Route path="/payments/status"      element={<PaymentStatusPage />} />
                    <Route path="*"                     element={<NotFoundPage />} />
                </Routes>
            </Router>
        </AuthProvider>
    );

}

export default App;
