# nginx-service

This guide explains how to create a Kubernetes Service for Nginx Ingress, which is required to expose your ingress controller to external traffic. If your Kubernetes cluster does not already have a service for the ingress controller, follow these steps.

---

## 1. Why Create a Service for Nginx Ingress?
- The Nginx Ingress controller manages external access to services in your cluster, typically via HTTP/HTTPS.
- A Kubernetes Service of type `NodePort` or `LoadBalancer` is needed to expose the ingress controller's ports (80/443) to the outside world.

---

## 2. Example Service Manifest
Below is an example YAML manifest to create a Service for the Nginx Ingress controller (for microk8s or similar setups):

```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-ingress-microk8s-controller
  namespace: ingress
spec:
  type: NodePort
  selector:
    name: nginx-ingress-microk8s
  ports:
    - name: http
      port: 80
      targetPort: 80
    - name: https
      port: 443
      targetPort: 443
```

- **name**: Should match the label of your ingress controller pods.
- **namespace**: Use the namespace where your ingress controller is deployed (commonly `ingress` or `ingress-nginx`).
- **type**: `NodePort` exposes the service on each node's IP at a static port.
- **ports**: Exposes HTTP (80) and HTTPS (443).

---

## 3. How to Apply the Service
1. Save the above YAML to a file, e.g., `service.yaml`.
2. Apply it with kubectl:
   ```sh
   kubectl apply -f service.yaml
   ```
3. Verify the service:
   ```sh
   kubectl get svc -n ingress
   ```

---

## 4. Accessing Ingress
- After creating the service, you can access your ingress resources via the node's IP and the assigned NodePort.
- For local clusters (like microk8s or minikube), use the node's IP or `localhost` with the NodePort.

---

## References
- [Kubernetes Ingress Controllers](https://kubernetes.io/docs/concepts/services-networking/ingress-controllers/)
- [Nginx Ingress Controller Docs](https://kubernetes.github.io/ingress-nginx/)

---
For more Kubernetes and Helm commands, see `../documentation/Comands.md`.

