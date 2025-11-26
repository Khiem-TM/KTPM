## Mục Tiêu
- Bổ sung CRUD quản trị chi tiết với bảng, phân trang server-side, và form validation.
- Hoàn thiện REST cho favourites/rating bên microservices, bỏ phụ thuộc vào monolith `src/`.
- Viết thêm tests cho pages/services quan trọng, nâng coverage (>=70%).
- Đảm bảo kết nối frontend ↔ backend qua Gateway `/api/*`. Nếu service thiếu DB, dùng MongoDB local.

## Kiến Trúc Hiện Tại
- Gateway định tuyến `/api/*` tới các service: `services/gateway/src/main/resources/application.yml:12`–`53`.
- Catalog/IAM/Borrowing dùng Postgres (Docker compose): `docker-compose.yml:2`–`43`, `:44`–`117`.
- Search dùng Elasticsearch: `docker-compose.yml:152`–`167`, `:201`–`211`.
- Notification, Media chưa khai DB (Notification hiện chỉ Kafka + Mail): `docker-compose.yml:168`–`199`.
- Frontend SPA đã dựng: router, Auth, pages admin/user/catalog/chat, http interceptor.

## Phân Trang & CRUD Admin (Server-side)
### Catalog Service (Books)
- Endpoints (mở rộng từ OpenAPI `services/catalog/openapi.yaml:8`–`58`):
  - `GET /api/catalog/v1/books?page&size&sort&title&author&isbn` trả `Page<BookResponse>`.
  - `POST /api/catalog/v1/books` tạo sách (DTO validation).
  - `PUT /api/catalog/v1/books/{id}` cập nhật.
  - `DELETE /api/catalog/v1/books/{id}` xóa.
- Validation: Bean Validation trên DTO (`@NotBlank`, `@Size`, `@Pattern` cho ISBN).
- Truy vấn: Spring Data JPA + Specification (lọc title/author/isbn), phân trang `Pageable`.

### IAM Service (Users)
- Bổ sung endpoints quản trị:
  - `GET /api/iam/v1/users?page&size&sort&search` (search theo `username`/`email`).
  - `POST /api/iam/v1/users` (tạo user, set roles), `PUT /api/iam/v1/users/{id}`, `DELETE /api/iam/v1/users/{id}`.
- Validation: `@Email`, `@Size`, kiểm tra unique email/username.

### Borrowing Service (Loans)
- Endpoints:
  - `GET /api/borrowing/v1/loans?page&size&userId&bookId&status` trả `Page<Loan>`.
  - `POST /api/borrowing/v1/loans` tạo loan (trường: `userId`, `bookId`, `quantity`).
  - `POST /api/borrowing/v1/loans/{id}/return` trả sách.
- Validation: không mượn quá `quantity` tồn; trạng thái `OPEN/RETURNED/OVERDUE`.

## REST Favourites/Rating (Microservices)
### Rating (đặt tại Catalog)
- Schema: `Rating{id, bookId, userId, points(1..10), comment, time}` trong Postgres Catalog.
- Endpoints:
  - `GET /api/catalog/v1/books/{id}/ratings?page&size` → `Page<RatingResponse>`.
  - `POST /api/catalog/v1/books/{id}/ratings` body `{userId, points, comment}`.
  - `GET /api/catalog/v1/books/{id}/rating-summary` trả `{avg, count}`.
- Security: yêu cầu `ROLE_USER/ROLE_ADMIN` qua Gateway.

### Favourites (đặt tại IAM)
- Schema: `Favourite{id, userId, bookId, createdAt}` trong Postgres IAM.
- Endpoints:
  - `GET /api/iam/v1/favourites?page&size&userId` → `Page<FavouriteResponse>`.
  - `POST /api/iam/v1/favourites` body `{bookId}` (user lấy từ JWT).
  - `DELETE /api/iam/v1/favourites/{bookId}` xoá theo user hiện tại.
- Gọi Catalog để enrich book info ở response nếu cần (hoặc trả `bookId` + để frontend gọi Catalog).

## MongoDB Local (bổ sung nơi thiếu DB)
- Thêm `mongodb` service vào `docker-compose.yml`:
  - `image: mongo:6`, ports `27017:27017`, volume `mongo_data`.
- Notification Service lưu thông báo vào MongoDB:
  - Collection `notifications` `{id, userId, title, content, status, createdAt}`.
  - Endpoints: `GET /api/notification/v1/notifications?page&size&userId`, `POST /api/notification/v1/notifications`, `PUT /api/notification/v1/notifications/{id}` (đổi status), `DELETE /.../{id}`.
- Tùy chọn: Admin audit logs trong Mongo (nếu cần).
- Search tiếp tục dùng Elasticsearch; không chuyển sang Mongo.

## Frontend Cập Nhật
- Admin pages:
  - Users: bảng, phân trang (server), form create/update, xoá; validate client.
  - Books: bảng + CRUD + filter; form validation (ISBN pattern).
  - Borrows: bảng loans + filter; thao tác return.
- Catalog/User pages:
  - Favourites: chuyển sang endpoints IAM `/api/iam/v1/favourites` thay vì monolith.
  - Rating: chuyển sang endpoints Catalog `/api/catalog/v1/books/{id}/ratings`.
- Pagination component chung, Table component, Form input với lỗi.

## Tests & Coverage
- Backend:
  - Unit tests: Services + Validators cho Catalog/IAM/Borrowing.
  - Integration tests: Controllers với Testcontainers Postgres; Kafka stub khi cần.
  - IAM favourites: create/delete/list; Catalog rating: create/list/summary.
- Frontend:
  - RTL tests cho Admin Users/Books/Borrows pages (render bảng, gọi service, xử lý phân trang).
  - Mock axios (đã có `src/__mocks__/axios.js`) và kiểm tra luồng submit/validate.
  - Coverage tối thiểu 70%.

## Bảo Mật & Gateway
- Gateway giữ nguyên routes; thêm filter JWT nếu cần cho IAM favourites/Catalog ratings.
- Role mapping: `ROLE_ADMIN` truy cập admin endpoints; `ROLE_USER` truy cập user/favourites/ratings.

## Lộ Trình Thực Thi
1. Mở rộng Catalog/IAM/Borrowing: thêm entity/DTO/repository/service/controller cho phân trang CRUD.
2. Implement Rating (Catalog) và Favourites (IAM) + migration SQL.
3. Bổ sung MongoDB vào Compose và tích hợp notification service lưu/publish.
4. Cập nhật frontend gọi endpoints mới: pages admin và pages favourites/rating.
5. Viết tests backend + frontend; bật CI chạy test/build.
6. Verify end-to-end: gateway → services → DB (Postgres/Mongo) → frontend.

## Yêu Cầu Môi Trường
- Docker Compose chạy Postgres/Kafka/Elasticsearch/Gateway/Services, cộng thêm MongoDB mới.
- `REACT_APP_API_URL` trỏ tới Gateway (`http://localhost:8080`).

Nếu đồng ý kế hoạch, mình sẽ triển khai code cho services, thêm MongoDB vào compose, cập nhật frontend, và viết tests/CI để bảo đảm hệ thống chạy ổn end-to-end.