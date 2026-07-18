🏡 Airbnb Clone Backend

A production-inspired Airbnb backend built with Java 21, Spring Boot 3.5, PostgreSQL, Spring Security, JWT, and Stripe Checkout.

This project implements the core backend of an Airbnb-like hotel booking platform with secure authentication, hotel and room management, dynamic inventory, pricing strategies, booking lifecycle management, payment processing, and reporting.

The application follows a clean layered architecture and RESTful API design with DTO mapping, centralized exception handling, validation, and comprehensive testing.

⸻

✨ Features

🔐 Authentication & Security

* User Signup
* User Login
* JWT Authentication
* Refresh Token API
* BCrypt Password Encryption
* Spring Security
* Custom UserDetailsService
* Custom UserPrincipal
* Role-Based Authorization (RBAC)
* Protected REST APIs

⸻

👤 User Management

* Update Profile
* View My Bookings
* Secure authenticated endpoints›

⸻

🏨 Hotel Management

Hotel owners can:

* Create Hotel
* Update Hotel
* Delete Hotel
* View Hotel Details
* View All Owned Hotels
* Activate Hotel
* Hotel Reports
* Hotel Booking History

⸻

🛏️ Room Management

* Add Rooms
* Update Room Details
* Delete Rooms
* View Hotel Rooms
* Automatic Inventory Initialization for Active Hotels

⸻

📅 Inventory Management

The inventory module supports:

* 365-day inventory generation
* Date-range inventory updates
* Room availability management
* Closed/Open inventory
* Dynamic room pricing
* Bulk inventory updates
* Concurrency-safe inventory updates using database locking

⸻

💰 Dynamic Pricing Engine

The project implements a Strategy Pattern for pricing.

Available pricing strategies include:

* Base Pricing
* Surge Pricing
* Occupancy Pricing
* Urgency Pricing

The final room price is calculated dynamically based on pricing rules.

Whenever the room’s base price changes, future inventory prices are automatically synchronized.

⸻

🔍 Hotel Search

Search hotels by:

* City
* Check-in Date
* Check-out Date
* Number of Rooms

The search returns only hotels with complete inventory availability for the requested stay.

⸻

📖 Booking Management

Supports the complete booking lifecycle:

* Initialize Booking
* Add Guests
* Calculate Booking Price
* Stripe Checkout
* Payment Confirmation
* Booking Cancellation
* Booking Status Updates
* Booking History

Includes ownership validation and inventory consistency checks to prevent overbooking.

⸻

💳 Stripe Payment Integration

Integrated with Stripe Checkout.

Features:

* Checkout Session Creation
* Secure Payment Processing
* Stripe Webhooks
* Payment Status Tracking
* Automatic Booking Confirmation
* Inventory Synchronization
* Failed Payment Handling

⸻

📊 Reporting

Hotel owners can retrieve reports including:

* Total Bookings
* Confirmed Bookings
* Total Revenue
* Average Revenue per Booking

⸻

🏗️ Project Architecture

Client
   │
REST API
   │
Controllers
   │
Services
   │
Repositories
   │
PostgreSQL Database

The project follows a layered architecture consisting of:

* Controller Layer
* Service Layer
* Repository Layer
* DTO Layer
* Entity Layer
* Security Layer
* Strategy Layer
* Exception Handling Layer
* Utility Layer

⸻

📂 Project Structure

src/main/java
│
├── advice
├── config
├── controller
├── dto
├── entity
│   └── enums
├── exception
├── repository
├── security
├── service
├── strategy
└── util

⸻

🚀 REST APIs

Authentication

* POST /auth/signup
* POST /auth/login
* POST /auth/refresh

⸻

Hotels

* POST /admin/hotels
* GET /admin/hotels
* GET /admin/hotels/{hotelId}
* PUT /admin/hotels/{hotelId}
* DELETE /admin/hotels/{hotelId}

⸻

Rooms

* POST /admin/hotels/{hotelId}/rooms
* GET /admin/hotels/{hotelId}/rooms
* GET /admin/hotels/{hotelId}/rooms/{roomId}
* PUT /admin/hotels/{hotelId}/rooms/{roomId}
* DELETE /admin/hotels/{hotelId}/rooms/{roomId}

⸻

Inventory

* GET /admin/inventory/rooms/{roomId}

⸻

Browse Hotels

* GET /hotels/search
* GET /hotels/{hotelId}/info

⸻

Booking

* POST /bookings/init
* POST /bookings/{bookingId}/addGuests
* POST /bookings/{bookingId}/payments
* POST /bookings/{bookingId}/cancel
* POST /bookings/{bookingId}/status

⸻

Users

* GET /users/myBookings

⸻

Webhook

* POST /webhook/payment

⸻

🛠️ Tech Stack

Backend

* Java 21
* Spring Boot 3.5
* Spring MVC
* Spring Data JPA
* Hibernate

Security

* Spring Security
* JWT
* BCrypt

Database

* PostgreSQL

Payment Gateway

* Stripe Checkout
* Stripe Webhooks

API Documentation

* Swagger / OpenAPI

Utilities

* Lombok
* ModelMapper

Build Tool

* Maven

⸻

🧪 Testing

The project includes:

* Unit Testing
* Repository Testing
* Service Testing
* Controller Testing
* Integration Testing
* Testcontainers Support
* Mockito
* JUnit 5

⸻

🛡️ Exception Handling

Centralized exception handling using:

* GlobalExceptionHandler
* Custom Exceptions
* Standard API Response Wrapper
* Validation Error Responses

⸻

📚 Validation

Request validation implemented using:

* Jakarta Bean Validation
* DTO Validation
* Custom Validation Messages

⸻

🔮 Future Enhancements

* Docker Support
* Redis Caching
* AWS Deployment
* CI/CD Pipeline
* Email Notifications
* Elasticsearch
* Kafka Event Streaming
* Microservices Architecture
* Admin Dashboard
* Analytics Dashboard

⸻

🎯 What I Learned

This project helped me gain hands-on experience with:

* Spring Boot
* Spring Security
* JWT Authentication
* REST API Design
* Hibernate & JPA
* PostgreSQL
* Stripe Payment Gateway
* DTO Mapping
* Global Exception Handling
* Bean Validation
* Strategy Design Pattern
* Repository Pattern
* Layered Architecture
* Testing with JUnit & Mockito
* Testcontainers
* Clean Code Practices

⸻

👩‍💻 Author

Shubhangi Rajput

Backend Engineer passionate about building scalable backend systems using Java, Spring Boot, REST APIs, SQL, Spring Security, and modern backend architecture.

* GitHub: https://github.com/shubhangi-rajput-dev

⸻

⭐ Support

If you found this project helpful or learned something from it, consider giving it a ⭐ on GitHub. It motivates me to build and share more production-ready backend projects.
