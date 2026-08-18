# Catalogs & User Identity Services

This document details the catalog microservices (**Movie** and **Theatre** services) and the **User Service** which synchronizes identities with Keycloak IAM.

---

## 1. User Identity & Auth Service (`bookmyshow-user-service`)

The `bookmyshow-user-service` (Port `8089`) coordinates client credentials registration and role-based permissions. It manages a local MySQL representation of profiles and bridges sync requests to Keycloak.

### A. Core Classes deep-Dive:
* **[UserService](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/bookmyshow-user-service/src/main/java/org/example/bookmyshowuserservice/user/service/UserService.java)**:
  * **Use**: Entry point for register, sync, delete, and lookup operations.
  * **Optimization (Compensating Transaction Pattern)**: When registering a user, the system creates the profile in Keycloak first to reserve the username. If the subsequent local MySQL database save fails (e.g. database disconnect), a compensation block is executed to delete the Keycloak account:
    ```java
    try {
        ...
        userRepository.save(user);
    } catch (Exception ex) {
        keyCloakAdminService.deleteUser(keycloakId); // Compensation rollback
        throw new UserOperationException("Failed to save user...", ex);
    }
    ```
    This prevents orphaned credentials in Keycloak and guarantees system-wide eventual consistency.
  * **Redundant**: The entity stores the user's raw password string: `user.setPassword(request.getPassword())`.
  * **Improvement / Security Warning**: Storing raw passwords in the local database is a major security hazard. Since Keycloak handles password hash verification securely, the local `User` entity should entirely omit the `password` field.
* **[KeyCloakAdminService](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/bookmyshow-user-service/src/main/java/org/example/bookmyshowuserservice/services/KeyCloakAdminService.java)**:
  * **Use**: Interacts directly with Keycloak REST endpoints using Spring Boot's new `RestClient`. Retrieves admin tokens using OAuth2 client credentials and provisions user profiles.

### B. Auxiliary Classes:
* `User` / `Role`: JPA entities mapping accounts to permissions (e.g., `ROLE_USER`, `ROLE_ADMIN`).
* `RoleDataInitializer`: Command-line runner populating default database roles (`USER`, `ADMIN`, `THEATRE_MANAGER`) on boot.

---

## 2. Theatre & Screens Service (`bookmyshow-theatre-service`)

The `bookmyshow-theatre-service` (Port `8087`) manages physical structures, screen rooms, screen size properties, and structural layout configurations.

### A. Core Classes deep-Dive:
* **[TheatreService](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/bookmyshow-theatre-service/src/main/java/org/example/bookmyshowtheatreservice/theatre/service/TheatreService.java)**: Exposes standard CRUD operations to manage theatres and addresses.
* **[ScreenService](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/bookmyshow-theatre-service/src/main/java/org/example/bookmyshowtheatreservice/theatre/service/ScreenService.java)**: Exposes screens mapping.
* **[SeatService](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/bookmyshow-theatre-service/src/main/java/org/example/bookmyshowtheatreservice/theatre/service/SeatService.java)**: Creates and queries structural seat layouts (static layout grids).

### B. Auxiliary Classes:
* `Theatre` / `Screen` / `Seat` / `Address`: JPA models storing coordinates.

---

## 3. Movie Catalog Service (`bookmyshow-movie-service`)

The `bookmyshow-movie-service` (Port `8088`) serves as a simple catalog of available movies.

### Core Class:
* **`MovieService`**: Manages movies, categories, runtime, languages, and ratings.
* **Optimizations**: Pure relational CRUD, low latency due to minimal joins.
* **Redundancies**: Basic entity-DTO mapping boilerplate.
* **Improvements**: Add a search indexes caching layer (e.g. Redis) to decrease query times during high movie search traffic.

---

## 4. Reputable Reference Sources

* **SAGA Compensation Pattern in Microservices**: [Baeldung Saga Pattern Guide](https://www.baeldung.com/cs/saga-pattern-microservices)
* **Keycloak Admin API Reference**: [Keycloak REST API Documentation](https://www.keycloak.org/docs-api/latest/rest-api/index.html)
* **Spring Boot RestClient Guide**: [Baeldung Spring Boot RestClient](https://www.baeldung.com/spring-boot-restclient)
* **Spring Boot Transactions**: [Spring Framework Transaction Management Reference](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction)
