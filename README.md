# Swiftpay-Project
# SwiftPay: Microservices Digital Wallet

SwiftPay is a production-grade, event-driven financial system designed for high auditability and data integrity. It leverages a microservices architecture to handle secure money transfers and real-time transaction logging.

## 🚀 Architecture Highlights
- **Event-Driven:** Uses Apache Kafka for asynchronous communication and decoupling.
- **Data Integrity:** Implements Pessimistic Locking (PostgreSQL) to prevent race conditions.
- **Auditability:** Dedicated Transaction Service using MongoDB for immutable audit trails.
- **Resiliency:** Service discovery via Eureka and centralized routing via Spring Cloud Gateway.

## 🛠 Tech Stack
- **Backend:** Java 17, Spring Boot 3.4.0
- **Databases:** 
  - PostgreSQL (Identity & Wallet - Relational consistency)
  - MongoDB (Transaction History - Scalable audit logs)
- **Messaging:** Apache Kafka (Confluent Docker Image)
- **Infrastructure:** Docker, Netflix Eureka, Spring Cloud Gateway
- **Communication:** OpenFeign (Sync), Kafka (Async)

## 🏗 Service Map

| Service | Port | Responsibility | Database |
| :--- | :--- | :--- | :--- |
| **API Gateway** | 8080 | Central entry point & routing | N/A |
| **Eureka Server**| 8761 | Service Registration & Discovery | N/A |
| **Identity** | 8081 | User management & JWT Auth | PostgreSQL (5434) |
| **Wallet** | 8082 | Balances & Transfer logic | PostgreSQL (5433) |
| **Transaction** | 8083 | Audit logging & History | MongoDB (27017) |

## 🚦 Getting Started (Local Setup)

### 1. Infrastructure
Start the core infrastructure using Docker:
```bash
docker-compose up -d # Runs Kafka, Zookeeper, PostgreSQL, and MongoDB
```

### 2. Startup Sequence
To avoid connection errors, start services in this order:
1. `eureka-server`
2. `api-gateway`
3. `identity-service`
4. `wallet-service`
5. `transaction-service`

### 3. Kafka Topics
The system automatically uses the following topics:
- `user-registration-topic`: Syncs new users from Identity to Wallet.
- `transaction-events`: Syncs audit logs from Wallet to Transaction.

## 🛡 Security & Audit
- **Precision:** Uses `BigDecimal` for all financial calculations.
- **Locking:** Prevents "Double Spend" via PostgreSQL `FOR UPDATE`.
- **Audit:** MongoDB stores every movement of money, including failed attempts.
