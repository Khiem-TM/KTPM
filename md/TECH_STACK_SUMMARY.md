# 🛠️ Technology Stack & Design Patterns Summary

## 📋 Quick Reference for Presentation

---

## 🔧 **TECHNOLOGY STACK**

### **Backend Core**
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Programming language |
| Spring Boot | 3.3.4 | Application framework |
| Spring Cloud Gateway | 2023.0.3 | API Gateway |
| Spring Data JPA | 3.3.4 | Data access |
| Spring Security | 6.3.3 | Security framework |

### **Databases**
| Technology | Version | Purpose |
|------------|---------|---------|
| PostgreSQL | 16 | Primary database (per service) |
| Flyway | 11.1.0 | Database migrations |
| Elasticsearch | 8.11.0 | Full-text search |

### **Message Broker**
| Technology | Version | Purpose |
|------------|---------|---------|
| Kafka | 7.6.1 | Event streaming |
| Zookeeper | 7.6.1 | Kafka coordination |

### **Storage**
| Technology | Version | Purpose |
|------------|---------|---------|
| MinIO | Latest | S3-compatible object storage |

### **Security**
| Technology | Version | Purpose |
|------------|---------|---------|
| JWT (jjwt) | 0.11.5 | Token-based authentication |
| BCrypt | Built-in | Password hashing |
| RSA | 2048-bit | JWT signing keys |

### **Frontend**
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 19.2.0 | UI framework |
| Axios | 1.13.2 | HTTP client |
| React Scripts | 5.0.1 | Build tooling |

### **DevOps**
| Technology | Version | Purpose |
|------------|---------|---------|
| Docker | Latest | Containerization |
| Docker Compose | Latest | Local orchestration |
| Kubernetes | Latest | Production orchestration |
| Helm | 3 | K8s package manager |
| GitHub Actions | Latest | CI/CD |
| Trivy | Latest | Security scanning |

### **Monitoring**
| Technology | Version | Purpose |
|------------|---------|---------|
| Prometheus | Built-in | Metrics collection |
| Spring Actuator | 3.3.4 | Health checks |
| Micrometer | Built-in | Metrics |

### **Documentation**
| Technology | Version | Purpose |
|------------|---------|---------|
| OpenAPI/Swagger | springdoc 2.6.0 | API documentation |

---

## 🎨 **DESIGN PATTERNS**

### **1. API Gateway Pattern** ✅
**Implementation**: Spring Cloud Gateway
**Purpose**: Single entry point for all services
**Features**:
- Request routing
- JWT validation
- CORS handling
- Load balancing ready

**Code Location**: `services/gateway/`

---

### **2. Database per Service Pattern** ✅
**Implementation**: PostgreSQL per service
**Purpose**: Data isolation and independent scaling
**Services**:
- IAM: `iam-postgres` (port 5434)
- Catalog: `catalog-postgres` (port 5433)
- Borrowing: `borrowing-postgres` (port 5435)

**Benefits**:
- Independent scaling
- Technology flexibility
- Fault isolation

---

### **3. Event-Driven Architecture** ✅
**Implementation**: Kafka message broker
**Purpose**: Async communication between services
**Events**:
- `BookCreated`
- `InventoryAdjusted`
- `LoanCreated`
- `UserRegistered`

**Benefits**:
- Decoupling
- Scalability
- Resilience

**Code Location**: `services/notification/`, `services/catalog/`

---

### **4. API Versioning Pattern** ✅
**Implementation**: URL-based versioning
**Format**: `/api/{service}/v1/{resource}`
**Examples**:
- `/api/iam/v1/auth/login`
- `/api/catalog/v1/books`
- `/api/borrowing/v1/loans`

**Benefits**:
- Backward compatibility
- Gradual migration
- Clear API evolution

---

### **5. Service Discovery Pattern** ⚠️
**Implementation**: Docker DNS (implicit)
**Purpose**: Service location
**Current**: Service names as DNS
**Future**: Eureka or K8s DNS

**Example**:
- `catalog:8081`
- `iam:8082`
- `gateway:8080`

---

### **6. Circuit Breaker Pattern** ⚠️
**Status**: Prepared (Resilience4j dependency)
**Purpose**: Fault tolerance
**Implementation**: Ready for integration
**Use Cases**:
- Service-to-service calls
- External API calls

**Code Location**: `services/admin/` (prepared)

---

### **7. Saga Pattern** ⚠️
**Status**: Prepared for Borrowing service
**Purpose**: Distributed transaction management
**Use Case**: Loan creation with inventory update
**Implementation**: Orchestrator pattern ready

**Code Location**: `services/borrowing/` (prepared)

---

