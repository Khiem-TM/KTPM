# 📋 Báo Cáo Refactor Microservices - BookVault

## 🎯 Tổng Quan Dự Án

Dự án đã được refactor từ kiến trúc monolithic Spring Boot sang **kiến trúc microservices** theo Domain-driven design (DDD), với các service độc lập, database riêng, và giao tiếp qua API Gateway và message broker.

**Ngày hoàn thành**: 2024  
**Phiên bản**: 0.0.1-SNAPSHOT  
**Java**: 21  
**Spring Boot**: 3.3.4

---

## 📁 Cấu Trúc Thư Mục Đã Tạo

```
BookVault-main/
├── services/                    # Microservices
│   ├── catalog/                 # Catalog Service (quản lý sách)
│   ├── iam/                     # IAM Service (xác thực & phân quyền)
│   └── gateway/                 # Gateway Service (API Gateway)
├── platform/
│   └── helm/                    # Helm charts cho Kubernetes
│       ├── catalog/
│       ├── iam/
│       └── gateway/
├── config/                      # Cấu hình tập trung
├── libs/                        # Thư viện dùng chung
├── common/                      # Code dùng chung
├── contracts/                   # API contracts/interfaces
├── .github/
│   └── workflows/
│       └── build-deploy.yml     # CI/CD pipeline
└── docker-compose.yml           # Docker Compose cho local dev
```

---

## 🏗️ Các Service Đã Triển Khai

### 1. **IAM Service** (Identity & Access Management)

**Port**: 8082  
**Database**: PostgreSQL (riêng biệt)  
**Chức năng**:
- Đăng ký user (`POST /api/iam/v1/auth/register`)
- Đăng nhập (`POST /api/iam/v1/auth/login`)
- JWT token generation (RS256)
- Phân quyền USER/ADMIN
- Public key endpoint (`GET /api/iam/v1/auth/public-key`)

**Công nghệ**:
- Spring Boot 3.3.4 + Spring Security
- JWT (jjwt 0.11.5) với RS256
- PostgreSQL + Flyway migrations
- BCrypt password hashing

**Files đã tạo**:
```
services/iam/
├── pom.xml
├── Dockerfile
├── src/main/java/com/scar/bookvault/iam/
│   ├── IamServiceApplication.java
│   ├── security/SecurityConfiguration.java
│   ├── user/User.java
│   ├── user/UserRepository.java
│   ├── auth/JwtService.java
│   └── auth/AuthController.java
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/V1__init.sql
└── openapi.yaml
```

