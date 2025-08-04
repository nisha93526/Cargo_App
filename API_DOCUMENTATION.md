# API Documentation

## Overview
This document provides detailed information about the CargoPro Backend System REST API endpoints.

## Base URL
```
http://localhost:8080
```

## Authentication
Currently, the API does not require authentication. This can be added in future versions.

## Content Type
All requests and responses use `application/json` content type.

## Error Handling

### Error Response Format
```json
{
  "error": "Error message description",
  "timestamp": "2025-08-04T21:11:11.445Z",
  "status": 400,
  "path": "/api/endpoint"
}
```

### HTTP Status Codes
- `200 OK` - Successful GET, PUT requests
- `201 Created` - Successful POST requests
- `204 No Content` - Successful DELETE requests
- `400 Bad Request` - Invalid request data or business rule violation
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Load Management API

### Create Load
**POST** `/load`

Creates a new cargo load.

**Request Body:**
```json
{
  "shipperId": "SHIP001",
  "loadingPoint": "New York, NY",
  "unloadingPoint": "Los Angeles, CA",
  "loadingDate": "2025-08-10T08:00:00.000Z",
  "unloadingDate": "2025-08-15T17:00:00.000Z",
  "productType": "Electronics",
  "truckType": "Flatbed",
  "noOfTrucks": 2,
  "weight": 15000.50,
  "comment": "Fragile items, handle with care"
}
```

**Response (201 Created):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "shipperId": "SHIP001",
  "loadingPoint": "New York, NY",
  "unloadingPoint": "Los Angeles, CA",
  "loadingDate": "2025-08-10T08:00:00.000Z",
  "unloadingDate": "2025-08-15T17:00:00.000Z",
  "productType": "Electronics",
  "truckType": "Flatbed",
  "noOfTrucks": 2,
  "weight": 15000.50,
  "comment": "Fragile items, handle with care",
  "status": "POSTED",
  "datePosted": "2025-08-04T21:11:11.445Z"
}
```

### Get All Loads
**GET** `/load`

Retrieves all loads with optional filtering and pagination.

**Query Parameters:**
- `shipperId` (optional) - Filter by shipper ID
- `truckType` (optional) - Filter by truck type
- `status` (optional) - Filter by status (POSTED, BOOKED, CANCELLED)
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 10) - Page size

**Example Request:**
```
GET /load?shipperId=SHIP001&status=POSTED&page=0&size=5
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "shipperId": "SHIP001",
      "loadingPoint": "New York, NY",
      "unloadingPoint": "Los Angeles, CA",
      "loadingDate": "2025-08-10T08:00:00.000Z",
      "unloadingDate": "2025-08-15T17:00:00.000Z",
      "productType": "Electronics",
      "truckType": "Flatbed",
      "noOfTrucks": 2,
      "weight": 15000.50,
      "comment": "Fragile items, handle with care",
      "status": "POSTED",
      "datePosted": "2025-08-04T21:11:11.445Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "pageNumber": 0,
    "pageSize": 5,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "size": 5,
  "number": 0,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "empty": false
}
```

### Get Load by ID
**GET** `/load/{loadId}`

Retrieves a specific load by its ID.

**Path Parameters:**
- `loadId` - UUID of the load

**Response (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "shipperId": "SHIP001",
  "loadingPoint": "New York, NY",
  "unloadingPoint": "Los Angeles, CA",
  "loadingDate": "2025-08-10T08:00:00.000Z",
  "unloadingDate": "2025-08-15T17:00:00.000Z",
  "productType": "Electronics",
  "truckType": "Flatbed",
  "noOfTrucks": 2,
  "weight": 15000.50,
  "comment": "Fragile items, handle with care",
  "status": "POSTED",
  "datePosted": "2025-08-04T21:11:11.445Z"
}
```

### Update Load
**PUT** `/load/{loadId}`

Updates an existing load.

**Path Parameters:**
- `loadId` - UUID of the load

**Request Body:**
```json
{
  "loadingPoint": "Boston, MA",
  "unloadingPoint": "Seattle, WA",
  "productType": "Machinery",
  "truckType": "Lowboy",
  "noOfTrucks": 1,
  "weight": 25000.00,
  "comment": "Heavy machinery transport"
}
```

**Response (200 OK):**
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "shipperId": "SHIP001",
  "loadingPoint": "Boston, MA",
  "unloadingPoint": "Seattle, WA",
  "loadingDate": "2025-08-10T08:00:00.000Z",
  "unloadingDate": "2025-08-15T17:00:00.000Z",
  "productType": "Machinery",
  "truckType": "Lowboy",
  "noOfTrucks": 1,
  "weight": 25000.00,
  "comment": "Heavy machinery transport",
  "status": "POSTED",
  "datePosted": "2025-08-04T21:11:11.445Z"
}
```

### Delete Load
**DELETE** `/load/{loadId}`

Cancels a load (sets status to CANCELLED).

**Path Parameters:**
- `loadId` - UUID of the load

**Response (204 No Content)**

## Booking Management API

### Create Booking
**POST** `/booking`

Creates a new booking for a load.

**Request Body:**
```json
{
  "loadId": "123e4567-e89b-12d3-a456-426614174000",
  "transporterId": "TRANS001",
  "proposedRate": 5000.00,
  "comment": "Experienced with electronics transport"
}
```

**Response (201 Created):**
```json
{
  "id": "987fcdeb-51a2-43d1-9c4f-123456789abc",
  "loadId": "123e4567-e89b-12d3-a456-426614174000",
  "transporterId": "TRANS001",
  "proposedRate": 5000.00,
  "comment": "Experienced with electronics transport",
  "status": "PENDING",
  "requestedAt": "2025-08-04T21:15:30.123Z"
}
```

### Get All Bookings
**GET** `/booking`

Retrieves all bookings with optional filtering and pagination.

**Query Parameters:**
- `loadId` (optional) - Filter by load ID
- `transporterId` (optional) - Filter by transporter ID
- `status` (optional) - Filter by status (PENDING, ACCEPTED, REJECTED)
- `page` (optional, default: 0) - Page number
- `size` (optional, default: 10) - Page size

### Get Booking by ID
**GET** `/booking/{bookingId}`

Retrieves a specific booking by its ID.

### Update Booking
**PUT** `/booking/{bookingId}`

Updates a booking status (accept or reject).

**Request Body:**
```json
{
  "status": "ACCEPTED"
}
```

### Delete Booking
**DELETE** `/booking/{bookingId}`

Deletes a booking.

## Business Rules

### Load Status Transitions
- `POSTED` → `BOOKED` (when first booking is created)
- `BOOKED` → `POSTED` (when all bookings are deleted/rejected)
- `Any Status` → `CANCELLED` (when load is deleted)

### Booking Validation
- Cannot create bookings for loads with status `CANCELLED`
- Booking status can only be updated to `ACCEPTED` or `REJECTED`

## Rate Limiting
Currently, no rate limiting is implemented. Consider implementing rate limiting for production use.

## Versioning
The API currently does not use versioning. Future versions may include `/v1/` prefix.
