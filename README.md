# Load Balancer Microservice

This is a Spring Boot-based microservice that acts as a load balancer. It allows you to register backend servers, deregister them, set load balancing strategies, and forward client requests to the selected backend server.

## Features
- **Backend Server Management**: Register and deregister backend servers.
- **Load Balancing Strategies**: Supports `Random` and `Round Robin` strategies.
- **Request Forwarding**: Forwards client requests to the selected backend server and relays the response back to the client.
- **Dynamic Strategy Switching**: Change the load balancing strategy at runtime.

## Prerequisites
- Java 17 or higher
- Maven 3.8 or higher
- Spring Boot 3.x
- A REST client (e.g., Postman) for testing APIs

## Setup Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/prakhar9837/loadbalancer.git
   cd loadbalancer
   mvn clean install

## Run the application:  
- mvn spring-boot:run
- The application will start on http://localhost:8080.  

## API Endpoints
- **Register a Backend Server**
   ```
   URL: /loadbalancer/register
   Method: POST
   Request Body:
   {
   "url": "http://localhost:8081",
   "active": true
   }
   Response: 200 OK - Backend server registered successfully.
  ```
- **Deregister a Backend Server**
   ```
   URL: /loadbalancer/deregister
   Method: DELETE
   Query Parameter: url (e.g., ?url=http://localhost:8081)
   Response: 200 OK - Backend server deregistered successfully.
   ```
- **Set Load Balancing Strategy**
   ```
   URL: /loadbalancer/strategy
   Method: GET
   Query Parameter: strategyName (e.g., ?strategyName=random or ?strategyName=roundrobin)
   Response: 200 OK - Load balancing strategy set successfully.
    ```
- Get All Backend Servers
   ```
   URL: /loadbalancer/server/all
   Method: GET
   Response: 200 OK - List of all registered backend servers.
    ```
- Forward a Request
   ```
   URL: /loadbalancer/forward
   Method: Supports GET, POST, PUT, DELETE
   Query Parameter: path (e.g., ?path=/api/resource)
   Headers: Pass any required headers.
   Request Body: Optional, depending on the HTTP method.
   Response: 202 Accepted - Request successfully forwarded to the backend server.
   Load Balancing Strategies
   Random: Selects a backend server randomly.
   Round Robin: Selects backend servers in a circular order.
   Error Handling
   Returns appropriate HTTP status codes and error messages for invalid requests or server errors.
    ```