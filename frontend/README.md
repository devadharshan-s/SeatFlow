# Frontend Application

This directory contains the React frontend application for the BookMyShow microservices.

## Setup Instructions

1.  **Create React App with Vite (TypeScript)**:
    ```bash
    npm create vite@latest frontend -- --template react-ts
    ```
    (If you have already created the `frontend` directory, navigate into it first and then run `npm create vite@latest . -- --template react-ts`.)

2.  **Navigate into the `frontend` directory**:
    ```bash
    cd frontend
    ```

3.  **Install dependencies**:
    ```bash
    npm install
    ```
    Then, install additional dependencies:
    ```bash
    npm install react-router-dom axios zustand styled-components
    npm install --save-dev @types/styled-components
    ```

4.  **Run the application**:
    ```bash
    npm run dev
    ```

## Project Structure

The project will follow the architecture designed in the planning phase:

```
src/
├── api/
│   ├── types.ts
├── assets/
├── components/
│   ├── common/
│   └── layout/
├── contexts/
├── hooks/
├── pages/
│   ├── Auth/
│   ├── Movies/
│   ├── Shows/
│   ├── Payments/
│   └── NotFoundPage.tsx
├── store/
├── styles/
│   ├── global.css
│   ├── theme.ts
│   └── mixins.ts
├── utils/
├── App.tsx
├── index.tsx
└── main.tsx
```

## Completed: Initial Frontend Implementation

The initial frontend directory structure, basic styling, and core components have been set up as planned.

## Completed Features

Based on the microservice analysis in `CLAUDE.md`, the following frontend features have been implemented:

1.  **Authentication (Login/Register):**
    *   Implemented user registration (`RegisterPage.tsx`).
    *   Implemented user login (`LoginPage.tsx`).
    *   Integrated with `bookmyshow-user-service` for authentication (`AuthContext.tsx`, `frontend/src/api/auth.ts`).
2.  **Movie Listing and Details:**
    *   Implemented display of a list of movies (`MovieListPage.tsx`).
    *   Implemented display of detailed information for a single movie (`MovieDetailPage.tsx`).
    *   Integrated with `bookmyshow-movie-service` (`frontend/src/api/movie.ts`).
3.  **Show Listing and Booking (API Integration):**
    *   Integrated with `bookmyshow-show-service` (`frontend/src/api/show.ts`).
    *   Integrated with `bookmyshow-booking-service` (`frontend/src/api/booking.ts`).

## Next Steps

1.  **Show Listing and Booking (UI Implementation):**
    *   Display available shows for a movie (`ShowListPage.tsx`).
    *   Implement seat selection and locking for a show (`ShowBookingPage.tsx`).
2.  **Payment Integration:**
    *   Initiate payment for booked tickets.
    *   Handle payment status and redirection (`PaymentStatusPage.tsx`).
    *   Integrate with `bookmyshow-payment-service` (`frontend/src/api/payment.ts` - to be created).
