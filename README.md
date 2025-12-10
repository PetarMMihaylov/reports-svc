# Reports Service

A Java-based service for generating and managing reports.

## 🚀 Tech Stack
- **Language:** Java 17
- **Build Tool:** Maven
- **Frameworks:** Spring Boot 3.4.0
- **Database:** MySQL
- **Testing:** Unit, API and integration tests

## ✨ Features
- **List Reports by User** – Retrieve all reports for a given user.
- **Get Report Details** – View detailed information about a specific report.
- **Create Report** – Submit a new report via REST API.
- **Delete Report** – Remove a report by its ID.

## 🔗 Integrations
- MySQL database for persistence and H2 database for testing
- External API for fetching report data

## 🏗️ Architecture

This project (`reports-svc`) is designed as a **microservice** within a larger system.  
It exposes RESTful APIs for creating, retrieving, and managing reports.

The service is intended to be consumed by the **Health Insurance Project** (an MVC application).  
The MVC project can call the endpoints of `reports-svc` to:
- Generate new reports for users
- Retrieve existing reports by user ID or report ID
- Delete reports when no longer needed

This separation of concerns allows the Health Insurance Project to focus on user-facing functionality, while `reports-svc` handles reporting logic as an independent, reusable service.


