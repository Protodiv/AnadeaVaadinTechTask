# TechTask - Full-Stack Kotlin (Vaadin) Application

## 🎯 Project Overview
This is a microservices-based web application designed to demonstrate a modern Kotlin/Vaadin stack. It features role-based access control (User/Admin), automated database seeding, and a user management dashboard.

## ⚙️ Tech Stack
* **Language:** Kotlin
* **UI Framework:** Vaadin (using [KaribuDSL](https://github.com/mvysny/karibu-dsl))
* **Architecture:** Microservices (Auth, User, API Gateway, Frontend)
* **Databases:** Two separate PostgreSQL instances (Auth DB and User DB)
* **Orchestration:** Kubernetes (Kustomize)

## 🚀 How to Run
This project is designed to be deployed exclusively via Kubernetes.

### Prerequisites
1.  A running Kubernetes cluster (e.g., Docker Desktop, Minikube, or Kind).
2.  `kubectl` CLI tool installed and configured.

### Deployment Steps
To deploy the entire stack (services, databases, and configurations), run the following command from the project root:

```bash
kubectl apply -k k8s/
```

This command will:
*   Create the `anadea` namespace.
*   Deploy PostgreSQL StatefulSets for both the Auth and User services.
*   Start the Auth, User, Gateway, and Frontend deployments.
*   Initialize all necessary ConfigMaps, Secrets, and Services.

### Verifying the Deployment
Check the status of the pods:
```bash
kubectl get pods -n anadea
```
Wait until all pods are in the `Running` state.


Once the host entry is added, you can access the application in your browser at:
`http://localhost`

## 🔐 Authentication & Roles
The application enforces role-based behavior:
*   **User Role:** Read-only access to the user list. Can search and sort.
*   **Admin Role:** Full CRUD access (Create, Edit, Delete users).

### Default Credentials
For ease of testing, the **Login Page** includes an **Auto-Input** button. Clicking this button will automatically populate the credentials for both the Admin and User test accounts.

## 📊 Data Initialization
On startup, the application performs the following:
*   **Schema Migration:** Automatically applies database schemas.
*   **Seeding:** The User database is automatically populated with **500 test users** (1 Admin and 499 standard Users).
*   **Readiness:** Services wait for database availability before fully starting up.

## 🏗 Architecture Notes
*   **Separation of Concerns:** Authentication data is stored and managed in the `auth` service/database, while profile and business data reside in the `user` service/database.
*   **API Gateway:** Acts as the single entry point for the frontend to communicate with various backend microservices.
