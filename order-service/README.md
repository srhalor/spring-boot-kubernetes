# order-service
Spring Boot microservice for managing customer orders, designed for deployment on Kubernetes using Helm. Nginx Ingress is used for API gateway and security redirection.

## Overview
The `order-service` handles order-related operations. It is built with Spring Boot and exposes REST endpoints for order management and health checks.

### Key Features
- RESTful API for order management
- Health check endpoint using Spring Boot Actuator
- Configurable via Helm for Kubernetes deployment
- Dockerized for easy containerization
- **Authentication is enforced by Nginx Ingress, which calls the security-service API for JWT validation as configured in the Helm `values.yaml` file.**

### Endpoints
| Endpoint      | Method | Description                          | Auth Required                      |
|---------------|--------|--------------------------------------|------------------------------------|
| `/health`     | GET    | Returns health status of the service | No                                 |
| `/api/orders` | GET    | Returns order details (sample)       | Yes (via Ingress/Security Service) |

### Authentication via Nginx Ingress
- Authentication for order-service endpoints is handled by Nginx Ingress.
- The Ingress is configured (see `helm/values.yaml`) to call the security-service `/api/authenticate` endpoint for validating JWT tokens before forwarding requests to order-service.
- Example configuration in `values.yaml`:
  ```yaml
  ingress:
    enabled: true
    className: "nginx"
    annotations:
      nginx.ingress.kubernetes.io/auth-url: "http://security-service.myspace.svc.cluster.local:8080/api/authenticate"
      nginx.ingress.kubernetes.io/auth-response-headers: Authorization
  ```
- This ensures that only authenticated requests reach the order-service.

### Docker
- The service includes a `Dockerfile` for containerization.
- Example build and run commands:
  ```sh
  # Build the JAR file
  ./mvnw clean package

  # Build the Docker image
  docker build -t <your-dockerhub-username>/order-service:latest ./order-service

  # Run the Docker container
  docker run -p 8080:8080 <your-dockerhub-username>/order-service:latest
  ```
- The Dockerfile uses Amazon Corretto JDK 21 (Alpine) and runs the JAR as a non-root user.

### Helm Deployment
- Helm charts for Kubernetes deployment are located in `order-service/helm/`.
- Example deployment commands:
  ```sh
  # Package the Helm chart
  helm package order-service/helm

  # Install or upgrade the release
  helm upgrade --install order-service ./order-service/helm \
    --namespace myspace --create-namespace
  ```
- The chart supports configuration of image, replicas, service, ingress, and more via `values.yaml`.
- Example values to override:
  ```yaml
  image:
    repository: <your-dockerhub-username>/order-service
    tag: latest
  replicaCount: 2
  ingress:
    enabled: true
    hosts:
      - host: api.fmd.com
        paths:
          - path: /order-service(/|$)(.*)
            pathType: ImplementationSpecific
  ```

### Deployment
- The service is containerized and can be deployed on Kubernetes using Helm charts located in `order-service/helm/`.
- Nginx Ingress is used for routing and securing API traffic, including authentication via security-service.

---
For more details, see the source code in `order-service/src/main/java/com/fmd/order_service/` and the Helm charts in `order-service/helm/`.
