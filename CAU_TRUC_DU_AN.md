# 📁 Cấu Trúc Dự Án BookVault Microservices

## 🏗️ Tổng Quan Kiến Trúc

```
KTPM/
├── 📂 services/          # Microservices
├── 📂 platform/          # Kubernetes/Helm charts
├── 📂 common/            # Shared libraries
├── 📂 contracts/         # API contracts
├── 📂 config/            # Configuration files
├── 📂 database/          # Database scripts
├── 📂 libs/              # External libraries
├── 📂 asset/             # Frontend assets
├── 📄 docker-compose.yml # Docker orchestration
└── 📄 pom.xml            # Parent Maven project
```

---

## 🔧 Microservices (`/services`)

### 1. **Gateway Service** (Port 8080)
**Chức năng**: API Gateway, routing, JWT validation

```
services/gateway/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/com/scar/bookvault/gateway/
    │   ├── GatewayServiceApplication.java
    │   └── security/
    │       └── GatewaySecurityConfig.java
    └── resources/
        └── application.yml
```

**Dependencies**: 
- Catalog, IAM, Borrowing, Search, Notification, Media, Admin

---

### 2. **IAM Service** (Port 8082)
**Chức năng**: Authentication & Authorization, JWT management

```
services/iam/
├── Dockerfile
├── pom.xml
├── openapi.yaml
└── src/main/
    ├── java/com/scar/bookvault/iam/
    │   ├── IamServiceApplication.java
    │   ├── auth/
    │   │   ├── AuthController.java
    │   │   └── JwtService.java
    │   ├── security/
    │   │   └── SecurityConfiguration.java
    │   └── user/
    │       ├── User.java
    │       └── UserRepository.java
    └── resources/
        ├── application.yml
        └── db/migration/
            └── V1__init.sql
```

**Database**: PostgreSQL (iam-postgres:5432)

---

### 3. **Catalog Service** (Port 8081)
**Chức năng**: Book management (CRUD operations)

```
services/catalog/
├── Dockerfile
├── pom.xml
├── openapi.yaml
└── src/main/
    ├── java/com/scar/bookvault/catalog/
    │   ├── CatalogServiceApplication.java
    │   └── book/
    │       ├── Book.java
    │       ├── BookController.java
    │       ├── BookRepository.java
    │       └── BookService.java
    └── resources/
        ├── application.yml
        └── db/migration/
            └── V1__init.sql
```

**Database**: PostgreSQL (catalog-postgres:5432)

---

### 4. **Borrowing Service** (Port 8083)
**Chức năng**: Loan management, borrowing operations

```
services/borrowing/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/com/scar/bookvault/borrowing/
    │   ├── BorrowingServiceApplication.java
    │   ├── api/
    │   │   └── LoanController.java
    │   └── domain/
    │       ├── Loan.java
    │       └── LoanRepository.java
    └── resources/
        ├── application.yml
        └── db/migration/
            └── V1__init.sql
```

**Database**: PostgreSQL (borrowing-postgres:5432)  
**Dependencies**: Catalog Service

---

### 5. **Search Service** (Port 8084)
**Chức năng**: Full-text search using Elasticsearch

```
services/search/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/com/scar/bookvault/search/
    │   ├── SearchServiceApplication.java
    │   ├── api/
    │   │   └── SearchController.java
    │   ├── domain/
    │   │   └── BookDocument.java
    │   └── repository/
    │       └── BookSearchRepository.java
    └── resources/
        └── application.yml
```

**External Service**: Elasticsearch (port 9200)

---

### 6. **Notification Service** (Port 8085)
**Chức năng**: Email notifications, event handling

```
services/notification/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/com/scar/bookvault/notification/
    │   ├── NotificationServiceApplication.java
    │   ├── api/
    │   │   └── NotificationController.java
    │   ├── config/
    │   │   └── ObjectMapperConfig.java
    │   └── service/
    │       ├── EmailService.java
    │       └── NotificationService.java
    └── resources/
        └── application.yml
```

**Message Broker**: Kafka (port 29092)

---

### 7. **Media Service** (Port 8086)
**Chức năng**: File storage, upload/download

```
services/media/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/com/scar/bookvault/media/
    │   ├── MediaServiceApplication.java
    │   ├── api/
    │   │   └── MediaController.java
    │   ├── config/
    │   │   └── MinioConfig.java
    │   └── service/
    │       └── MediaService.java
    └── resources/
        └── application.yml
```

**Storage**: MinIO (ports 9000, 9001)

---

### 8. **Admin Service** (Port 8087)
**Chức năng**: Dashboard, reports, statistics

```
services/admin/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/com/scar/bookvault/admin/
    │   ├── AdminServiceApplication.java
    │   ├── api/
    │   │   └── AdminController.java
    │   └── service/
    │       └── AdminService.java
    └── resources/
        └── application.yml
```

**Dependencies**: Catalog, Borrowing

---

## 🚀 Platform & Deployment (`/platform`)

### Helm Charts for Kubernetes

```
platform/helm/
├── admin/
│   ├── Chart.yaml
│   ├── values.yaml
│   └── templates/
│       ├── deployment.yaml
│       └── service.yaml
├── borrowing/
├── catalog/
├── gateway/
├── iam/
├── media/
├── notification/
└── search/
```

---

## 🗄️ Infrastructure Services

### Databases
- **catalog-postgres**: PostgreSQL 16 (port 5433)
- **iam-postgres**: PostgreSQL 16 (port 5434)
- **borrowing-postgres**: PostgreSQL 16 (port 5435)

### Message Broker
- **Kafka**: Confluent Kafka 7.5.0 (port 29092)
- **Zookeeper**: Bitnami Zookeeper 3.9 (port 2181)

