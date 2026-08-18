# Netflix Eureka Service Discovery Server

This document covers the configuration and deployment details of the central registry server (`bookmyshow-eureka-server`).

---

## 1. Role in System

The `bookmyshow-eureka-server` acts as the service registry (phonebook) for all other microservices in the SeatFlow system. 

Every service registers its dynamic IP address and port mapping with Eureka, allowing peer-to-peer discovery. This removes the need to hardcode direct host names and dynamic ports in configurations.

---

## 2. Core Class Summary

### [EurekaServerApplication](file:///c:/Users/devad/IdeaProjects/bookmyshow-microservices/bookmyshow-eureka-server/src/main/java/org/example/bookmyshoweurekaserver/EurekaServerApplication.java)

* **Use**: Entry point of the Eureka service. Annotating this class with `@EnableEurekaServer` configures the Spring context to run a Netflix Eureka Service Discovery registry.
* **Optimized**: No complex business optimizations are required as this is a registry daemon.
* **Redundant**: Fully minimal.
* **Improvements**: In a production-grade multi-node environment, Eureka itself should be configured for high availability (peer replication) by setting up multiple instances and having them register with each other.
* **TL;DR**: Runs the discovery server enabling client services to locate each other dynamically.

---

## 3. Configuration Breakdown (`application.properties`)

```properties
spring.application.name=bookmyshow-eureka-server
server.port=8761

# Disable Eureka Dashboard registration with itself as a client
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

# Eureka service URL endpoint configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### Explanation:
* `eureka.client.register-with-eureka=false`: Tells this instance not to attempt to register itself as a client service.
* `eureka.client.fetch-registry=false`: Disables downloading a local cached copy of client registries since this *is* the registry.
* `eureka.client.service-url.defaultZone`: The core endpoint where other services send heartbeats and query metadata.

---

## 4. Reputable Reference Sources

* **Netflix Eureka Architecture**: [Baeldung Spring Cloud Netflix Eureka](https://www.baeldung.com/spring-cloud-netflix-eureka)
* **Eureka Registry Self-Preservation Mode**: [Spring Cloud Eureka Server Docs](https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/#spring-cloud-eureka-server)
