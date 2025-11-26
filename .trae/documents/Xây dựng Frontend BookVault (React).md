## Tổng Quan Kiến Trúc
- Backend monolith (Spring MVC + Thymeleaf) tại `src/` với các controller và bảo mật form login, OAuth2:
  - Phân quyền và permit tài nguyên tĩnh: `src/main/java/com/scar/lms/config/SecurityConfiguration.java:44`
  - Điều hướng theo role sau login: `src/main/java/com/scar/lms/config/SecurityConfiguration.java:123`
  - Các luồng sách: `src/main/java/com/scar/lms/controller/BookController.java:57`, `:77`, `:124`, `:142`, `:167`, `:224`, `:250`, `:262`, `:288`, `:307`, `:336`, `:356`
  - WebSocket STOMP: endpoint `/ws` và broker `/topic`: `src/main/java/com/scar/lms/config/WebSocketConfiguration.java:21`, `:28`
- Hệ microservices + API Gateway (Spring Cloud Gateway) tại `services/` phục vụ REST dưới `http://localhost:8080/api/*`:
  - Cấu hình routes Gateway: `services/gateway/src/main/resources/application.yml:1`, `:12`–`:53`
  - OpenAPI đã có cho Catalog/IAM: `services/catalog/openapi.yaml:8`–`:58`, `services/iam/openapi.yaml:8`–`:26`
- Frontend React đã khởi tạo tại `bookvault-frontend/`:
  - Base URL cấu hình qua env: `bookvault-frontend/.env:1`
  - Mapping endpoints microservices: `bookvault-frontend/src/config/api.js:1`
  - Dịch vụ sẵn có: `bookvault-frontend/src/services/authService.js:5`, `bookvault-frontend/src/services/bookService.js:10`

## Mục Tiêu Frontend
- Xây dựng SPA React thay thế Thymeleaf views, tiêu thụ APIs qua Gateway `/api/*`.
- Bao phủ các tính năng hiện có bên monolith: duyệt/sửa sách, tìm kiếm, mượn/trả, yêu thích, profile người dùng, lịch sử, dashboard admin, thông báo. Tùy chọn: tích hợp chat/bot qua WebSocket của monolith.

## Công Nghệ & Phụ Thuộc
- React CRA hiện hữu (`react-scripts`). Bổ sung:
  - `react-router-dom` cho định tuyến client
  - `jwt-decode` để đọc role từ JWT
  - `axios` dùng sẵn, thêm interceptor chung
  - Tùy chọn chat: `sockjs-client`, `@stomp/stompjs`
- Không thêm UI framework nếu không cần; tận dụng CSS hiện có hoặc tối thiểu.

## Kiến Trúc Thư Mục
- Giữ `bookvault-frontend/` và mở rộng:
  - `src/routes/` chứa cấu hình routes và `ProtectedRoute`
  - `src/pages/` cho từng màn hình (Home, Login, Register, Books, BookDetail, Search, Profile, Admin,...)
  - `src/components/` cho UI tái sử dụng (Navbar, Sidebar, Table, Pagination, Modal)
  - `src/services/` mở rộng sẵn có: `iamService`, `catalogService`, `borrowingService`, `adminService`, `searchService`, `mediaService`, `notificationService`
  - `src/context/AuthContext.js` quản lý trạng thái đăng nhập và role
  - `src/hooks/` (ví dụ `useAuth`, `useFetch`)
  - `src/config/api.js` giữ nguyên

## Định Tuyến & Phân Quyền
- Cấu hình routes với guard theo role:
  - Public: `/`, `/login`, `/register`
  - User: `/books`, `/books/:id`, `/search`, `/profile`, `/favourites`, `/borrowed`, `/history`
  - Admin: `/admin`, `/admin/users`, `/admin/books`, `/admin/borrows`, `/admin/notifications`
- `ProtectedRoute` kiểm tra JWT từ `localStorage` (giống `authService.getToken`) và role (`ROLE_USER`, `ROLE_ADMIN`).

## Xác Thực & IAM
- Luồng đăng ký/đăng nhập dùng IAM:
  - `POST /api/iam/v1/auth/register`, `POST /api/iam/v1/auth/login`: `services/iam/openapi.yaml:8`–`:19`
  - Lưu `accessToken` (đã có): `bookvault-frontend/src/services/authService.js:10`
  - Decode token lấy `roles` bằng `jwt-decode` và lưu vào context
  - Tùy chọn: lấy public key để verify signature phía client: `services/iam/openapi.yaml:20`–`:26`
- Interceptor Axios: luôn thêm `Authorization: Bearer <token>` và xử lý 401 (logout/redirect login).

## Catalog (Sách)
- API theo OpenAPI Catalog: `services/catalog/openapi.yaml:8`–`:58`
  - Danh sách: `GET /api/catalog/v1/books`
  - Chi tiết: `GET /api/catalog/v1/books/{id}`
  - Tạo/Cập nhật/Xóa: `POST|PUT|DELETE /api/catalog/v1/books`, `/{id}` (role Admin)