### Storage & Search
- **Elasticsearch**: 8.11.0 (port 9200)
- **MinIO**: Latest (ports 9000, 9001)

---

## 📦 Cấu Trúc Mỗi Service

### Pattern Chuẩn

```
service-name/
├── Dockerfile                    # Container build
├── pom.xml                       # Maven dependencies
├── openapi.yaml                  # API specification (optional)
└── src/main/
    ├── java/com/scar/bookvault/{service}/
    │   ├── {Service}Application.java    # Main class
    │   ├── api/                         # REST controllers
    │   ├── domain/                      # Entities, repositories
    │   ├── service/                     # Business logic
    │   ├── config/                      # Configuration classes
    │   └── dto/                         # Data transfer objects
    └── resources/
        ├── application.yml              # Service config
        └── db/migration/                # Flyway migrations (if DB)
            └── V1__init.sql
```

---

## 🔗 Service Communication

### Synchronous (HTTP/REST)
- Gateway → All Services
- Borrowing → Catalog
- Admin → Catalog, Borrowing

### Asynchronous (Kafka)
- Borrowing → Notification (loan events)
- Catalog → Search (book indexing)

---

## 📊 Port Mapping

| Service | Internal Port | External Port | Protocol |
|---------|--------------|---------------|----------|
| Gateway | 8080 | 8080 | HTTP |
| Catalog | 8081 | 8081 | HTTP |
| IAM | 8082 | 8082 | HTTP |
| Borrowing | 8083 | 8083 | HTTP |
| Search | 8084 | 8084 | HTTP |
| Notification | 8085 | 8085 | HTTP |
| Media | 8086 | 8086 | HTTP |
| Admin | 8087 | 8087 | HTTP |
| PostgreSQL (Catalog) | 5432 | 5433 | TCP |
| PostgreSQL (IAM) | 5432 | 5434 | TCP |
| PostgreSQL (Borrowing) | 5432 | 5435 | TCP |
| Elasticsearch | 9200 | 9200 | HTTP |
| MinIO | 9000 | 9000 | HTTP |
| MinIO Console | 9001 | 9001 | HTTP |
| Kafka | 9092 | 29092 | TCP |
| Zookeeper | 2181 | 2181 | TCP |

---

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.3.4
- **Java**: JDK 21
- **Build Tool**: Maven 3.9+
- **Database**: PostgreSQL 16
- **ORM**: JPA/Hibernate
- **Migration**: Flyway

### Infrastructure
- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **Container Registry**: Local (pull_policy: never)
- **Kubernetes**: Helm charts available

### External Services
- **Search**: Elasticsearch 8.11.0
- **Storage**: MinIO
- **Message Broker**: Apache Kafka 3.6.0
- **Coordination**: Zookeeper 3.9

### Security
- **Authentication**: JWT (RS256)
- **Authorization**: Spring Security

---

## 📝 Configuration Files

### Root Level
- `docker-compose.yml` - Full stack orchestration
- `docker-compose.minimal.yml` - Core services only
- `docker-compose.simple.yml` - Simplified version
- `pom.xml` - Parent Maven project

### Service Level
- `application.yml` - Service configuration
- `Dockerfile` - Container build instructions
- `pom.xml` - Service dependencies

---

## 🎯 Development Workflow

1. **Build**: `docker compose build`
2. **Start**: `docker compose up -d`
3. **Logs**: `docker compose logs -f [service-name]`
4. **Stop**: `docker compose down`

---

## 📚 Documentation Files

- `MICROSERVICES_README.md` - Main documentation
- `CACH_CHAY_NHANH.md` - Quick start guide
- `QUICK_START.md` - Detailed quick start
- `START_CORE_SERVICES.md` - Core services guide
- `TROUBLESHOOTING.md` - Troubleshooting guide
- `PROJECT_ASSESSMENT.md` - Project assessment
- `CAU_TRUC_DU_AN.md` - This file

---

## 🔄 Data Flow

```
Client Request
    ↓
Gateway (8080) - JWT Validation, Routing
    ↓
┌─────────────────────────────────────────┐
│  Services (8081-8087)                   │
│  - Catalog (Books)                      │
│  - IAM (Auth)                           │
│  - Borrowing (Loans)                    │
│  - Search (Elasticsearch)               │
│  - Notification (Kafka → Email)         │
│  - Media (MinIO)                       │
│  - Admin (Stats)                        │
└─────────────────────────────────────────┘
    ↓
Databases (PostgreSQL) / External Services
```

---

## 🎨 Frontend Assets

```
asset/
├── frontend/
│   ├── admin/          # Admin dashboard assets
│   └── user/           # User interface assets
```

---

## 📌 Key Features

✅ **Microservices Architecture** - 8 independent services  
✅ **API Gateway** - Single entry point  
✅ **JWT Authentication** - Secure token-based auth  
✅ **Database per Service** - Data isolation  
✅ **Event-Driven** - Kafka for async communication  
✅ **Full-Text Search** - Elasticsearch integration  
✅ **File Storage** - MinIO S3-compatible storage  
✅ **Containerized** - Docker for all services  
✅ **Kubernetes Ready** - Helm charts available  
✅ **Database Migrations** - Flyway for version control  

---

## 🚦 Health Checks

All services expose health endpoints:
- `http://localhost:{port}/actuator/health`

---

## 📖 API Documentation

- **Swagger UI**: Available at `http://localhost:{port}/swagger-ui`
- **OpenAPI**: Defined in `openapi.yaml` (Catalog, IAM)

---

*Last Updated: Generated from project structure*