### **8. CQRS Pattern** ⚠️
**Status**: Partial (Admin Service)
**Purpose**: Separate read/write models
**Implementation**: Admin Service aggregates data
**Future**: Full CQRS for complex queries

**Code Location**: `services/admin/`

---

### **9. Outbox Pattern** ⚠️
**Status**: Prepared
**Purpose**: Transactional event publishing
**Implementation**: Ready for integration
**Use Case**: Ensure event delivery after DB commit

---

### **10. Observability Pattern** ✅
**Implementation**: Prometheus + Actuator
**Features**:
- Health checks (`/actuator/health`)
- Metrics (`/actuator/prometheus`)
- Trace ID injection
- JSON structured logs

**Code Location**: All services

---

### **11. Strangler Fig Pattern** ✅
**Implementation**: Gradual migration
**Approach**:
- New services alongside monolithic
- Feature-by-feature migration
- Zero downtime

**Status**: In progress

---

### **12. Backend for Frontend (BFF)** ⚠️
**Status**: Gateway acts as BFF
**Purpose**: API aggregation for frontend
**Implementation**: Gateway routes + aggregation

---

## 📊 **ARCHITECTURE PATTERNS**

### **Microservices Architecture**
- **Style**: Domain-driven design
- **Communication**: REST + Events (Kafka)
- **Data Management**: Database per service
- **Deployment**: Containerized (Docker)

### **Layered Architecture** (per service)
```
Controller Layer
    ↓
Service Layer
    ↓
Repository Layer
    ↓
Database Layer
```

### **Hexagonal Architecture** (prepared)
- Domain entities
- Application services
- Infrastructure adapters

---

## 🔐 **SECURITY PATTERNS**

### **1. JWT Authentication** ✅
- **Algorithm**: RS256 (asymmetric)
- **Key Size**: 2048-bit RSA
- **Token Type**: Access token
- **Expiration**: 3600 seconds

### **2. Password Hashing** ✅
- **Algorithm**: BCrypt
- **Salt Rounds**: 10

### **3. API Security** ✅
- CORS configuration
- JWT validation at Gateway
- Role-based access control (prepared)

---

## 🚀 **DEPLOYMENT PATTERNS**

### **1. Containerization** ✅
- Multi-stage Docker builds
- Optimized image sizes
- Layer caching

### **2. Orchestration** ✅
- Docker Compose (development)
- Kubernetes + Helm (production)

### **3. CI/CD** ✅
- GitHub Actions workflow
- Automated testing
- Security scanning
- Automated deployment

---

## 📈 **PERFORMANCE PATTERNS**

### **1. Connection Pooling** ✅
- HikariCP (default in Spring Boot)
- Optimized pool sizes

### **2. Caching** ⚠️
- Prepared for Redis integration
- Service-level caching ready

### **3. Async Processing** ✅
- Kafka for non-critical operations
- Async endpoints ready

---

## 🔄 **COMMUNICATION PATTERNS**

### **Synchronous**
- REST API calls
- Service-to-service via Gateway
- Direct service calls (internal)

### **Asynchronous**
- Kafka message publishing
- Event-driven workflows
- Background processing

---

## 📝 **SUMMARY TABLE**

| Pattern | Status | Implementation |
|---------|--------|----------------|
| API Gateway | ✅ | Spring Cloud Gateway |
| Database per Service | ✅ | PostgreSQL per service |
| Event-Driven | ✅ | Kafka |
| API Versioning | ✅ | URL-based |
| Service Discovery | ⚠️ | Docker DNS |
| Circuit Breaker | ⚠️ | Prepared (Resilience4j) |
| Saga | ⚠️ | Prepared |
| CQRS | ⚠️ | Partial (Admin) |
| Outbox | ⚠️ | Prepared |
| Observability | ✅ | Prometheus + Actuator |
| Strangler Fig | ✅ | Gradual migration |
| JWT Authentication | ✅ | RS256 |
| Containerization | ✅ | Docker |
| CI/CD | ✅ | GitHub Actions |

**Legend**:
- ✅ = Implemented
- ⚠️ = Prepared/Partial

---

## 🎯 **KEY TAKEAWAYS FOR PRESENTATION**

1. **7 Microservices** with independent databases
2. **API Gateway** for unified entry point
3. **Event-Driven** communication via Kafka
4. **Modern Stack**: Java 21, Spring Boot 3.3.4
5. **Production-Ready**: Docker, K8s, CI/CD
6. **Security**: JWT RS256, CORS, BCrypt
7. **Observability**: Prometheus, Health checks
8. **Scalable**: Horizontal scaling per service

---

**Use this as a quick reference during your presentation! 🚀**

