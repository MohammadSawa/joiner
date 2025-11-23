# Joiner

Joiner is a backend application built with Spring Boot and Maven. It provides user authentication, member profile management, and advanced filtering with role-based access control. The project is designed with clean code principles, secure authentication using session-based security, and a scalable architecture.

## 🚀 Getting Started

### Prerequisites
* JDK 21 or later
* Maven 3.8+
* MySQL 8.0 or later

### Installation

```bash
# Clone the repository
git clone https://github.com/MohammadSawa/joiner.git

# Navigate into the project
cd joiner

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Configuration

Update database connection settings in `src/main/resources/application.properties` before running the app:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/joiner
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

## 📖 API Guide

Below is a high-level overview of the available API endpoints. All endpoints (except signup/login) require an active session.

Localization support: Add `Accept-Language: en` or `Accept-Language: ar` header for English or Arabic responses.

### 🔑 Authentication APIs (`/api/v1/auth`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/register` | Register a new user | Public |
| POST | `/login` | Authenticate and create session | Public |
| POST | `/logout` | Logout current session | Authenticated |

### 👤 Member APIs (`/api/v1/members`)

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/` | Create member profile | Authenticated (1 per user) |
| GET | `/me` | Get current user's profile | Authenticated |
| GET | `/` | List all members (paginated) | Admin |
| GET | `/{id}` | Get specific member by ID | Admin |
| GET | `/?firstName=...&gender=...` | Filter members by criteria | Admin |
| PATCH | `/{id}` | Update member info | Owner / Admin |
| DELETE | `/{id}?hard=false` | Soft delete member | Admin |
| DELETE | `/{id}?hard=true` | Hard delete member permanently | Admin |

## 🔐 Roles & Permissions

### Admin
- Full CRUD on all members
- Can list and filter all members
- Can view any member profile
- Can soft/hard delete members

### User
- Can create ONE member profile
- Can view and edit only their own profile
- Cannot list all members
- Cannot delete members

## 📝 Request/Response Examples

### Register
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -H "Accept-Language: en" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password12345"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": "uuid",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "success": true
  },
  "timestamp": "2025-11-23T10:30:00"
}
```

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -H "Accept-Language: en" \
  -d '{
    "email": "john@example.com",
    "password": "password12345"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "uuid",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "USER",
    "authenticated": true
  },
  "timestamp": "2025-11-23T10:30:00"
}
```

### Create Member Profile
```bash
curl -X POST http://localhost:8080/api/v1/members \
  -H "Content-Type: application/json" \
  -H "Accept-Language: en" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "mobileNumber": "0790001111",
    "gender": "MALE",
    "membershipType": "INTERNAL",
    "persona": "INDIVIDUAL"
  }'
```

### List All Members (Admin Only)
```bash
curl -X GET "http://localhost:8080/api/v1/members?page=0&size=10" \
  -H "Accept-Language: en"
```

### Filter Members (Admin Only)
```bash
curl -X GET "http://localhost:8080/api/v1/members?page=0&size=10&firstName=John&gender=MALE&membershipType=INTERNAL" \
  -H "Accept-Language: en"
```

### Update Member (Partial)
```bash
curl -X PATCH http://localhost:8080/api/v1/members/{memberId} \
  -H "Content-Type: application/json" \
  -H "Accept-Language: en" \
  -d '{
    "firstName": "John Updated",
    "mobileNumber": "0799999999"
  }'
```

### Delete Member (Soft Delete)
```bash
curl -X DELETE "http://localhost:8080/api/v1/members/{memberId}?hard=false" \
  -H "Accept-Language: en"
```

### Delete Member (Hard Delete)
```bash
curl -X DELETE "http://localhost:8080/api/v1/members/{memberId}?hard=true" \
  -H "Accept-Language: en"
```

## 🌍 Localization

All error and success messages support English and Arabic:
- `Accept-Language: en` → English messages
- `Accept-Language: ar` → Arabic messages

Message files located in `src/main/resources/`:
- `messages.properties` - English messages
- `messages_ar.properties` - Arabic messages

## 📊 Database Schema

### Users Table
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | Primary Key |
| email | String | Unique, Not Null |
| password | String | Hashed, Not Null |
| firstName | String | Not Null |
| lastName | String | Not Null |
| userRole | String | ADMIN / USER |

### Members Table
| Column | Type | Constraints |
|--------|------|-------------|
| id | UUID | Primary Key |
| firstName | String | Not Null |
| lastName | String | Not Null |
| email | String | Not Null |
| mobileNumber | String | Optional |
| gender | Enum | MALE / FEMALE |
| membershipType | Enum | INTERNAL / EXTERNAL |
| persona | Enum | INDIVIDUAL / CORPORATE / GOVERNMENT |
| user_id | UUID | Foreign Key (Users) |
| deleted | Boolean | For soft delete |

## 📁 Project Structure

```
joiner/
├── src/main/java/io/appswave/joiner/
│   ├── config/              # Security & app configuration
│   ├── controller/           # REST controllers
│   ├── service/             # Business logic
│   ├── repository/           # Data access layer
│   ├── entity/              # JPA entities
│   ├── dto/                 # Request/Response DTOs
│   ├── enums/               # Enumerations
│   ├── exception/           # Custom exceptions
│   └── util/                # Utility classes
├── src/main/resources/
│   ├── application.properties
│   ├── messages.properties
│   └── messages_ar.properties
├── pom.xml
├── .gitignore
└── README.md
```

## 🛠️ Tech Stack

- **Spring Boot 4.0.0** - Application framework
- **Spring Security** - Session-based authentication
- **Spring Data JPA** - Database operations
- **MySQL** - Relational database
- **Lombok** - Boilerplate reduction
- **Maven** - Dependency management
- **Java 21** - Programming language

## 📋 Features

✅ User authentication with hashed passwords (BCrypt)  
✅ Role-based access control (ADMIN / USER)  
✅ Member profile CRUD operations  
✅ Advanced filtering and pagination  
✅ Soft & hard delete options  
✅ Session-based security  
✅ Localization (English & Arabic)  
✅ Consistent API response envelope  
✅ Comprehensive error handling  
✅ Input validation with custom messages  
