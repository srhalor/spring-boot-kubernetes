# Kubernetes & Helm Command Reference

This document provides a quick reference for common `kubectl` and `helm` commands used in managing Kubernetes clusters and deploying applications. It is intended to help developers and DevOps engineers work efficiently with microk8s, Kubernetes, and Helm.

---

## Table of Contents
- [Kubernetes (`kubectl`) Commands](#kubernetes-kubectl-commands)
  - [Cluster Management](#cluster-management)
  - [Pod & Service Management](#pod--service-management)
  - [Role & Access Management](#role--access-management)
  - [Troubleshooting & Debugging](#troubleshooting--debugging)
- [Helm Commands](#helm-commands)
- [Useful Resources](#useful-resources)

---

## Kubernetes (`kubectl`) Commands

### Cluster Management
- `kubectl get nodes`: List all nodes in the cluster.
- `kubectl get namespaces`: List all namespaces in the cluster.
- `kubectl create namespace <namespace-name>`: Create a new namespace.
- `kubectl delete namespace <namespace-name>`: Delete a specific namespace.

### Pod & Service Management
- `kubectl get pods`: List all pods in the current namespace.
- `kubectl get services`: List all services in the current namespace.
- `kubectl get all`: List all resources in the current namespace.
- `kubectl get all --all-namespaces`: List all resources across all namespaces.
- `kubectl describe pod <pod-name>`: Get detailed information about a specific pod (useful for debugging).
- `kubectl logs <pod-name>`: View the logs of a specific pod (add `-f` to follow logs in real-time).
- `kubectl exec -it <pod-name> -- /bin/bash`: Open a shell inside a specific pod (useful for troubleshooting or inspecting containers).
- `kubectl apply -f <file.yaml>`: Apply a configuration file to the cluster (create or update resources).
- `kubectl delete -f <file.yaml>`: Delete resources defined in a configuration file.
- `kubectl port-forward <pod-name> <local-port>:<remote-port>`: Forward a local port to a port on a pod (useful for accessing services locally).

### Role & Access Management
- `kubectl create clusterrolebinding kubernetes-dashboard -n kube-system --clusterrole=cluster-admin --serviceaccount=kube-system:kubernetes-dashboard`: Grant cluster-admin permissions to the Kubernetes dashboard.
- `kubectl delete clusterrolebinding kubernetes-dashboard`: Remove the cluster role binding for the Kubernetes dashboard.

### Troubleshooting & Debugging
- Use `kubectl describe` and `kubectl logs` to investigate issues with pods and deployments.
- Use `kubectl get events` to see recent events in the cluster.
- Use `kubectl top pods` and `kubectl top nodes` (requires metrics-server) to monitor resource usage.

#### Tip: Connect kubectl with microk8s
See the official guide: https://microk8s.io/docs/working-with-kubectl

---

## Helm Commands
Helm is a package manager for Kubernetes that helps you manage applications on your cluster. Here are some commonly used `helm` commands:

- `helm repo add <repo-name> <repo-url>`: Add a Helm repository (source of charts).
- `helm repo update`: Update the list of available charts from all repositories.
- `helm search repo <chart-name>`: Search for a chart in the added repositories.
- `helm install <release-name> <chart-name>`: Install a chart as a release (deploy an application).
- `helm upgrade <release-name> <chart-name>`: Upgrade an existing release to a new chart version.
- `helm uninstall <release-name>`: Uninstall a release (remove an application).
- `helm list`: List all installed releases in the current namespace.
- `helm status <release-name>`: Get the status of a specific release.
- `helm get all <release-name>`: Get all information about a specific release (manifests, values, etc.).
- `helm template <chart-name>`: Render the templates in a chart without installing it (useful for previewing manifests).
- `helm create <chart-name>`: Create a new Helm chart (scaffold a new chart directory).

---

## Useful Resources
- [Kubernetes Official Documentation](https://kubernetes.io/docs/)
- [kubectl Cheat Sheet](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)
- [Helm Official Documentation](https://helm.sh/docs/)
- [microk8s Documentation](https://microk8s.io/docs)

---

**Tip:** Always check the namespace you are working in with `kubectl config view --minify | grep namespace:` or by specifying `-n <namespace>` in your commands.

For more advanced usage, refer to the official documentation links above.
