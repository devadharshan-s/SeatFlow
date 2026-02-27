# SeatFlow

SeatFlow is a BookMyShow-style backend project that evolved through:
- monolith
- modular monolith
- microservices (current focus in this repo)

## Microservices In This Repo

- `bookmyshow-booking-service` (port `8085`)
- `bookmyshow-payment-service` (port `8084`)
- `bookmyshow-show-service` (port `8086`)
- `bookmyshow-theatre-service` (port `8087`)
- `bookmyshow-movie-service` (port `8088`)

## Tech Stack

- Java 21
- Spring Boot
- Spring Security + Keycloak (JWT)
- Spring Data JPA + MySQL
- OpenFeign (service-to-service calls)
- Stripe (payment module)

## Prerequisites

- Java 21
- Maven
- MySQL running locally
- Keycloak running locally (`http://localhost:8080`)

## Required Databases

Create these databases before starting services:

- `bookmyshow_booking`
- `bookmyshow_payment`
- `bookmyshow_show`
- `bookmyshow_theatre`
- `bookmyshow_movie`

## Environment Variables

Set these before running:

- `DB_PASSWORD`
- `KEYCLOAK_CLIENT_SECRET`
- `STRIPE_SECRET_KEY` (for payment service)
- `STRIPE_WEBHOOK_SECRET` (for payment service)

## Run Services

Start each service from its folder:

```bash
mvn spring-boot:run
```

Recommended startup order:
1. `bookmyshow-theatre-service`
2. `bookmyshow-movie-service`
3. `bookmyshow-show-service`
4. `bookmyshow-booking-service`
5. `bookmyshow-payment-service`

## Notes

- APIs return a consistent `ApiResponse` envelope.
- Global exception handlers are configured per service.
- Feign clients propagate auth token + downstream error messages.
- Stripe webhook endpoint is in payment service: `POST /payments/webhook`.

## Project Evolution

This repository now focuses on the microservices split only.  
The active services are the five `bookmyshow-*-service` folders listed above.
