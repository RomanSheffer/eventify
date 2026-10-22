# Eventify — Backend Event Management & Ticket Booking Service

A production-ready backend ecosystem for organizing events and purchasing tickets. The product features a strict Role-Based Access Control (RBAC) architecture on the backend and interacts seamlessly with a modern user interface built with TypeScript and Tailwind CSS.

At the same time, this project is a clean architectural application fully containerized and pre-configured for a fast local setup.

---

## 🛠️ Tech Stack

* **🔹 Backend Core:** Java 17/21, Spring Boot 3.x, Spring Data JPA
* **🔹 Security Layer:** Spring Security, Stateless JWT, Role-Based Access Control (RBAC)
* **🔹 Database & Migrations:** PostgreSQL, Flyway
* **🔹 DevOps & Infrastructure:** Docker, Docker Compose

---

## 📐 Architectural Features

### 🔒 Enterprise-Grade Security
The entire API is secured using stateless JWT authentication via Spring Security. The system strictly separates permissions for two user groups: **User** and **Administrator**.

### 🧩 Clean Code & SOLID Principles
The project is built according to strict code quality standards, fully adhering to the Single Responsibility Principle (SRP). Controllers act purely as a transport layer, while all core business logic is fully isolated within specialized service layers.

### 💾 Data Integrity & Overbooking Protection
Designed an optimized PostgreSQL database schema with automated Flyway migrations. The architecture implements an optimistic locking mechanism (`Optimistic Locking` via `@Version`) within database transactions. This ensures robust protection against race conditions (such as double-booking the same seat by different users simultaneously) during high-traffic events.

### 🐳 Containerized Infrastructure
The backend service and the PostgreSQL database are fully isolated and containerized. The Docker Compose configuration allows running the entire infrastructure setup with a single command.

---

## 🚀 User Guide

### ⏱️ Getting Started
To run the entire ecosystem (Backend + Database) with a single click, make sure you have **Docker** installed, then follow these steps:

#### 📌 Main Principle: The Detached Mode Run
Since the project uses a nested folder structure, you need to navigate to the `eventify` subdirectory before running the orchestration file.

To start the application, execute the following commands in your terminal:

```bash
# Step 1: Navigate to the working directory
cd eventify

# Step 2: Build and start all containers in the background
docker-compose up --build -d
```