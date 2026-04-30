## 🧪 Developer Test Task — Full-Stack Kotlin (Vaadin)

### 🎯 Objective

Design and implement a small web application using modern JVM tooling and frameworks. The goal is to evaluate your ability to build a clean, maintainable, and production-ready application with a UI, backend, and database integration.

---

## ⚙️ Technical Requirements (Non-Functional)

The application **must** adhere to the following constraints:

* **Language:** Kotlin
* **Build tool:** Gradle using Kotlin DSL (`build.gradle.kts`)
* **UI Framework:** Latest stable version of Vaadin
* **Libraries:** Use [KaribuDSL](https://github.com/mvysny/karibu-dsl) for UI building
* **Database:** PostgreSQL
* **Containerization:** Docker & Docker Compose
* **AI** AI coding agents usage is encouraged. However, candidate is responsible for the result and should ensure high coding and architecture standards, not just vibe-code the solution
* The entire application must be runnable with **a single command**:

  ```bash
  docker-compose up
  ```

---

## 📌 Functional Requirements

### 1. Authentication

* Implement a **login page**
* Authentication can be simple (in-memory or DB-backed)
* After successful login, redirect the user to the **dashboard page**

---

### 2. User Roles

There are **two roles**:

* **User**
* **Admin**

Role-based behavior must be enforced.

---

### 3. Dashboard

After login, the user lands on a dashboard containing:

#### 👥 User List (Visible to both roles)

* Display a list of users with at least the following fields:

  * Name
  * Email
  * Created At
  * Updated At

* Features:

  * 🔍 Search/filter by:

    * Name
    * Email
  * ↕️ Sorting by:

    * Name
    * Email
    * Creation date
    * Last update date

---

### 4. Permissions

#### 👤 User Role

* Read-only access to the user list
* Can search and sort users
* Cannot modify data

#### 🛠 Admin Role

* Full access:

  * Create user
  * Edit user
  * Delete user

---

### 5. Data Initialization

* On application startup:

  * Automatically populate the database with **500 test users**
* You may use:

  * SQL scripts
  * Migration tools (e.g., Flyway or Liquibase)
  * Programmatic seeding

---

## 🐳 Deployment Requirements

* Provide a `docker-compose.yml` that:

  * Starts PostgreSQL
  * Starts the application
* The app should:

  * Wait for DB readiness (gracefully handle startup dependency)
  * Apply schema and seed data automatically

---

## 📦 Deliverables

Provide a Git repository containing:

* Full source code
* `docker-compose.yml`
* Gradle configuration (`build.gradle.kts`)
* Clear `README.md` with:

  * Setup instructions
  * How to run the project
  * Default credentials for User/Admin
  * Any assumptions or trade-offs

---

## ✅ Evaluation Criteria

Candidates will be evaluated based on:

### Code Quality

* Clean architecture (layering, separation of concerns)
* Idiomatic Kotlin usage
* Proper null-safety handling

### UI/UX

* Usability of Vaadin UI
* Logical layout and responsiveness

### Backend Design

* REST or service-layer structure
* Proper validation and error handling
* Role-based access control implementation

### Database

* Schema design
* Indexing (if applicable)
* Migration/seeding strategy

### DevOps & Packaging

* Docker setup quality
* Reproducibility (one-command startup)
* Environment configuration

---

## ⭐ Bonus (Optional)

Candidates may optionally implement:

* Pagination for user list
* Unit and/or integration tests
* Audit logging for user changes
* Password hashing & secure authentication
* API layer (REST endpoints)
* CI configuration (e.g., GitHub Actions)

---

## ⏱ Estimated Time

Expected effort: **4–8 hours**

---

## 📬 Submission

Share:

* A link to the repository
* (Optional) Screenshots or short demo video
