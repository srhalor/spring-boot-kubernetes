# spring-boot-kubernetes

This repository contains a set of Spring Boot microservices deployed on Kubernetes using Helm. Nginx Ingress is used as the API gateway for routing and security redirection.

## Services Overview

### 1. security-service
- Handles authentication and authorization using JWT tokens.
- Provides endpoints for health checks and authentication validation.
- See [`security-service/README.md`](./security-service/README.md) for details on endpoints, Docker, Helm, and configuration.

### 2. order-service
- Manages customer orders and exposes REST endpoints for order management and health checks.
- Authentication is enforced by Nginx Ingress, which calls the security-service for JWT validation (see `order-service/helm/values.yaml`).
- See [`order-service/README.md`](./order-service/README.md) for endpoints, Docker, Helm, and authentication flow.

### 3. nginx-service
- Provides the Nginx Ingress controller setup for the cluster.
- If a service for the ingress controller is not present, create it using the manifest in [`nginx-service/service.yaml`](./nginx-service/service.yaml).
- See [`nginx-service/README.md`](./nginx-service/README.md) for instructions on exposing Nginx Ingress in Kubernetes.

---

## Quick Start

1. **Clone the repository:**
   ```sh
   git clone <your-repo-url>
   cd spring-boot-kubernetes
   ```

2. **Build and package services:**
   ```sh
   cd security-service && ./mvnw clean package
   cd ../order-service && ./mvnw clean package
   ```

3. **Build Docker images:**
   ```sh
   docker build -t <your-dockerhub-username>/security-service:latest ./security-service
   docker build -t <your-dockerhub-username>/order-service:latest ./order-service
   ```

4. **Deploy to Kubernetes with Helm:**
   ```sh
   helm upgrade --install security-service ./security-service/helm --namespace myspace --create-namespace
   helm upgrade --install order-service ./order-service/helm --namespace myspace --create-namespace
   ```

5. **Set up Nginx Ingress (if not present):**
   - Apply the service manifest:
     ```sh
     kubectl apply -f nginx-service/service.yaml
     ```

---

## Documentation & References
- [Kubernetes & Helm Command Reference](./documentation/Comands.md)
- Each service contains its own `README.md` and `help.md` for setup and usage details.
- Official docs:
  - [Kubernetes](https://kubernetes.io/docs/)
  - [Helm](https://helm.sh/docs/)
  - [Spring Boot](https://spring.io/projects/spring-boot)

---

For more advanced usage, troubleshooting, and developer setup, see the `help.md` files in each service directory and the command reference in `documentation/Comands.md`.
