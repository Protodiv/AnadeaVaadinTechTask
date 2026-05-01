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

### 🌐 Accessing the Application
The application is configured to be accessible at `http://anadea.ua`. To access it from your host machine, you must configure your local `hosts` file to resolve this domain to the IP of your Ingress Controller (usually `127.0.0.1` for local Kubernetes setups).

#### **Windows (Run Notepad as Administrator)**
1. Open `C:\Windows\System32\drivers\etc\hosts`.
2. Add the following line at the end:
   `127.0.0.1 anadea.ua`
3. Save the file.

#### **Linux / macOS (Use sudo)**
1. Open the terminal and run: `sudo nano /etc/hosts`.
2. Add the following line at the end:
   `127.0.0.1 anadea.ua`
3. Save and exit (`Ctrl+O`, `Enter`, `Ctrl+X`).

Once the host entry is added, you can access the application in your browser at:
`http://anadea.ua`

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
