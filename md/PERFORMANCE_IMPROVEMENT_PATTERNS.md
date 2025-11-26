# 🚀 Performance Improvement Patterns

## Design Patterns để Tăng Hiệu Năng cho Microservices

---

## 📋 **Tổng Quan**

Các patterns này sẽ giúp cải thiện:
- ⚡ **Response Time**: Giảm latency 30-70%
- 📈 **Throughput**: Tăng 50-200% RPS
- 🛡️ **Resilience**: Giảm failure rate 60-90%
- 💰 **Cost**: Tối ưu resource usage 20-40%

---

## 1. **Sidecar Pattern** ⚠️ (Recommended)

### **Mô Tả**
Sidecar là một container chạy cùng với service container, cung cấp cross-cutting concerns như logging, monitoring, caching, security.

### **Lợi Ích**
- ✅ Tách biệt concerns khỏi business logic
- ✅ Reusable across services
- ✅ Independent scaling
- ✅ Technology flexibility

### **Use Cases cho BookVault**

#### **A. Caching Sidecar (Redis)**
```
┌─────────────────────────────────────┐
│         Catalog Service             │
│         Port: 8081                  │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      Redis Sidecar Container        │
│      - Cache frequently accessed    │
│      - Book details                 │
│      - Search results               │
│      - Inventory counts             │
└─────────────────────────────────────┘
```

**Implementation**:
```yaml
# docker-compose.yml
catalog:
  image: bookvault-catalog:latest
  depends_on:
    - catalog-sidecar

catalog-sidecar:
  image: redis:7-alpine
  command: redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru
  volumes:
    - redis-catalog-data:/data
```

**Performance Gain**: 
- Cache hit: **-50-80ms** latency
- Throughput: **+40-60%** RPS
- Database load: **-60-80%**

---

#### **B. Logging Sidecar (Fluentd/Fluent Bit)**
```
┌─────────────────────────────────────┐
│         Any Service                 │
│         (stdout logs)               │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      Fluentd Sidecar                │
│      - Collect logs                 │
│      - Parse & enrich                │
│      - Forward to ELK/Loki          │
└─────────────────────────────────────┘
```

**Benefits**:
- Centralized logging
- No code changes needed
- Structured logs
- Performance: **-10-20%** logging overhead

---

#### **C. Metrics Sidecar (Prometheus Exporter)**
```
┌─────────────────────────────────────┐
│         Service                     │
│         (Business Logic)            │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│      Prometheus Exporter Sidecar    │
│      - Scrape metrics               │
│      - Export to Prometheus         │
│      - No code instrumentation      │
└─────────────────────────────────────┘
```

**Benefits**:
- Zero code changes
- Consistent metrics
- Performance: **-5-10%** metrics overhead

---

### **Implementation Steps**

1. **Add Redis Sidecar cho Catalog Service**
```yaml
# services/catalog/docker-compose.sidecar.yml
version: '3.8'
services:
  catalog:
    # ... existing config
    depends_on:
      - catalog-redis-sidecar

  catalog-redis-sidecar:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - catalog-redis-data:/data
    command: redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru
```

2. **Update Catalog Service Code**
```java
// services/catalog/src/main/java/.../config/RedisConfig.java
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}

// services/catalog/src/main/java/.../book/BookService.java
@Service
public class BookService {
    @Autowired
    private RedisTemplate<String, Book> redisTemplate;
    
    public Book getBook(Long id) {
        String cacheKey = "book:" + id;
        Book cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached; // Cache hit: -50-80ms
        }
        
        Book book = bookRepository.findById(id).orElseThrow();
        redisTemplate.opsForValue().set(cacheKey, book, 1, TimeUnit.HOURS);
        return book;
    }
}
```

