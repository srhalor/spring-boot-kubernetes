# Developer Environment Setup Guide for security-service

This guide will help you set up your local development environment for the `security-service` Spring Boot microservice.

---

## Prerequisites
- **Java 21** (Amazon Corretto or OpenJDK recommended)
- **Maven 3.8+**
- **Docker** (for containerization)
- **Kubernetes** (e.g., Minikube, Docker Desktop, or microk8s)
- **Helm 3+** (for Kubernetes deployments)
- **Git**

---

## 1. Clone the Repository
```sh
git clone <your-repo-url>
cd spring-boot-kubernetes/security-service
```

## 2. Build the Project
```sh
./mvnw clean package
```
- The JAR will be generated in the `target/` directory.

## 3. Run Locally (Without Docker)
```sh
java -jar target/security-service-0.0.1.jar
```
- The service will start on port 8080 (API) and 8081 (actuator health).

## 4. Run with Docker
```sh
docker build -t security-service:dev .
docker run -p 8080:8080 security-service:dev
```

## 5. Run in Kubernetes (with Helm)
- Make sure your Kubernetes cluster is running and `kubectl` is configured.
- Package and deploy with Helm:
```sh
helm upgrade --install security-service ./helm --namespace myspace --create-namespace
```
- To override values, edit `helm/values.yaml` or use `--set` flags.

## 6. Useful Endpoints
- Health: `GET http://localhost:8080/health`
- Authenticate: `GET http://localhost:8080/api/authenticate` (requires JWT)

## 7. Testing
- Run unit and integration tests:
```sh
./mvnw test
```

## 8. Logs & Debugging
- Logs are output to the console by default.
- Logging configuration: `src/main/resources/application-logging.properties`

## 9. Common Issues
- **Port conflicts:** Make sure ports 8080/8081 are free.
- **Missing dependencies:** Run `./mvnw clean install` to resolve.
- **Kubernetes/Helm errors:** Check your cluster status and Helm release logs.

## 10. References
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Helm Documentation](https://helm.sh/docs/)

---
For more advanced commands, see `../documentation/Comands.md`.