- Màn hình:
  - BookList (phân trang, top, tổng): mirror từ monolith `BookController.findAllBooks`: `src/main/java/com/scar/lms/controller/BookController.java:124`
  - BookSearch (lọc theo title/author/genre/publisher/year): mirror `searchBooks`: `src/main/java/com/scar/lms/controller/BookController.java:77`
  - BookDetail (rating, borrow, favourite): mirror `findBookById`, `rateBook`, `borrow`, `return`, `favourite`: `src/main/java/com/scar/lms/controller/BookController.java:142`, `:167`, `:288`, `:307`, `:336`, `:356`
- Chú ý khoảng trống API microservices cho favourites/rating/borrow
  - Nếu Borrowing đã cung cấp: dùng `POST /api/borrowing/v1/loans`, `POST /api/borrowing/v1/loans/{id}/return` (mapping từ hướng dẫn MD)
  - Favourites/Rating: tạm thời gọi monolith endpoints `/books/add-favourite`, `/books/rate` hoặc hiển thị read-only; sẽ mở rộng microservice sau.

## User/Profile
- Thao tác người dùng (IAM + Media):
  - Profile: `GET/PUT /api/iam/v1/users/me` (nếu có; nếu chưa, dùng monolith `/users/profile` tạm thời)
  - Upload avatar: `POST /api/media/v1/upload` (tham chiếu service media)
  - Lịch sử mượn/yêu thích: dùng Borrowing/Catalog hoặc monolith `/users/*` nếu API REST chưa đầy đủ.

## Admin
- Dashboard và quản trị:
  - Thống kê: tổng sách/tổng lượt mượn/tổng người dùng (tương tự monolith admin): `src/main/java/com/scar/lms/controller/AdminController.java` (xem các đường `GET /admin/total-book`, `GET /admin/total-borrow`)
  - Quản lý User: list/search/create/update/delete
  - Quản lý Book: CRUD, import từ Google Books (mirror `searchAPI`): `src/main/java/com/scar/lms/controller/BookController.java:57`
  - Notifications: list/gửi
  - REST qua Gateway `/api/admin/v1/*` nếu service admin đã expose; nếu thiếu, gọi trực tiếp các service liên quan (IAM/Catalog) theo nhu cầu.

## Thông Báo & Chat
- WebSocket Chat (tùy chọn): kết nối monolith endpoint `/ws`:
  - Broker `/topic`, app prefix `/app`: `src/main/java/com/scar/lms/config/WebSocketConfiguration.java:21`, `:28`
  - Client: dùng `sockjs-client` + `@stomp/stompjs`, subscribe `/topic/chat` (tham chiếu `src/main/resources/static/js/chat.js`)
- Notifications: REST từ service `notification` hoặc fallback polling qua API liên quan.

## Trải Nghiệm Người Dùng & UI
- Navbar cố định + Sidebar admin
- Bảng dữ liệu có phân trang/sort
- Modal xác nhận cho delete/mượn/trả
- Snackbar/Toast hiển thị lỗi/thành công
- Tái sử dụng CSS hiện có; nếu cần, thêm lớp CSS nhẹ trong `src/App.css`

## Xử Lý Lỗi & Trạng Thái
- Interceptor lỗi HTTP chung (401/403/5xx)
- `ErrorBoundary` cho runtime errors
- Loading & Empty states trên mỗi trang

## Kiểm Thử
- Unit/RTL tests cho pages/components chính
- Mock `axios` để test services (`__mocks__/axios.js`)
- Smoke tests cho routes
- Coverage tối thiểu 60%

## CI/CD Frontend
- Tạo pipeline riêng dựa theo hướng dẫn MD (điều chỉnh path thành `bookvault-frontend/**`): `md/HUONG_DAN_HOAN_THIEN.md:474`–`:538`
- Thiết lập secret `API_URL` trỏ tới Gateway (`http://localhost:8080` hoặc domain deploy)

## Lộ Trình Triển Khai
1. Thêm phụ thuộc `react-router-dom`, `jwt-decode` (và tùy chọn `sockjs-client`, `@stomp/stompjs`)
2. Tạo `AuthContext`, `ProtectedRoute`, cấu hình router trong `App.js`
3. Thiết lập `axios` instance + interceptors; tích hợp `authService`
4. Xây dựng trang Public (Home, Login, Register)
5. Xây dựng Catalog (List, Search, Detail + Rating)
6. Xây dựng Borrowing (mượn/trả)
7. Xây dựng Favourites (tạm thời theo monolith nếu chưa có REST)
8. Xây dựng User (Profile, Upload avatar, History)
9. Xây dựng Admin (Dashboard, Users, Books, Borrows, Notifications)
10. Tích hợp Notifications/Chat (nếu bật)
11. Viết tests, hoàn thiện CI

## Giả Định & Ràng Buộc
- Frontend sẽ gọi qua Gateway `/api/*` ở `http://localhost:8080` (có thể thay qua env): `bookvault-frontend/.env:1`
- JWT chứa role `ROLE_USER/ROLE_ADMIN`; xác thực dựa trên IAM
- Một số tính năng (favourites/rating đầy đủ) có thể cần tạm dùng endpoints monolith `src/` trước khi microservice hoàn thiện
- Không thêm framework UI nặng; chỉ dependencies tối cần thiết nêu ở trên

Bạn vui lòng xác nhận kế hoạch trên. Sau khi duyệt, mình sẽ tiến hành cài dependencies, tạo routes/pages, và bắt đầu triển khai theo lộ trình.