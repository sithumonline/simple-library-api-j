# Simple Library API

A RESTful API for managing a library system, built with Spring Boot and Java 17. This API allows users to manage books and borrowers, handle book borrowing and returns, and maintain library records.

## Features

- Register and manage library borrowers
- Register and manage library books
- List all available books
- Borrow and return books
- Unique ISBN-based book identification
- Concurrent borrowing prevention
- Swagger UI documentation

## Tech Stack

- Java 17
- Spring Boot 3.4.5
- Spring Data JPA
- PostgreSQL
- Gradle
- Docker
- Fly.io (Deployment)

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Gradle

## Getting Started

### Local Development

1. Clone the repository:
```bash
git clone https://github.com/sithumonline/simple-library-api-j.git
cd simple-library-api
```

2. Start the PostgreSQL database using Docker Compose:
```bash
docker-compose up -d
```

3. Run the application:
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### API Documentation

Once the application is running, you can access the Swagger UI documentation at:
- Local: `http://localhost:8080/swagger-ui/index.html`
- Production: `https://simple-library-api.fly.dev/swagger-ui/index.html`

## API Endpoints

### Borrower Management
- `POST /api/borrowers` - Register a new borrower
- `GET /api/borrowers` - List all borrowers

### Book Management
- `POST /api/books` - Register a new book
- `GET /api/books` - List all books
- `POST /api/books/{bookId}/borrow` - Borrow a book
- `POST /api/books/{bookId}/return` - Return a book

## Data Models

### Borrower
- Unique ID
- Name
- Email address

### Book
- Unique ID
- ISBN number
- Title
- Author

## ISBN Rules
- Books with the same title and author but different ISBN numbers are considered different books
- Books with the same ISBN number must have the same title and author
- Multiple copies of books with the same ISBN number are allowed

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/sithumonline/simplelibrary/
│   │       ├── controllers/
│   │       ├── models/
│   │       ├── repositories/
│   │       ├── services/
│   │       └── SimpleLibraryApplication.java
│   └── resources/
│       ├── application.yml
│       └── application-prod.yml
└── test/
    └── java/
        └── com/sithumonline/simplelibrary/
```

## Configuration

The application supports multiple environments:
- Development (default)
- Production

Production configuration is managed through `application-prod.yml` and includes:
- PostgreSQL database configuration
- JPA/Hibernate settings

## Docker Support

The project includes Docker configuration for both the application and database:

### Application Dockerfile
- Uses Gradle JDK17 for building
- Uses Eclipse Temurin JRE 17 for runtime
- Exposes port 8080

### Database (docker-compose.yml)
- PostgreSQL 14
- Persistent volume for data storage
- Environment variables for database configuration

## Deployment

The application is deployed on Fly.io and can be accessed at:
https://simple-library-api.fly.dev

## Assumptions

1. Email addresses are unique for borrowers
2. Book borrowing is limited to one copy per borrower
3. Books cannot be borrowed if already borrowed by another user
4. ISBN numbers follow standard format validation
5. All API endpoints require proper validation and error handling

## Future Improvements

1. Add authentication and authorization
2. Implement rate limiting
3. Add more comprehensive logging
4. Implement caching for frequently accessed data
5. Add more unit and integration tests
6. Implement pagination for list endpoints
7. Add search functionality for books and borrowers

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
