# BlogApp

A secure blog application built with **Spring Boot** and **MongoDB**, featuring user authentication via **JWT**, role-based access control, and RESTful APIs.

## Features

- User Signup, Login, and Logout
- JWT-based Authentication (Access & Refresh Tokens)
- Role-Based Authorization (Admin, User, etc.)
- Blog CRUD operations
- MongoDB integration with Spring Data
- Secure API endpoints with Spring Security
- Validation and error handling

## Tech Stack

- Java 21
- Spring Boot 3.4.4
- Spring Security
- Spring Data MongoDB
- JSON Web Token (JJWT)
- Lombok
- Maven

## API Endpoints

| Method | Endpoint        | Description              | Access        |
|--------|-----------------|--------------------------|---------------|
| POST   | `/auth/signup`  | Register a new user      | Public        |
| POST   | `/auth/login`   | Login and get JWT tokens | Public        |
| POST   | `/auth/refresh` | Get new access token     | Public        |
| GET    | `/blogs`        | View all blogs           | Public        |
| GET    | `/blogs/{id}`   | View a blog by ID        | Public        |
| POST   | `/blogs`        | Create a new blog        | Authenticated |
| PUT    | `/blogs/{id}`   | Update a blog            | Author/Admin  |
| DELETE | `/blogs/{id}`   | Delete a blog            | Author/Admin  |


## Getting Started

### Prerequisites

- Java 21+
- Maven
- MongoDB Atlas or Local MongoDB instance

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/raghunath704/BlogApp.git
   cd BlogApp
### Configure MongoDB

Set your MongoDB URI in `application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster-url>/<database>?retryWrites=true&w=majority
