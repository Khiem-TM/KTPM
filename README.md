# 📚 **BookVault: Ứng Dụng Quản Lý Sách**

---

## 📖 **Mục Lục**  
1. [Giới thiệu](#gioi-thieu)  
2. [Cài đặt và Cách sử dụng](#cai-dat-va-cach-su-dung)  
3. [Tính năng](#tinh-nang)  
4. [Hình ảnh giao diện](#hinh-anh-giao-dien)  
5. [Hướng phát triển](#huong-phat-trien)  

---
<a id="gioi-thieu"></a>
## 🌟 **Giới thiệu**
**BookVault** là ứng dụng web giúp bạn quản lý bộ sưu tập sách một cách hiệu quả. Phù hợp cho:  
✅ **Thư viện**  
✅ **Người yêu sách**  
✅ **Người quản lý**  

Ứng dụng giúp bạn theo dõi và tổ chức thông tin sách dễ dàng, tiện lợi với giao diện thân thiện.

### 👥 **Thành viên phát triển**  
1. **Phạm Anh Tuấn**     - MSV: 23021709  
2. **Trương Mạnh Khiêm** - MSV: 23021601  
3. **Trần Chiến Thắng**  - MSV: 23021725  

### 🎯 **Lợi ích của BookVault**  
- 🗂 **Quản lý sách hiệu quả**: Sắp xếp sách theo thể loại.  
- 📊 **Tăng năng suất**: Hệ thống quản lý tập trung.  
- 🔍 **Dễ dàng tìm kiếm**: Theo dõi trạng thái mượn trả nhanh chóng.  

### 🛠 **Mô hình cơ sở dữ liệu**  
![Mô hình CSDL](https://github.com/Akapi895/CSDL17/blob/main/asset/frontend/admin/database.png)

---
<a id="cai-dat-va-cach-su-dung"></a>
## 🚀 **Cài đặt và Cách sử dụng**  

### Tài khoản demo
1. User:
- username: usertest
- password: 123456789
2. Admin:
- username: admintest
- password: 123456789

#### Lưu ý:
Vì lí do bản quyền nên nhóm em không thể push OpenAI API Key lên github, thầy có thể liên hệ bạn nhóm trưởng để có thể test tính năng gọi đến openAI ạ.

### 💻 **Yêu cầu hệ thống**  
- **Java Development Kit (JDK)**  
- **Hệ quản trị cơ sở dữ liệu**: MySQL Workbench  
- **IDE**: IntelliJ IDEA  

### 🔧 **Hướng dẫn cài đặt**  

1. **Clone ứng dụng về máy**  
   ```bash
   git clone https://github.com/Akapi895/BookVault
   
2. **Thiết lập cơ sở dữ liệu**
- **Bước 1:** Tạo Schemas mới trong MySQL Workbench tên 'lms'.
- **Bước 2:** Import file SQL:
   ```css
   File -> Open SQL Script...  
   Chọn file `lms.sql` trong folder `database`.  
   Chạy toàn bộ đoạn lệnh SQL.
   ```
- **Bước 3:** Cấu hình MySQL trong file 'application.properties' trong thư mục 'resources':
  ```properties
  spring.datasource.username=your_username  
  spring.datasource.password=your_password
  
3. **Khởi chạy ứng dụng**
- Mở folder BookVault bằng IntelliJ IDEA
- Chạy file LmsApplication.java

4. **Truy cập ứng dụng**
Mở trình duyệt và truy cập:
```ardruino
http://localhost:8080
```
---
<a id="tinh-nang"></a>
## ⚙️ **Tính năng**  

### 📑 **1. Quản lý danh mục sách**  
- Thêm, xóa thông tin sách.  
- Lưu trữ thông tin chi tiết như **tiêu đề**, **tác giả**, **thể loại**, và **năm xuất bản**.  

### 🔍 **2. Tìm kiếm và lọc**  
- Tìm kiếm sách nhanh chóng theo:  
  - **Từ khóa** (tiêu đề, tác giả).  
  - **Thể loại**.  
  - **Năm xuất bản**.  

### 👥 **3. Quản lý người dùng**  
- **Admin**:  
  - Thêm, chỉnh sửa và xóa sách.
  - Tìm kiếm sách thông qua **Google Books API**.
  - Quản lý thông tin người dùng (thêm, xóa, chỉnh sửa).
  - Theo dõi tình trạng sách: sách đang mượn, sách đã trả và lịch sử mượn sách.
 - **User**:
  - **Mượn sách**: Người dùng có thể tìm kiếm và mượn sách một cách dễ dàng.
  - **Đánh giá và bình luận**: Đưa ra đánh giá và bình luận cho từng đầu sách.
  - **Quản lý thông tin cá nhân**: Chỉnh sửa hồ sơ và xem lịch sử mượn trả sách.
  - **Sách yêu thích**: Thêm sách vào danh mục yêu thích để dễ dàng theo dõi.

### 📱 **4. Giao diện thân thiện và đáp ứng**  
- Thiết kế tương thích với mọi thiết bị: **máy tính**, **máy tính bảng**.
- Tối ưu hóa trải nghiệm người dùng với bố cục đơn giản và dễ thao tác.

### 🔒 **5. Bảo mật cao**  
- **Xác thực người dùng**: Bảo mật thông tin qua hệ thống xác thực.
- **Phân quyền người dùng**: Phân quyền rõ ràng giữa Admin và User.
- **Mã hóa dữ liệu**: Thông tin nhạy cảm được mã hóa đảm bảo an toàn.
- **Spring Security**: Tích hợp bảo mật ở cấp độ ứng dụng. 

### 🎮 **6. Tính năng bổ sung**
- **Game**: Trò chơi vui nhộn giúp người dùng thử tài với các gợi ý tên sách.
- **Google Books API**: Tìm kiếm thông tin sách mở rộng qua Google Books API và nhập sách mới vào hệ thống.
- **Spring Security**: Bảo mật hệ thống với tính năng xác thực và phân quyền người dùng.
- **OAuth2 Authentication**: Cho phép người dùng đăng nhập thông qua tài khoản Google hoặc GitHub.
- **Spring Websocket**: Tính năng chat thời gian thực giữa người dùng với nhau.
- **Spring Async**: Tích hợp đa luồng để tối ưu hóa hiệu năng và trải nghiệm mượt mà hơn.
- **Google Cloud Storage API**: Lưu trữ và quản lý hình ảnh sách trên Google Cloud Storage. Đường dẫn hình ảnh được lưu trữ trong cơ sở dữ liệu.

---
<a id="hinh-anh-giao-dien"></a>
## 🖼 **Hình ảnh giao diện**  
### **Giao diện khi chưa đăng nhập**
![notlogin](https://github.com/user-attachments/assets/de896766-b72d-40d3-9c69-c19b90810d5b)

### 👤 **Giao diện Người dùng (User)**  
![user](https://github.com/user-attachments/assets/e602bcd8-073a-436e-846f-014daf70803c)

### 🔐 **Giao diện Quản trị viên (Admin)**  
![admin](https://github.com/user-attachments/assets/2bde4839-a70c-4009-abe3-3fa602b98efc)

---
<a id="huong-phat-trien"></a>
## 🎯 **Hướng phát triển trong tương lai**  
- Bổ sung tính năng **thống kê báo cáo**: số lượng sách mượn trả, sách yêu thích.
- Đào tạo mô hình AI để hỗ trợ người đọc.  
- Phát triển tính năng **gửi thông báo mượn/trả sách** qua email.  
- Nâng cấp giao diện với trải nghiệm người dùng tốt hơn.  

---

Cảm ơn bạn đã sử dụng **BookVault**! 🚀
