# My Personal Fuel Tracker

My custom Java application built for tracking vehicle fuel usage with a powerful REST API backend and an easy-to-use command-line interface.

## Project Overview

```
my-fuel-tracker/
├── backend/          # REST API server component
└── cli/              # Terminal-based client application
```

## What This App Does

### Backend System (Core & Advanced)

- Full REST API for vehicle and fuel record management
- Uses in-memory data storage (no external database needed)
- Custom Java Servlet implementation from scratch
- Comprehensive error handling with standard HTTP status codes

### Command Line Tool (Client App)

- Terminal interface that connects to the backend service
- Uses modern Java HTTP client for network communication
- Handles vehicle creation, fuel logging, and performance analytics

## System Prerequisites

- Java 11 or newer
- Maven 3.6+ build tool

## How to Build

### Building the Server

```bash
cd backend
mvn clean package
```

### Building the Client

```bash
cd cli
mvn clean package
```

## Getting Started

### Step 1: Launch the Server

```bash
cd backend
mvn exec:java
```

The server runs on `http://localhost:8080`

### Step 2: Run the Client Tool

Open another terminal window:

```bash
cd cli
mvn exec:java -Dexec.args="<command>"
```

## Available Commands

### Adding a New Vehicle

```bash
mvn exec:java -Dexec.args="create-car --brand Toyota --model Corolla --year 2018"
```

### Recording Fuel Purchase

```bash
mvn exec:java -Dexec.args="add-fuel --carId 1 --liters 40 --price 52.5 --odometer 45000"
```

### Checking Fuel Performance

```bash
mvn exec:java -Dexec.args="fuel-stats --carId 1"
```

Sample result:

```
Total fuel: 120.0 L
Total cost: 155.00
Average consumption: 8.0 L/100km
```

## API Reference

### Main REST Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/cars` | Create a new car |
| GET | `/api/cars` | List all cars |
| GET | `/api/cars/{id}` | Get car by ID |
| POST | `/api/cars/{id}/fuel` | Add fuel entry to a car |
| GET | `/api/cars/{id}/fuel/stats` | Get fuel statistics for a car |

### Legacy Servlet Route

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/servlet/fuel-stats?carId={id}` | Get fuel statistics (manual servlet implementation) |

## API Usage Examples

### Vehicle Registration

**Request:**

```json
POST /api/cars
{
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018
}
```

**Response:**

```json
{
  "id": 1,
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2018,
  "fuelEntries": []
}
```

### Fuel Log Entry

**Request:**

```json
POST /api/cars/1/fuel
{
  "liters": 40,
  "price": 52.5,
  "odometer": 45000
}
```

**Response:**

```json
{
  "id": 1,
  "liters": 40.0,
  "price": 52.5,
  "odometer": 45000.0,
  "timestamp": "2024-01-15T10:30:00"
}
```

### Performance Analytics

**Request:**

```
GET /api/cars/1/fuel/stats
```

**Response:**

```json
{
  "totalFuel": 120.0,
  "totalCost": 155.00,
  "averageConsumption": "8.0 L/100km",
  "totalDistance": 500.0
}
```

## Error Responses

The system provides clear HTTP status codes for different scenarios:

- `200 OK` - Everything worked fine
- `404 Not Found` - Vehicle doesn't exist
- `400 Bad Request` - Invalid input data
- `500 Internal Server Error` - Something went wrong on the server

## Implementation Details

### Server Architecture

- **Web Framework**: Spark Java for lightweight routing
- **Data Handling**: Jackson library for JSON operations
- **Data Storage**: Simple in-memory HashMap (no external DB)
- **Servlet Layer**: Custom HttpServlet built manually

### Client Application

- **Network Layer**: Modern Java HttpClient (Java 11+)
- **Data Processing**: Jackson for JSON serialization
- **Command Parser**: Custom-built argument processing

## Important Notes

- Data is stored in memory only, so it disappears when the server shuts down
- The servlet shows how to handle HTTP requests manually
- Every API response is formatted as JSON
- Cross-origin requests are allowed with CORS

## Project Status

This is my personal fuel tracking project - built for learning and practical use.