**Database Schema**:
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) DEFAULT 'USER'
);
```

---

### 2. **Catalog Service** (Quản Lý Sách)

**Port**: 8081  
**Database**: PostgreSQL (riêng biệt)  
**Chức năng**:
- CRUD sách (`GET/POST/PUT/DELETE /api/catalog/v1/books`)
- Quản lý tồn kho (quantity)
- Validation ISBN unique

**Công nghệ**:
- Spring Boot 3.3.4 + Spring Data JPA
- PostgreSQL + Flyway
- OpenAPI/Swagger documentation
- Actuator + Prometheus metrics

**Files đã tạo**:
```
services/catalog/
├── pom.xml
├── Dockerfile
├── src/main/java/com/scar/bookvault/catalog/
│   ├── CatalogServiceApplication.java
│   └── book/
│       ├── Book.java
│       ├── BookRepository.java
│       ├── BookService.java
│       └── BookController.java
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/V1__init.sql
└── openapi.yaml
```

**Database Schema**:
```sql
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(64) UNIQUE NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);
```

---

### 3. **Gateway Service** (API Gateway)

**Port**: 8080  
**Chức năng**:
- Entry point cho toàn bộ hệ thống
- Route requests đến các service:
  - `/api/iam/**` → IAM Service (8082)
  - `/api/catalog/**` → Catalog Service (8081)
- JWT validation (RS256) với public key từ IAM
- Trace ID injection (X-Trace-Id header)
- Actuator + Prometheus metrics

**Công nghệ**:
- Spring Cloud Gateway
- Spring Security OAuth2 Resource Server
- JWT validation với NimbusReactiveJwtDecoder

**Files đã tạo**:
```
services/gateway/
├── pom.xml
├── Dockerfile
├── src/main/java/com/scar/bookvault/gateway/
│   ├── GatewayServiceApplication.java
│   └── security/GatewaySecurityConfig.java
└── src/main/resources/
    └── application.yml
```

**Route Configuration**:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: iam
          uri: http://iam:8082
          predicates:
            - Path=/api/iam/**
        - id: catalog
          uri: http://catalog:8081
          predicates:
            - Path=/api/catalog/**
```

---

## 🐳 Docker Compose

**File**: `docker-compose.yml`

**Services**:
1. **catalog-postgres**: PostgreSQL cho Catalog Service (port 5433)
2. **iam-postgres**: PostgreSQL cho IAM Service (port 5434)
3. **catalog**: Catalog Service (port 8081)
4. **iam**: IAM Service (port 8082)
5. **gateway**: Gateway Service (port 8080)
6. **zookeeper**: Zookeeper cho Kafka
7. **kafka**: Kafka message broker (port 29092)

**Health Checks**: Đã cấu hình health checks cho PostgreSQL để đảm bảo services khởi động đúng thứ tự.

**Network**: Tất cả services chạy trên network `bookvault`.

---

## ☸️ Helm Charts (Kubernetes)

**Location**: `platform/helm/`

Đã tạo Helm charts cho 3 services:
- `catalog/`
- `iam/`
- `gateway/`

Mỗi chart bao gồm:
- `Chart.yaml`: Metadata
- `values.yaml`: Default values
- `templates/deployment.yaml`: Kubernetes Deployment
- `templates/service.yaml`: Kubernetes Service

**Cấu hình**:
- Image: `ghcr.io/your-org/{service}-service:latest`
- Replica count: 1 (có thể scale)
- Service type: ClusterIP
- Environment variables: Database connection, JWT keys

**Deploy command**:
```bash
helm upgrade --install catalog ./platform/helm/catalog \
  --set image.repository=ghcr.io/your-org/catalog-service \
  --namespace bookvault --create-namespace
```

---

## 🔄 CI/CD Pipeline

**File**: `.github/workflows/build-deploy.yml`

**Workflow stages**:

1. **build-and-test**
   - Build và test tất cả services (catalog, iam, gateway)
   - Sử dụng JDK 21
   - Maven cache

2. **build-images**
   - Build Docker images cho mỗi service
   - Push lên GitHub Container Registry (ghcr.io)
   - Docker layer caching

3. **security-scan**
   - Scan images với Trivy
   - Upload SARIF results lên GitHub Security

4. **helm-deploy**
   - Deploy lên Kubernetes bằng Helm
   - Chỉ chạy khi push vào branch `main`
   - Yêu cầu `KUBECONFIG` secret

**Triggers**:
- Push vào `main` hoặc `develop`
- Pull requests vào `main`

---

## 🔐 Security Features

### JWT Authentication (RS256)
- **Private key**: Được generate tự động nếu không có trong env (dev mode)
- **Public key**: Exposed qua `/api/iam/v1/auth/public-key`
- **Algorithm**: RS256 (RSA 2048-bit)
- **Token TTL**: 3600 seconds (configurable)

### Password Security
- BCrypt hashing với salt rounds
- Không lưu plaintext password

### Gateway Security
- JWT validation cho tất cả routes (trừ `/api/iam/**` và actuator)
- Fallback: Nếu không có public key → permit all (dev mode)

---

## 📊 Observability

### Metrics
- **Prometheus**: Tất cả services expose metrics tại `/actuator/prometheus`
- **Actuator**: Health, info, metrics endpoints

### Logging
- JSON logs với trace ID
- Trace ID được inject vào request headers (`X-Trace-Id`)

### OpenAPI Documentation
- Swagger UI tại `/swagger-ui` cho mỗi service
- OpenAPI spec tại `/v3/api-docs`

---

## 🚀 Hướng Dẫn Chạy

### 1. Local Development (Docker Compose)

```bash
# Start tất cả services
docker compose up -d

# Xem logs
docker compose logs -f

# Stop
docker compose down
```

**Services sẽ chạy tại**:
- Gateway: http://localhost:8080
- Catalog: http://localhost:8081
- IAM: http://localhost:8082

### 2. Test API

**Register user**:
```bash
curl -X POST http://localhost:8080/api/iam/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'
```

**Login**:
```bash
curl -X POST http://localhost:8080/api/iam/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

**Create book** (với JWT token):
```bash
curl -X POST http://localhost:8080/api/catalog/v1/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"title":"Spring Boot Guide","author":"John Doe","isbn":"978-1234567890","quantity":10}'
```

### 3. Kubernetes Deployment

```bash
# Install Helm charts
helm install catalog ./platform/helm/catalog -n bookvault --create-namespace
helm install iam ./platform/helm/iam -n bookvault
helm install gateway ./platform/helm/gateway -n bookvault

# Check status
kubectl get pods -n bookvault
```

---

## 📝 Checklist Đã Hoàn Thành

### ✅ Infrastructure
- [x] Tạo cấu trúc monorepo (services/, platform/, config/, libs/, common/, contracts/)
- [x] Docker Compose cho local development
- [x] Helm charts cho Kubernetes deployment
- [x] CI/CD pipeline (GitHub Actions)

### ✅ IAM Service
- [x] Spring Boot application skeleton
- [x] User entity + repository
- [x] JWT service (RS256)
- [x] Auth controller (register/login)
- [x] Database migration (Flyway)
- [x] Dockerfile
- [x] OpenAPI spec
- [x] Helm chart

### ✅ Catalog Service
- [x] Spring Boot application skeleton
- [x] Book entity + repository + service
- [x] CRUD REST API
- [x] Database migration (Flyway)
- [x] Dockerfile
- [x] OpenAPI spec
- [x] Helm chart

### ✅ Gateway Service
- [x] Spring Cloud Gateway setup
- [x] Route configuration
- [x] JWT validation filter
- [x] Security configuration
- [x] Dockerfile
- [x] Helm chart

### ✅ Message Broker
- [x] Kafka + Zookeeper trong docker-compose

### ✅ Documentation
- [x] OpenAPI specs cho các services
- [x] Báo cáo refactor (file này)

---

## 🔮 Các Service Còn Lại (Chưa Implement)

Các service sau sẽ được implement trong giai đoạn tiếp theo:

1. **Borrowing Service**
   - Mượn/trả sách
   - Saga orchestration với Catalog
   - Database: loans, loan_items, fines

2. **Search Service**
   - Elasticsearch index từ Catalog events
   - Full-text search API

3. **Notification Service**
   - Email/SMS/WebSocket notifications
   - Template engine (Freemarker/Thymeleaf)

4. **Media Service**
   - Upload images (S3/MinIO)
   - Signed URLs

5. **Admin Service**
   - Dashboard, reports
   - Read-only aggregated views

---

## 📈 Metrics & Monitoring

### Health Checks
- **Gateway**: http://localhost:8080/actuator/health
- **Catalog**: http://localhost:8081/actuator/health
- **IAM**: http://localhost:8082/actuator/health

### Prometheus Metrics
- **Gateway**: http://localhost:8080/actuator/prometheus
- **Catalog**: http://localhost:8081/actuator/prometheus
- **IAM**: http://localhost:8082/actuator/prometheus

---

## 🛠️ Công Nghệ Sử Dụng

### Backend
- **Java**: 21
- **Spring Boot**: 3.3.4
- **Spring Cloud Gateway**: 2023.0.3
- **Spring Data JPA**: 3.3.4
- **Spring Security**: 3.3.4

### Database
- **PostgreSQL**: 16
- **Flyway**: Database migrations

### Security
- **JWT**: jjwt 0.11.5 (RS256)
- **BCrypt**: Password hashing

### Containerization
- **Docker**: Multi-stage builds
- **Docker Compose**: Local development
- **Kubernetes**: Production deployment
- **Helm**: Package management

### CI/CD
- **GitHub Actions**: Automation
- **Trivy**: Security scanning
- **Maven**: Build tool

### Observability
- **Micrometer**: Metrics
- **Prometheus**: Metrics collection
- **Actuator**: Health checks

---

## 📚 Tài Liệu Tham Khảo

- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [JWT Best Practices](https://datatracker.ietf.org/doc/html/rfc8725)
- [Helm Documentation](https://helm.sh/docs/)
- [Docker Compose Reference](https://docs.docker.com/compose/)

---

## 🎉 Kết Luận

Dự án đã được refactor thành công từ monolithic sang microservices architecture với:

- ✅ 3 services hoàn chỉnh (IAM, Catalog, Gateway)
- ✅ Database per service pattern
- ✅ API Gateway với JWT authentication
- ✅ Docker Compose cho local development
- ✅ Helm charts cho Kubernetes
- ✅ CI/CD pipeline tự động
- ✅ Security best practices (RS256 JWT, BCrypt)
- ✅ Observability (Prometheus, Actuator)

Hệ thống sẵn sàng để:
- Chạy local với `docker compose up`
- Deploy lên Kubernetes với Helm
- Mở rộng thêm các service khác (Borrowing, Search, Notification, etc.)

---

**Tác giả**: AI Assistant (Cursor)  
**Ngày tạo**: 2024  
**Phiên bản**: 1.0

