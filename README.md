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
