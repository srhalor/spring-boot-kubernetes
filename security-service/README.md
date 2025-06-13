# spring-boot-kubernetes
Spring Boot microservices deployed on Kubernetes using Helm. Nginx Ingress is used for API gateway and security redirection.

## Security Service

The `security-service` is responsible for authenticating and authorizing API requests using JWT tokens. It is built with Spring Boot and Spring Security, and provides endpoints for health checks and authentication validation.

### Key Features
- JWT-based authentication and authorization
- Custom error handling for authentication and access denied errors (JSON responses)
- Health check endpoint for monitoring
- Stateless session management
- Configurable via Helm for Kubernetes deployment

### Endpoints

| Endpoint            | Method | Description                                      | Auth Required |
|---------------------|--------|--------------------------------------------------|---------------|
| `/health`           | GET    | Returns health status of the service             | No            |
| `/api/authenticate` | GET    | Validates JWT and returns authenticated username | Yes (JWT)     |

### JWT Authentication
- All protected endpoints require a valid JWT token in the `Authorization` header (format: `Bearer <token>`).
- The JWT payload must include standard claims: `sub` (subject/username), `exp` (expiration), and optionally `roles`.
- The filter extracts and validates the JWT, and sets the authentication context for downstream use.

### Error Handling
- On authentication failure (invalid/missing/expired JWT), a JSON error response with HTTP 401 is returned.
- On access denied (insufficient roles), a JSON error response with HTTP 403 is returned.
- Error responses follow the structure:
  ```json
  {
    "timestamp": "2025-06-14 12:00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Authentication failed: ...",
    "path": "/api/authenticate"
  }
  ```

### Security Configuration
- Stateless: No HTTP session is created.
- CSRF protection is disabled.
- `/health`, `/actuator/**`, and `/error` are open to all.
- All other endpoints require authentication.
- JWT filter is applied before the default authentication filter.

### Docker
- The service includes a `Dockerfile` for containerization.
- Example build and run commands:
  ```sh
  # Build the JAR file
  ./mvnw clean package

  # Build the Docker image
  docker build -t <your-dockerhub-username>/security-service:latest ./security-service

  # Run the Docker container
  docker run -p 8080:8080 <your-dockerhub-username>/security-service:latest
  ```
- The Dockerfile uses Amazon Corretto JDK 21 (Alpine) and runs the JAR as a non-root user.

### Helm Deployment
- Helm charts for Kubernetes deployment are located in `security-service/helm/`.
- Example deployment commands:
  ```sh
  # Package the Helm chart
  helm package security-service/helm

  # Install or upgrade the release
  helm upgrade --install security-service ./security-service/helm \
    --namespace myspace --create-namespace
  ```
- The chart supports configuration of image, replicas, service, ingress, and more via `values.yaml`.
- Example values to override:
  ```yaml
  image:
    repository: <your-dockerhub-username>/security-service
    tag: latest
  replicaCount: 2
  ingress:
    enabled: true
    hosts:
      - host: api.fmd.com
        paths:
          - path: /security-service(/|$)(.*)
            pathType: ImplementationSpecific
  ```

### Deployment
- The service is containerized and can be deployed on Kubernetes using Helm charts located in `security-service/helm/`.
- Nginx Ingress is used for routing and securing API traffic.

### Example JWT Payload
```
{
  "iss": "issuer",
  "iat": 1718371200,
  "exp": 1718374800,
  "aud": "audience",
  "sub": "username",
  "name": "User Name",
  "roles": ["User", "Admin"]
}
```

---
For more details, see the source code in `security-service/src/main/java/com/fmd/security_service/` and the Helm charts in `security-service/helm/`.