3. **Add Dependency**
```xml
<!-- services/catalog/pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**Expected Performance**:
- Response time: **-50-80ms** (cache hit)
- Throughput: **+40-60%** RPS
- Database load: **-60-80%**

---

## 2. **Caching Patterns** ⚠️ (High Priority)

### **A. Cache-Aside Pattern** ✅ (Recommended)

**Flow**:
```
1. Check cache
2. If miss → Query DB
3. Store in cache
4. Return data
```

**Implementation**:
```java
@Service
public class BookService {
    @Cacheable(value = "books", key = "#id")
    public Book getBook(Long id) {
        return bookRepository.findById(id).orElseThrow();
    }
    
    @CacheEvict(value = "books", key = "#book.id")
    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }
}
```

**Performance**: 
- Cache hit: **-50-100ms**
- Throughput: **+50-100%**

---

### **B. Write-Through Caching** ⚠️

**Flow**:
```
1. Write to cache
2. Write to DB
3. Return success
```

**Use Case**: Critical data that must be consistent

**Performance**: 
- Write latency: **+5-10ms** (acceptable)
- Read latency: **-50-80ms** (cache hit)

---

### **C. Write-Behind Caching** ⚠️

**Flow**:
```
1. Write to cache (immediate)
2. Async write to DB (background)
```

**Use Case**: High write throughput, eventual consistency OK

**Performance**: 
- Write latency: **-80-90%** (immediate response)
- Risk: Data loss if cache fails

---

### **D. Read-Through Caching** ⚠️

**Flow**:
```
1. Cache automatically loads from DB on miss
2. Returns cached data
```

**Implementation**:
```java
@Cacheable(value = "books", 
           cacheManager = "readThroughCacheManager")
public Book getBook(Long id) {
    // Cache automatically loads from DB
    return bookRepository.findById(id).orElseThrow();
}
```

---

## 3. **Read Replica Pattern** ⚠️ (High Priority)

### **Mô Tả**
Sử dụng read replicas cho read-heavy operations, master cho writes.

### **Architecture**
```
┌─────────────────┐
│  Catalog Service │
└────────┬────────┘
         │
    ┌────┴────┐
    │         │
    ▼         ▼
┌───────┐  ┌──────────┐
│Master │  │Replica 1 │
│(Write)│  │(Read)     │
└───────┘  └──────────┘
              │
              ▼
         ┌──────────┐
         │Replica 2 │
         │(Read)    │
         └──────────┘
```

### **Implementation**
```java
@Configuration
public class DatabaseConfig {
    @Bean
    @Primary
    public DataSource masterDataSource() {
        // Write operations
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://catalog-postgres-master:5432/catalog")
            .build();
    }
    
    @Bean
    public DataSource replicaDataSource() {
        // Read operations
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://catalog-postgres-replica:5432/catalog")
            .build();
    }
}

@Service
public class BookService {
    @Autowired
    @Qualifier("masterDataSource")
    private BookRepository writeRepository;
    
    @Autowired
    @Qualifier("replicaDataSource")
    private BookRepository readRepository;
    
    public Book getBook(Long id) {
        return readRepository.findById(id).orElseThrow(); // Read from replica
    }
    
