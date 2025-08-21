# SnapBid

An auction platform built with **Spring Boot**, **PostgreSQL**, and **WebSocket**, featuring **real-time bidding**, **anti-sniping**, **watchlists**, and **notifications**.

## Features

### User Authentication and Roles
- JWT-based authentication
- Role-based access control(BIDDER, SELLER)
- Secure registration and login endpoints

### Auction Management
- Sellers can create, update, and delete auction items
- Auctions have title, description, starting price, start/end time, and status (SCHEDULED, OPEN, CLOSED, CANCELLED)
- Automatic status transitions (scheduled → open → closed) via scheduled jobs

### Real-time Bidding
- WebSocket-based real-time bid updates
- Optimistic locking ensures no race conditions or duplicate winning bids
- Minimum bid increment enforcement
- Bid history with pagination

### Watchlist
- Users can add/remove auctions to their watchlist
- Paginated watchlist viewing
- Prevents watching own auctions

### Notifications
- Outbid and auction-closed notifications
- Real-time delivery via WebSocket
- Unread count and batch mark-as-read support

### Search and Filtering
- Full-text search on title and description
- Filter by status, price range, end time
- Pagination and sorting support
- JPA Specification based dynamic querying

### Anti-sniping
- Prevents last-second bidding by extending auction end time
- Configurable threshold (default 60 seconds) and extension time (default 120 seconds)
- Maximum number of extensions per auction (default 3)
- Example:
  ```
  Auction end:    14:00:00
  Bid placed at:  13:59:00 (60s before end)
  Result:         End time extended to 14:02:00
                  Extension count increased by 1
  ```
- All connected clients receive real-time status updates via WebSocket
- Configuration in application.properties:
  ```properties
  auction.antiSniping.thresholdSeconds=60   # Trigger window
  auction.antiSniping.extendSeconds=120     # Extension duration
  auction.antiSniping.maxExtensions=3       # Max extensions allowed
  ```


## Project Structure
```
com.fintech.bank_app
├── config/                      # Configuration classes(SecurityConfig, WebSocketConfig, SwaggerConfig)
├── controller/                  # Rest API endpoints
├── dto/                         # Data Transfer Objects
├── exceptions/                  # Exception handling
├── jobs/                        # Scheduled tasks
├── mapper/                      # DTO ↔ Entity conversion logic
├── models/                      # JPA entities
├── realtime/                    # WebSocket components
├── repository/                  # JPA repositories for database access
├── service/                     # Business logic layer
├── spec/                        # JPA Specifications for dynamic queries
├── utils/                       # Utility classes
└── AuctionAppApplication.java   # Application entry point
```

## Technology Stack
- **Java Version**: 17
- **Spring Boot**: Version 3+
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT
- **Real-time**: WebSocket (STOMP)
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven

