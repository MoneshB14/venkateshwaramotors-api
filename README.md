# Venkateswara Motors - Service Center Management System

A comprehensive Spring Boot application for managing automotive service center operations, built with modern web technologies and featuring both public website booking and internal management systems.

## 🚗 Overview

Venkateswara Motors is a full-featured service center management system designed to streamline automotive service operations. The application provides two main interfaces:

1. **Public Website Booking** - Allows customers to book services online
2. **Service Center Management** - Internal dashboard for managing bookings, inventory, users, and operations

## 🛠️ Technology Stack

- **Backend Framework**: Spring Boot 3.5.4
- **Database**: MongoDB with Spring Data MongoDB
- **Caching**: Redis
- **Security**: Spring Security with JWT authentication
- **Email Service**: Custom email API integration
- **PDF Generation**: OpenHTML to PDF with JSoup
- **Java Version**: 17
- **Build Tool**: Maven

### Key Dependencies

- Spring Boot Web & WebFlux
- Spring Data MongoDB
- Spring Security
- Spring Data Redis
- JWT (JSON Web Tokens)
- Lombok for code generation
- Validation API
- OpenHTML PDF generation
- JSoup HTML parser

## 🏗️ Architecture

The application follows a modular architecture with clear separation of concerns:

```
com.monesh.venkateswaramotors/
├── config/           # Configuration classes
├── constants/        # Application constants
├── features/         # Feature modules
│   ├── vmoffice/     # Office management features
│   └── vmservice/    # Service-related features
│       ├── servicecenter/    # Internal service center features
│       └── websitebooking/   # Public booking features
├── global/           # Global services and controllers
├── utils/            # Utility classes
└── VenkateswaramotorsApplication.java
```

## 🔧 Features

### 🌐 Public Website Booking (`/website-booking`)

- **Service Booking**: Public API for customers to book services
- **Available Timings**: Check available appointment slots
- **Email Notifications**: Automatic booking confirmation emails
- **Validation**: Comprehensive input validation for booking data

**Key Endpoints:**
- `POST /website-booking/book-service` - Book a service
- `GET /website-booking/available-timings` - Get available time slots

### 🏢 Service Center Management (`/service-center`)

#### 🔐 Authentication System (`/service-center/auth`)
- **OTP-based Login**: Secure email-based OTP authentication
- **User Registration**: Admin-controlled user signup
- **Session Management**: Cookie-based authentication
- **Role-based Access**: Different access levels for users

**Key Endpoints:**
- `POST /service-center/auth/signup` - Register new user
- `POST /service-center/auth/login` - Request OTP for login
- `POST /service-center/auth/verify-otp` - Verify OTP and login
- `GET /service-center/auth/status` - Check authentication status

#### 📅 Booking Management (`/service-center/bookings`)
- **CRUD Operations**: Complete booking lifecycle management
- **Search & Filter**: Advanced booking search capabilities
- **Status Tracking**: Multiple booking statuses (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- **Technician Assignment**: Assign bookings to specific technicians
- **Date Range Queries**: Filter bookings by date ranges

**Key Features:**
- Pagination support for large datasets
- Search by customer name or vehicle registration
- Filter by service type, status, and date
- Available timings management

#### 💰 Billing System (`/service-center/bookings/bills`)
- **Bill Creation**: Generate bills for completed services
- **PDF Generation**: Convert HTML bills to PDF format
- **Bill Tracking**: Track bills by booking ID or bill number
- **Custom Styling**: Support for custom CSS in PDF generation

#### 📦 Inventory Management (`/service-center/inventory`)
- **Item Management**: Complete CRUD operations for inventory items
- **Stock Tracking**: Real-time stock level monitoring
- **Low Stock Alerts**: Automatic low stock notifications
- **Supplier Management**: Comprehensive supplier database
- **Transaction History**: Track all inventory movements
- **Categories & Status**: Organize items by category and status

**Key Features:**
- Stock adjustment workflows
- Supplier geographic filtering (city, state)
- Specialization-based supplier search
- Inventory statistics and reporting

#### 👥 User Management (`/service-center/user-management`)
- **User CRUD**: Complete user lifecycle management
- **Role Management**: Assign and modify user roles
- **Account Control**: Enable/disable and lock/unlock accounts
- **User Statistics**: Comprehensive user analytics

#### 👤 Customer Management (`/service-center/customers`)
- **Customer History**: Complete service history by vehicle registration
- **Customer Search**: Search customers across the database
- **Service Tracking**: Track all services performed for each customer

#### 📊 Dashboard & Overview (`/service-center/overview`)
- **Dashboard Statistics**: Real-time service center metrics
- **Booking Overview**: Today's bookings and status summaries
- **Performance Metrics**: Service completion rates and trends

## 🔒 Security Features

### Authentication Methods
1. **JWT-based Authentication**: For API access
2. **Cookie-based Authentication**: For web interface
3. **OTP Verification**: Email-based one-time passwords

### Authorization Levels
- **Public Access**: Website booking endpoints
- **Authenticated Users**: Service center management
- **Admin Only**: User management and inventory control

### Security Configuration
- CORS support for multiple frontend origins
- CSRF protection disabled for API usage
- Stateless session management
- Secure cookie configuration

## 📧 Email Integration

The application integrates with an external email service for notifications:

- **Service Booking Confirmations**
- **OTP Delivery**
- **Reminder Notifications**
- **General Communications**

Email templates are managed through constants and support dynamic content injection.

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MongoDB instance
- Redis server

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd venkateswaramotors
```

2. **Configure the application**
Update `src/main/resources/application.yml`:
```yaml
spring:
  data:
    mongodb:
      uri: your-mongodb-connection-string
      database: venkateswaramotors
    redis:
      host: your-redis-host
      port: 6379

email:
  api:
    url: your-email-service-url
    endpoint: /send-email
```

3. **Build the application**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run
```

The application will start on port 8888 with context path `/vm/api`.

### Testing
Run the test suite:
```bash
mvn test
```

## 📝 API Documentation

### Base URL
```
http://localhost:8888/vm/api
```

### Authentication
Most service center endpoints require authentication. Include the authentication cookie or JWT token in your requests.

### Response Format
All API responses follow a consistent format:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {...}
}
```

## 🏭 Production Deployment

### Environment Configuration
- Set appropriate MongoDB and Redis connection strings
- Configure email service endpoints
- Enable HTTPS and update cookie security settings
- Set up proper logging levels

### Docker Support
The application can be containerized using the provided Maven plugin configuration.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the terms specified in the project configuration.

## 🔧 Development Notes

### Code Organization
- **DTOs**: Data Transfer Objects for API communication
- **Entities**: MongoDB document models
- **Services**: Business logic implementation
- **Controllers**: REST API endpoints
- **Repositories**: Data access layer

### Key Design Patterns
- **Repository Pattern**: For data access abstraction
- **Service Layer Pattern**: For business logic separation
- **DTO Pattern**: For API data transfer
- **Builder Pattern**: Using Lombok for object construction

### Testing
- Unit tests for service layer
- Integration tests for repositories
- Controller tests for API endpoints

## 📞 Support

For support or questions about this application, please contact the development team or create an issue in the repository.

---

**Venkateswara Motors** - Streamlining automotive service operations with modern technology.
