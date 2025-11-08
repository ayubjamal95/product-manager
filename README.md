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

### Database Schema

The application uses a single `products` table with the following structure:

```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    vendor VARCHAR(255),
    product_type VARCHAR(255),
    price DECIMAL(10, 2),
    variants JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Note**: Product variants are stored as JSONB for flexibility, as one product can have multiple variants.

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
- Username: `postgres`
- Password: `postgres`

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

## How It Works

### Scheduled Job

The `ProductService` has a scheduled method that:
- Runs immediately on startup (`initialDelay = 0`)
- Fetches products from https://famme.no/products.json
- Saves up to 50 products to the database
- Only runs once (uses `fixedDelay = Long.MAX_VALUE`)
- Skips execution if products already exist

### HTMX Integration

The frontend uses HTMX for seamless updates:

1. **Load Products**:
   - Clicking "Load Products" sends GET request to `/products`
   - Server returns HTML table fragment
   - HTMX swaps the content into `#products-container`

2. **Add Product**:
   - Form submits POST request to `/products`
   - Server saves product and returns updated table
   - HTMX updates the table without page reload
   - Form automatically resets after submission

### API Endpoints

- `GET /` - Main page (serves index.html)
- `GET /products` - Returns products table HTML fragment
- `POST /products` - Adds new product and returns updated table HTML

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

### Adding Fields

To add new fields to the Product entity:

1. Update `Product.java` entity
2. Create a new Flyway migration in `src/main/resources/db/migration/`
3. Update the controller to handle new fields
4. Update the HTML template

### Testing

```bash
./gradlew test
```

## Configuration

Key configurations in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/productdb
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# Scheduling
spring.task.scheduling.pool.size=2
```

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

## Demo Video

Create a Loom video showing:
1. Starting the application
2. Database being populated with products
3. Loading products in the UI
4. Adding a new product via the form
5. Code walkthrough of key components:
   - Entity and Repository
   - Scheduled job in ProductService
   - Controller endpoints
   - HTMX integration in HTML

## License

MIT