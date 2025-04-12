# Blog Application REST API

![Java Version](https://img.shields.io/badge/Java-17-blue)
![Spring Boot Version](https://img.shields.io/badge/Spring%20Boot-3.x+-brightgreen) <!-- Verify and update with your exact Spring Boot version -->

## Overview

This project provides a RESTful API backend for a blog application, version `v0`. It allows users to register, log in securely using JWT (JSON Web Tokens) with refresh token support, manage their profiles, create, read, update, delete, and manage the publication status of blog posts using a MongoDB database.

## Features

*   **User Authentication:**
    *   User Signup (`POST /auth/signup`) - Requires `SignupRequest` body, returns `AuthResponse`.
    *   User Login (`POST /auth/login`) - Requires `LoginRequest` body, returns an object likely containing the access token (schema just shows `type: object`). Issues Access Token (and likely Refresh Token via Cookie/Body - check your implementation).
    *   Token Refresh (`POST /auth/refresh`) - Renews Access Tokens using a Refresh Token (likely sent via `HttpOnly` cookie or request body - check your implementation). Returns `type: object`.
    *   User Logout (`POST /auth/logout`) - Invalidates session/tokens (check your implementation detail). Returns `type: object`.
*   **User Management:**
    *   Get All Users (`GET /api/users`) - Returns array of `User`. Likely requires Admin role.
    *   Get User by Username (`GET /api/users/{username}`) - Returns `User`.
    *   Create User (`POST /api/users`) - Requires `SignupRequest` body, returns `boolean`. Potentially an admin-only alternative to signup.
    *   Delete User by ID (`DELETE /api/users/{id}`) - Returns `200 OK`. Likely requires Admin role.
*   **Blog Post Management:**
    *   Create Blog Post (`POST /api/blogs`) - Requires `Blog` body. Returns created `Blog`. Requires authentication.
    *   Get All Published Blog Posts (`GET /api/blogs`) - Returns array of `Blog`. Publicly accessible. (Note: `/api/blogs/search` also exists for searching).
    *   Search Published Blog Posts (`GET /api/blogs/search?search=...`) - Requires `search` query parameter. Returns array of `Blog`. Publicly accessible.
    *   Get All Blogs (Published & Unpublished) (`GET /api/blogs/all`) - Returns array of `Blog`. Likely requires Admin or specific permissions.
    *   Get Logged-in User's Unpublished Blogs (`GET /api/blogs/unpublished/my`) - Returns array of `Blog`. Requires authentication.
    *   Get All Unpublished Blogs (`GET /api/blogs/unpublished/all`) - Returns array of `Blog`. Likely requires Admin role.
    *   Get Blog Post by ID (`GET /api/blogs/{id}`) - Returns `Blog`. Public if published, protected if not.
    *   Update Blog Post (`PUT /api/blogs/{id}`) - Requires `Blog` body. Returns updated `Blog`. Requires authentication (Author/Admin).
    *   Delete Blog Post (`DELETE /api/blogs/{id}`) - Returns `200 OK`. Requires authentication (Author/Admin).
    *   Get Published Blogs by Username (`GET /api/blogs/user/{username}`) - Returns array of `Blog`. Publicly accessible.
    *   Get Published Blogs by Topic (`GET /api/blogs/topic/{topic}`) - Returns array of `Blog`. Publicly accessible.
    *   Toggle Blog Post Publish Status (`PUT /api/blogs/toggleStatus/{id}`) - Returns `ApiResponse`. Requires authentication (Author/Admin).

## Technologies Used

*   **Java:** 17
*   **Spring Boot:** 3.x+ (Verify exact version)
*   **Spring Security:** For JWT-based authentication and authorization.
*   **Spring Data MongoDB:** For database interaction with MongoDB.
*   **MongoDB:** NoSQL document database.
*   **Springdoc OpenAPI:** For generating interactive API documentation (Swagger UI).
*   **Maven / Gradle:** Your project's build automation tool.
*   **Lombok:** (Likely used) To reduce boilerplate code.
*   **jjwt (or similar):** Library for JWT creation and validation.

## Prerequisites

*   **JDK:** Java 17 or later installed.
*   **Maven / Gradle:** Your project's build tool must be installed.
*   **MongoDB:** A running instance of MongoDB (local or cloud-based like MongoDB Atlas).
*   **Git:** For cloning the repository.

## Setup and Running

1.  **Clone the repository:**
    *   You need to clone your specific Git repository to your local machine.
    ```bash
    # Example command structure, use your actual repository URL:
    # git clone <Your-Repository-URL>
    # cd <your-project-directory>
    ```

2.  **Configure the Application:**
    *   Open the `src/main/resources/application.properties` (or `application.yml`) file.
    *   **MongoDB:** Update `spring.data.mongodb.uri` or individual properties like `spring.data.mongodb.host`, `spring.data.mongodb.port`, `spring.data.mongodb.database`, `spring.data.mongodb.username`, `spring.data.mongodb.password` to match your MongoDB setup.
    *   **JWT Secrets:** Configure your JWT secret key and expiration times. You **must** locate the property names used in your application configuration (e.g., `app.jwtSecret`, `app.jwtExpirationMs`, `app.jwtRefreshExpirationMs`) and set them appropriately.
        *   **Security Warning:** Do **not** commit actual secrets directly in configuration files to version control. Use environment variables, configuration servers, or other secure methods for production or shared environments.
    *   Review other properties as needed (server port, logging levels, etc.).

3.  **Build the project:**
    *   Use the command appropriate for your project's build tool:
    *   **Using Maven:**
        ```bash
        mvn clean install
        ```
    *   **Using Gradle:**
        ```bash
        ./gradlew build
        ```

4.  **Run the application:**
    *   Use the command appropriate for your project's build tool:
    *   **Using Maven:**
        ```bash
        mvn spring-boot:run
        ```
    *   **Using Gradle:**
        ```bash
        ./gradlew bootRun
        ```
    *   **Alternatively, run the executable JAR file:**
        ```bash
        # Ensure the JAR is built first (step 3)
        # You need to replace <artifact-name>-<version>.jar with your actual JAR filename from the 'target' directory (Maven) or 'build/libs' (Gradle).
        java -jar target/<artifact-name>-<version>.jar
        # OR (for Gradle)
        # java -jar build/libs/<artifact-name>-<version>.jar
        ```

The application should start and be accessible at `http://localhost:8080` (or your configured port).

## API Documentation (Swagger UI)

Interactive API documentation is available via Springdoc OpenAPI (Swagger UI) once the application is running:

**URL:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Use this UI to:
*   View all available endpoints and their details (HTTP methods, parameters, request/response bodies).
*   See data schemas (`Blog`, `User`, `LoginRequest`, etc.).
*   Test the API endpoints directly from your browser.
    *   **Authentication:** To test protected endpoints, first use the `/auth/login` endpoint. Copy the received Access Token (likely from the response body field named `token` within the `AuthResponse` schema, although the login response schema is generic). Then, click the "Authorize" button (usually near the top right) and paste the token into the `BearerAuth` (or similarly named) security definition (typically in the format `Bearer <your_access_token>`).

## Authentication Flow (JWT + Refresh Token)

This API uses JWT for securing endpoints. The typical flow is:

1.  **Signup:** A user registers via `POST /auth/signup`. The response (`AuthResponse`) includes a confirmation message and potentially an initial JWT Access Token in the `token` field.
2.  **Login:** The user logs in via `POST /auth/login`.
    *   **Response:** The backend validates credentials and issues:
        *   A short-lived **Access Token (JWT)**: Sent back in the response body (check the exact response structure, as the OpenAPI schema is `type: object`). This token is used in the `Authorization: Bearer <token>` header.
        *   A long-lived **Refresh Token**: Typically sent as an `HttpOnly`, `Secure` cookie (common practice) or potentially included in the response body. **Verify your implementation.**
3.  **Accessing Protected Resources:** The frontend includes the Access Token in the `Authorization: Bearer <token>` header for requests to protected endpoints. The backend validates this token.
4.  **Access Token Expiry:** When the Access Token expires, the backend will return a `401 Unauthorized` error.
5.  **Token Refresh:**
    *   The frontend (usually via an HTTP interceptor) detects the `401`.
    *   It makes a request to `POST /auth/refresh`.
    *   If using `HttpOnly` cookies, the browser **automatically** sends the Refresh Token cookie. If not using cookies, the frontend must send the stored Refresh Token (e.g., in the request body - verify your implementation).
    *   The backend validates the Refresh Token.
    *   If valid, the backend issues a **new Access Token** (response schema is `type: object`, check your implementation for the new token's location) and potentially rotates the Refresh Token.
    *   The frontend receives the new Access Token, stores it, and **retries** the original request that failed.
6.  **Logout:** The user initiates logout via `POST /auth/logout`.
    *   The backend should invalidate the Refresh Token (e.g., remove it from the database or blacklist it) and potentially clear the Refresh Token cookie via response headers.
7.  **Refresh Token Expiry/Invalidation:** If the Refresh Token itself is expired or invalid when `/auth/refresh` is called, the backend returns an error (`401` or `403`), forcing the user to log in again.

## API Endpoint Summary

*   **Authentication:** `/auth/signup`, `/auth/login`, `/auth/refresh`, `/auth/logout`
*   **Users:** `/api/users`, `/api/users/{username}`, `/api/users/{id}`
*   **Blogs:** `/api/blogs`, `/api/blogs/search`, `/api/blogs/all`, `/api/blogs/unpublished/my`, `/api/blogs/unpublished/all`, `/api/blogs/{id}`, `/api/blogs/user/{username}`, `/api/blogs/topic/{topic}`, `/api/blogs/toggleStatus/{id}`

## Configuration Reference

Key configuration properties commonly reside in `src/main/resources/application.properties` or `application.yml`:

*   `spring.data.mongodb.uri`: Connection string for your MongoDB instance.
*   *(Alternatively: `spring.data.mongodb.host`, `.port`, `.database`, `.username`, `.password`)*
*   `app.jwtSecret`: **Critical Security!** Secret key for signing JWTs. You *must* verify the exact property name used in your code. Use a strong, unguessable secret, managed securely (e.g., via environment variables).
*   `app.jwtExpirationMs`: Expiration time for Access Tokens (in milliseconds). Verify property name.
*   `app.jwtRefreshExpirationMs`: Expiration time for Refresh Tokens (in milliseconds). Verify property name.
*   `server.port`: Port the application runs on (default 8080).

*(Note: Verify the exact property names used for JWT configuration in your application's files or `@Value` annotations.)*

## Contributing

Consider adding guidelines here if you want others to contribute. For example:
*   Fork the repository.
*   Create a new branch (`git checkout -b feature/YourFeature`).
*   Make your changes.
*   Commit your changes (`git commit -m 'Add some feature'`).
*   Push to the branch (`git push origin feature/YourFeature`).
*   Open a Pull Request.
