# Product Manager

A Spring Boot web application that manages products with a modern frontend using HTMX and Web Awesome icons.

## Features

- **Product Display**: Load and view products from PostgreSQL database in a beautiful table
- **Add Products**: Add new products via a form with real-time updates (no page reload)
- **Scheduled Data Import**: Automatically fetches product data from https://famme.no/products.json on startup
- **Modern UI**: Clean, responsive design with HTMX for dynamic interactions
- **Database Migrations**: Flyway for version-controlled database schema

## Tech Stack

- **Backend**: Spring Boot 3.5.7 (Java 22)
- **Database**: PostgreSQL 16
- **Frontend**:
  - HTMX 2.0.4 (for dynamic updates without page reload)
  - Web Awesome / Font Awesome icons
  - Custom CSS with gradient design
- **ORM**: Spring Data JPA with Hibernate
- **Database Migrations**: Flyway
- **Build Tool**: Gradle (Kotlin DSL)

## Architecture



### Project Structure

```
src/main/java/com/demo/product_manager/
├── entity/          # JPA entities
├── repository/      # Spring Data repositories
├── service/         # Business logic and scheduled jobs
├── controller/      # REST controllers
└── dto/            # Data transfer objects

src/main/resources/
├── db/migration/   # Flyway migration scripts
├── templates/      # Thymeleaf HTML templates
└── application.properties
```

## Prerequisites

- Java 22 or higher
- Docker and Docker Compose (for PostgreSQL)
- Gradle (included via wrapper)

## Getting Started

### 1. Start PostgreSQL Database

```bash
docker-compose up -d
```

This will start a PostgreSQL container on port 5432 with:
- Database: `productdb`

### 2. Build the Application

```bash
./gradlew build
```

### 3. Run the Application

```bash
./gradlew bootRun
```

The application will:
1. Start on http://localhost:8080
2. Run Flyway migrations to create the database schema
3. Execute the scheduled job to fetch 50 products from https://famme.no/products.json
4. Be ready to serve requests

### 4. Access the Application

Open your browser and navigate to: http://localhost:8080


## Database Operations

### View Database

```bash
docker exec -it product-manager-db psql -U postgres -d productdb
```

### Useful SQL Commands

```sql
-- Count products
SELECT COUNT(*) FROM products;

-- View all products
SELECT id, title, vendor, product_type, price FROM products;

-- View products with variants
SELECT id, title, variants FROM products WHERE variants IS NOT NULL;
```

### Stop Database

```bash
docker-compose down
```

### Stop and Remove Data

```bash
docker-compose down -v
```

## Development

### Hot Reload

The application uses Spring Boot DevTools for automatic restart on code changes.


## Troubleshooting

### Database Connection Issues

- Ensure PostgreSQL is running: `docker ps`
- Check logs: `docker logs product-manager-db`
- Verify port 5432 is not in use: `lsof -i :5432`

### Application Won't Start

- Check Java version: `java -version` (should be 22+)
- Clean build: `./gradlew clean build`
- Check application logs for errors

### Products Not Loading

- Check if scheduled job ran: Look for "Successfully saved X products" in logs
- Verify database has data: `docker exec -it product-manager-db psql -U postgres -d productdb -c "SELECT COUNT(*) FROM products;"`
- Check network access to https://famme.no/products.json