    public Book createBook(Book book) {
        return writeRepository.save(book); // Write to master
    }
}
```

**Performance**:
- Read latency: **-20-40%** (distributed load)
- Write throughput: **+30-50%** (no read interference)
- Scalability: **+100-200%** (add more replicas)

---

## 4. **API Gateway Caching** ⚠️ (High Priority)

### **Mô Tả**
Cache responses tại Gateway để giảm load cho backend services.

### **Implementation**
```yaml
# services/gateway/src/main/resources/application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: catalog-books
          uri: http://catalog:8081
          predicates:
            - Path=/api/catalog/v1/books/**
          filters:
            - name: CacheResponse
              args:
                cacheName: bookCache
                ttl: 3600 # 1 hour
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200
```

**Performance**:
- Response time: **-80-150ms** (cache hit)
- Backend load: **-70-90%**
- Throughput: **+200-300%**

---

## 5. **Circuit Breaker Pattern** ⚠️ (Critical)

### **Mô Tả**
Ngăn cascade failures bằng cách "mở circuit" khi service fails.

### **Implementation với Resilience4j**
```xml
<!-- services/borrowing/pom.xml -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.1.0</version>
</dependency>
```

```java
@Service
public class BorrowingService {
    @CircuitBreaker(name = "catalog", fallbackMethod = "getBookFallback")
    @Retry(name = "catalog")
    public Book checkBookAvailability(Long bookId) {
        return catalogClient.getBook(bookId);
    }
    
    public Book getBookFallback(Long bookId, Exception e) {
        // Fallback: Return cached data or default
        return Book.builder()
            .id(bookId)
            .available(false)
            .build();
    }
}
```

```yaml
# application.yml
resilience4j:
  circuitbreaker:
    instances:
      catalog:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 10s
        failureRateThreshold: 50
        eventConsumerBufferSize: 10
  retry:
    instances:
      catalog:
        maxAttempts: 3
        waitDuration: 1000
        exponentialBackoffMultiplier: 2
```

**Performance**:
- Failure recovery: **-90%** cascade failures
- Response time: **-200-500ms** (fallback vs timeout)
- System stability: **+80-95%**

---

## 6. **Bulkhead Pattern** ⚠️

### **Mô Tả**
Isolate resources để prevent one failure from affecting others.

### **Implementation**
```java
@Configuration
public class ThreadPoolConfig {
    @Bean("catalogExecutor")
    public Executor catalogExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("catalog-");
        executor.initialize();
        return executor;
    }
    
    @Bean("borrowingExecutor")
    public Executor borrowingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("borrowing-");
        executor.initialize();
        return executor;
    }
}

@Service
public class BorrowingService {
    @Autowired
    @Qualifier("catalogExecutor")
    private Executor catalogExecutor;
    
    @Async("catalogExecutor")
    public CompletableFuture<Book> getBookAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> 
            catalogClient.getBook(id), catalogExecutor
        );
    }
}
```

**Performance**:
- Isolation: **100%** (no interference)
- Resource utilization: **+30-50%**
- Failure containment: **+90%**

---

## 7. **Rate Limiting Pattern** ⚠️

### **Mô Tả**
Giới hạn số requests per time window để protect services.

### **Implementation tại Gateway**
```java
@Configuration
public class RateLimitConfig {
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(
            100, // replenishRate: requests per second
            200  // burstCapacity: max burst
        );
    }
}
```

```yaml
# application.yml
spring:
  cloud:
    gateway:
      routes:
        - id: catalog
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200
                redis-rate-limiter.requestedTokens: 1
```

**Performance**:
- Service protection: **+90%** (prevent overload)
- Fair resource sharing
- DDoS protection

---

## 8. **Database Connection Pooling Optimization** ✅ (Quick Win)

### **Current vs Optimized**
```yaml
# Current (default HikariCP)
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000

# Optimized
spring:
  datasource:
    hikari:
      maximum-pool-size: 20        # +100%
      minimum-idle: 10              # +100%
      connection-timeout: 20000     # -33%
      idle-timeout: 600000          # 10 minutes
      max-lifetime: 1800000        # 30 minutes
      leak-detection-threshold: 60000
```

**Performance**:
- Connection wait time: **-50-70%**
- Throughput: **+30-50%**
- Resource usage: **+20-30%** (acceptable trade-off)

---

## 9. **Request Coalescing Pattern** ⚠️

### **Mô Tả**
Combine multiple similar requests vào một request để giảm database calls.

### **Implementation**
```java
@Service
public class BookService {
    private final Map<Long, CompletableFuture<Book>> pendingRequests = new ConcurrentHashMap<>();
    
    public CompletableFuture<Book> getBook(Long id) {
        return pendingRequests.computeIfAbsent(id, bookId -> {
            CompletableFuture<Book> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return bookRepository.findById(bookId).orElseThrow();
                } finally {
                    pendingRequests.remove(bookId);
                }
            });
            return future;
        });
    }
}
```

**Performance**:
- Database calls: **-60-80%** (duplicate requests)
- Response time: **-30-50ms** (shared result)
- Throughput: **+40-60%**

---

## 10. **CDN Pattern** ⚠️ (For Static Content)

### **Mô Tả**
Serve static content từ CDN để giảm server load.

### **Implementation**
```java
@Configuration
public class CdnConfig {
    @Bean
    public ResourceHandlerRegistry cdnRegistry() {
        return new ResourceHandlerRegistry()
            .addResourceHandler("/static/**")
            .addResourceLocations("https://cdn.bookvault.com/static/");
    }
}
```

**Performance**:
- Static content latency: **-200-500ms**
- Server load: **-40-60%**
- Global availability: **+90%**

---

## 11. **Database Sharding Pattern** ⚠️ (Future)

### **Mô Tả**
Partition database theo shard key để distribute load.

### **Use Case cho BookVault**
- Shard by `genre` hoặc `author_id`
- Each shard on separate server

**Performance**:
- Write throughput: **+200-400%** (parallel writes)
- Read latency: **-30-50%** (smaller datasets)
- Scalability: **Unlimited** (add shards)

---

## 12. **Event Sourcing Pattern** ⚠️ (Advanced)

### **Mô Tả**
Store events instead of current state, rebuild state from events.

### **Use Case**
- Audit trail
- Time travel queries
- Replay events

**Performance**:
- Write throughput: **+100-200%** (append-only)
- Read complexity: **+50%** (rebuild state)
- Storage: **+200-300%** (all events)

---

## 13. **Materialized Views Pattern** ⚠️

### **Mô Tả**
Pre-compute complex queries into materialized views.

### **Implementation**
```sql
-- Create materialized view
CREATE MATERIALIZED VIEW book_statistics AS
SELECT 
    genre,
    COUNT(*) as total_books,
    SUM(quantity) as total_quantity,
    AVG(rating) as avg_rating
FROM books
GROUP BY genre;

-- Refresh periodically
REFRESH MATERIALIZED VIEW CONCURRENTLY book_statistics;
```

**Performance**:
- Query time: **-80-95%** (pre-computed)
- Database load: **-70-90%**
- Refresh overhead: **+10-20%** (acceptable)

---

## 14. **Write-Behind Caching với Queue** ⚠️

### **Mô Tả**
Write to cache immediately, queue DB writes for async processing.

### **Implementation**
```java
@Service
public class BookService {
    @Autowired
    private RedisTemplate<String, Book> redisTemplate;
    
    @Autowired
    private KafkaTemplate<String, Book> kafkaTemplate;
    
    public Book createBook(Book book) {
        // 1. Write to cache (immediate)
        redisTemplate.opsForValue().set("book:" + book.getId(), book);
        
        // 2. Queue DB write (async)
        kafkaTemplate.send("book-writes", book);
        
        return book; // Immediate response
    }
    
    @KafkaListener(topics = "book-writes")
    public void persistBook(Book book) {
        bookRepository.save(book); // Async DB write
    }
}
```

**Performance**:
- Write latency: **-90-95%** (immediate response)
- Throughput: **+200-400%**
- Risk: Eventual consistency

---

## 15. **API Response Compression** ✅ (Quick Win)

### **Implementation**
```yaml
# application.yml
server:
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain
    min-response-size: 1024
```

**Performance**:
- Response size: **-60-80%** (compressed)
- Network latency: **-40-60%**
- CPU overhead: **+5-10%** (acceptable)

---

## 📊 **Priority Matrix**

| Pattern | Priority | Effort | Impact | Performance Gain |
|---------|----------|--------|--------|------------------|
| **Sidecar (Redis Cache)** | 🔴 High | Medium | High | +40-60% throughput |
| **API Gateway Caching** | 🔴 High | Low | High | +200-300% throughput |
| **Circuit Breaker** | 🔴 High | Medium | High | +80-95% stability |
| **Read Replica** | 🟡 Medium | High | High | +30-50% read throughput |
| **Connection Pooling** | 🟡 Medium | Low | Medium | +30-50% throughput |
| **Rate Limiting** | 🟡 Medium | Low | Medium | +90% protection |
| **Bulkhead** | 🟡 Medium | Medium | Medium | +30-50% resource usage |
| **Request Coalescing** | 🟢 Low | Medium | Medium | +40-60% throughput |
| **Materialized Views** | 🟢 Low | Medium | Medium | -80-95% query time |
| **CDN** | 🟢 Low | High | High | -200-500ms latency |

---

## 🎯 **Recommended Implementation Order**

### **Phase 1: Quick Wins** (1-2 weeks)
1. ✅ API Response Compression
2. ✅ Connection Pooling Optimization
3. ⚠️ API Gateway Caching (Redis)

**Expected Gain**: +30-50% throughput, -20-40% latency

---

### **Phase 2: Caching Layer** (2-3 weeks)
1. ⚠️ Sidecar Pattern (Redis) cho Catalog Service
2. ⚠️ Cache-Aside Pattern implementation
3. ⚠️ Cache invalidation strategy

**Expected Gain**: +40-60% throughput, -50-80ms latency

---

### **Phase 3: Resilience** (2-3 weeks)
1. ⚠️ Circuit Breaker (Resilience4j)
2. ⚠️ Retry Pattern với exponential backoff
3. ⚠️ Bulkhead Pattern

**Expected Gain**: +80-95% stability, -90% cascade failures

---

### **Phase 4: Advanced** (4-6 weeks)
1. ⚠️ Read Replica setup
2. ⚠️ Rate Limiting
3. ⚠️ Request Coalescing
4. ⚠️ Materialized Views

**Expected Gain**: +100-200% scalability, -30-50% latency

---

## 📈 **Expected Overall Performance**

### **After Phase 1-2**:
- Throughput: **+70-110%** (from 1500 → 2500-3100 RPS)
- Latency: **-40-60%** (from 80-120ms → 32-72ms)
- Database load: **-60-80%**

### **After Phase 3-4**:
- Throughput: **+150-250%** (from 1500 → 3750-5250 RPS)
- Latency: **-60-80%** (from 80-120ms → 16-48ms)
- Stability: **+80-95%**
- Scalability: **+200-400%**

---

## 🛠️ **Implementation Checklist**

### **Sidecar Pattern**
- [ ] Add Redis sidecar containers
- [ ] Configure Redis connection in services
- [ ] Implement Cache-Aside pattern
- [ ] Add cache invalidation logic
- [ ] Monitor cache hit rates

### **Circuit Breaker**
- [ ] Add Resilience4j dependency
- [ ] Configure circuit breaker for external calls
- [ ] Implement fallback methods
- [ ] Add retry logic
- [ ] Monitor circuit breaker metrics

### **API Gateway Caching**
- [ ] Configure Redis at Gateway
- [ ] Add cache filters to routes
- [ ] Set appropriate TTLs
- [ ] Implement cache invalidation

### **Read Replica**
- [ ] Setup PostgreSQL replicas
- [ ] Configure read/write data sources
- [ ] Implement routing logic
- [ ] Monitor replication lag

---

## 📚 **Resources**

- **Sidecar Pattern**: https://microservices.io/patterns/deployment/sidecar.html
- **Resilience4j**: https://resilience4j.readme.io/
- **Redis Caching**: https://redis.io/docs/manual/patterns/cache-aside/
- **Circuit Breaker**: https://martinfowler.com/bliki/CircuitBreaker.html

---

**Start with Phase 1 for quick wins, then gradually implement advanced patterns! 🚀**