## Configuration
Set in `application.properties` and `env.properties`:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
jwt.secret=${JWT_SECRET}
bid.optimistic.max-retries=5
```

## Prerequisites
To run the application, ensure you have the following installed:
- Java 17+
- Maven 3.9.10 (configured via Maven Wrapper)
- PostgreSQL (database server running on localhost:5432)
- Git (for cloning the repository)

## Setup instructions
1.  **Clone the repository**:
```
git clone https://github.com/babafemiolatona/snapbid.git
cd snapbid
```
2.  **Configure Environment Variables**: Create an env.properties file in the project root with the following content:
```
DB_NAME=snapbid_db
DB_USER=your_postgres_username
DB_PASSWORD=your_postgres_password
JWT_SECRET=your_base64_encoded_jwt_secret
```
- Replace your_postgres_username and your_postgres_password with your PostgreSQL credentials.
- Generate a secure JWT secret (Base64-encoded) and add it to JWT_SECRET. You can generate a secure JWT secret using command-line tools like openssl or base64.

  Using **openssl** (Linux/Mac):
  ```
  openssl rand -base64 32
  ```
  Using **dd** and **base64** (Linux/Mac):
  ```
  dd if=/dev/urandom bs=32 count=1 2>/dev/null | base64
  ```
3.  **Set Up PostgreSQL**:
- Create a database named bank_app_db (or as specified in DB_NAME)
```
CREATE DATABASE snapbid_db;
```
4.  **Build and Run the Application**: Use the Maven Wrapper to build and run:
```
./mvnw clean install
./mvnw spring-boot:run
```
The application will start on http://localhost:8080.

5. **Access Swagger UI**:
- Open http://localhost:8080/swagger-ui/index.html in your browser to explore and test the API endpoints.

## API Overview

### Authentication

- `POST /api/v1/auth/register` — Register new user
- `POST /api/v1/auth/login` — Obtain JWT

### Public Auction Search & Browse

- `GET /api/v1/auctions/search`
  - Query params: `q`, `status`, `minPrice`, `maxPrice`, `endingBefore`, `endingAfter`, `page`, `size`, `sort`
  - Example:  
    `/api/v1/auctions/search?q=phone&status=OPEN&minPrice=100&endingBefore=2025-12-31T23:59:59&page=0&size=10&sort=endTime,asc`
- `GET /api/v1/auction-items` — List all auctions (paginated)
- `GET /api/v1/auction-items/{id}` — Get auction details
- `GET /api/v1/auction-items/seller/{sellerId}` — List auctions by seller

### Auction Item Management (SELLER)

- `POST /api/v1/seller/auction-items` — Create auction (SELLER only)
- `PUT /api/v1/seller/auction-items/{id}` — Update auction (SELLER only)
- `DELETE /api/v1/seller/auction-items/{id}` — Delete auction (SELLER only)
- `GET /api/v1/seller/auction-items` — List seller's auctions
- `GET /api/v1/seller/auction-items/{id}` — Get seller's auction details
- `GET /api/v1/seller/auction-items/{id}/status` — Get auction status (with anti-sniping info)

### Bidding (BIDDER)

- `POST /api/v1/bids/auction/{auctionItemId}` — Place a bid (BIDDER only)
  - Body: `{ "amount": 123.45 }`
- `GET /api/v1/bids/auction/{auctionItemId}` — List bids for an auction (BIDDER only)
- `GET /api/v1/bids/me` — List user's bids (BIDDER only)

### Watchlist

- `POST /api/v1/watchlist/{auctionItemId}` — Add to watchlist
- `DELETE /api/v1/watchlist/{auctionItemId}` — Remove from watchlist
- `GET /api/v1/watchlist` — List watchlist items (paginated)
- `GET /api/v1/watchlist/is-watching/{auctionItemId}` — Check if watching

### Notifications

- `GET /api/v1/notifications` — List notifications (paginated)
- `GET /api/v1/notifications/unread-count` — Get unread notification count
- `POST /api/v1/notifications/{id}/read` — Mark notification as read
- `POST /api/v1/notifications/mark-read` — Batch mark notifications as read
- `POST /api/v1/notifications/mark-read/all` — Mark all as read

## WebSocket Testing Guide

### Quick Start
1. Open `ws-test.html` in your browser
2. Get JWT token from `/api/v1/auth/login`
3. Enter WebSocket URL: `ws://localhost:8080/ws`
4. Enter Auction ID and JWT
5. Select subscriptions (bid, status, closed)
6. Click "Connect"

### Available Topics
```javascript
/topic/auction/{id}/bid     // New bids
/topic/auction/{id}/status  // Anti-sniping updates
/topic/auction/{id}/closed  // Auction end
/user/queue/outbid         // Personal outbid notifications
```

## Sample Messages

### New Bid
```json
{
  "auctionId": 35,
  "bidId": 42,
  "amount": 300,
  "bidderUsername": "user123",
  "at": "2025-08-20T20:05:10.890Z"
}
```

### Auction Status Update(Anti-sniping)
```json
{
  "auctionId": 35,
  "status": "OPEN",
  "endTime": "2025-08-20T20:07:10.890Z",
  "timeRemainingSeconds": 120,
  "extensionCount": 1
}
```

## Testing
- Run unit tests using:
```
./mvnw test
```
- The test suite (AuctionAppApplicationTests.java) is currently minimal and can be expanded to cover services and controllers.

## Contributing
1. Fork the repository.
2. Create a feature branch (git checkout -b feature/your-feature).
3. Commit your changes (git commit -m "Add your feature").
4. Push to the branch (git push origin feature/your-feature).
5. Open a pull request.