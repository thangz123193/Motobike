# Motorbike Repair Shop Management System

Hệ thống quản lý cửa hàng sửa xe máy: Java EE (Jakarta EE), JDK 17, JSP/Servlet,
SQL Server, chạy trên Apache Tomcat 10+ thông qua NetBeans. Không dùng Maven —
toàn bộ thư viện (.jar) được thêm thủ công vào project, đúng như yêu cầu ban đầu.

3 vai trò: **Admin**, **Staff** (kỹ thuật viên), **Customer** (khách hàng).

---

## 1. Yêu cầu môi trường

| Thành phần        | Phiên bản tối thiểu                       |
|--------------------|--------------------------------------------|
| JDK                | 17                                          |
| NetBeans           | 17+ (có hỗ trợ Jakarta EE / Tomcat)        |
| Apache Tomcat      | **10.1.x** (bắt buộc ≥10, vì code dùng `jakarta.servlet.*`, không phải `javax.servlet.*`) |
| SQL Server         | 2017+ (Express là đủ)                       |
| SQL Server JDBC    | mssql-jdbc 12.x, bản `jre11` hoặc `jre17`   |

> ⚠️ **Quan trọng:** Tomcat 9 và cũ hơn dùng package `javax.servlet.*`, không tương
> thích với code này (`jakarta.servlet.*`). Phải dùng Tomcat 10.1+.

---

## 2. Cấu trúc thư mục dự án

```
MotorbikeRepairShop/
├── sql/
│   ├── 01_create_database.sql      <- chạy trước (tạo DB + bảng)
│   └── 02_seed_data.sql            <- chạy sau (dữ liệu mẫu)
├── src/java/
│   ├── model/        (User, RepairOrder, CustomerBooking, Part, Service, ...)
│   ├── dao/           (UserDAO, RepairOrderDAO, PartDAO, ...)
│   ├── filter/        (AuthFilter - phân quyền theo role)
│   ├── util/          (DBContext, ServletUtils)
│   └── controller/
│       ├── common/    (LoginServlet, LogoutServlet, RegisterServlet, RootServlet)
│       ├── customer/  (~7 servlet)
│       ├── staff/     (~8 servlet)
│       └── admin/     (~14 servlet)
├── web/
│   ├── common/, customer/, staff/, admin/   (các trang .jsp)
│   ├── assets/css/style.css
│   └── WEB-INF/web.xml
└── lib/                              <- nơi bạn copy file .jar vào (xem bước 4)
```

---

## 3. Tạo Database SQL Server

1. Mở **SQL Server Management Studio (SSMS)**, kết nối tới instance của bạn.
2. Đảm bảo **SQL Server Authentication** đang bật (không chỉ Windows Authentication):
   SSMS → chuột phải vào server → Properties → Security → "SQL Server and Windows
   Authentication mode" → restart service SQL Server.
3. Đảm bảo có một login `sa` đang active với mật khẩu bạn biết (hoặc tạo login riêng).
4. Mở file `sql/01_create_database.sql`, chạy toàn bộ (F5). File này sẽ:
   - Xoá database `MotorbikeRepairShopDB` nếu đã tồn tại (cẩn thận khi chạy lại)
   - Tạo lại từ đầu: 11 bảng, constraint, index
5. Mở file `sql/02_seed_data.sql`, chạy toàn bộ. File này thêm:
   - 3 role, 5 user mẫu, 2 xe, 6 dịch vụ, 10 phụ tùng, booking mẫu, 1 đơn sửa chữa mẫu

**Tài khoản demo** (đã có sẵn sau khi seed):

| Username    | Password | Vai trò   |
|-------------|----------|-----------|
| admin       | 123456   | Admin     |
| staff01     | 123456   | Staff     |
| staff02     | 123456   | Staff     |
| customer01  | 123456   | Customer  |
| customer02  | 123456   | Customer  |

6. Kiểm tra SQL Server đang lắng nghe TCP/IP cổng 1433:
   **SQL Server Configuration Manager** → SQL Server Network Configuration →
   Protocols for [instance] → **TCP/IP** → Enabled = Yes → restart SQL Server service.

---

## 4. Tạo Project trong NetBeans

### Bước 4.1 — Tạo project mới
1. NetBeans → **File → New Project**
2. Chọn **Java with Maven**? ❌ KHÔNG — chọn **Java Web → Web Application**
   (đây là loại project Ant truyền thống, không Maven)
3. Project Name: `MotorbikeRepairShop`
4. Ở bước **Server and Settings**:
   - Server: chọn **Apache Tomcat 10.1** (nếu chưa có, bấm "Add Server..." và chỉ
     đường dẫn tới thư mục bạn đã giải nén Tomcat 10.1)
   - Java EE Version: **Jakarta EE 10** (hoặc bản cao nhất có chứa `jakarta.servlet`)
5. Finish.

NetBeans sẽ tạo sẵn cấu trúc `src/`, `web/`, `nbproject/`. Bước tiếp theo là **thay
thế / bổ sung** bằng code đã được cung cấp.

### Bước 4.2 — Copy code vào project
Copy đè các thư mục sau từ bộ code này vào project NetBeans vừa tạo (giữ đúng cấu trúc):

- `src/java/*` → vào thư mục `Source Packages` của project (thường là `src/java/`
  hoặc chỉ `src/` tuỳ phiên bản NetBeans — xem package gốc `model`, `dao`, `controller`...)
- `web/*` (trừ `WEB-INF/lib` nếu NetBeans đã tạo sẵn) → vào thư mục `Web Pages`
  (thường là `web/`)
- Đảm bảo file `web/WEB-INF/web.xml` được ghi đè bằng file trong bộ code này.

Sau khi copy, cây project trong NetBeans cần có dạng:
```
MotorbikeRepairShop
├── Source Packages
│   ├── controller.admin
│   ├── controller.common
│   ├── controller.customer
│   ├── controller.staff
│   ├── dao
│   ├── filter
│   ├── model
│   └── util
└── Web Pages
    ├── admin/, common/, customer/, staff/, assets/
    ├── index.jsp
    └── WEB-INF/web.xml
```

### Bước 4.3 — Thêm thư viện JAR thủ công (không Maven)

Bạn cần 2 thư viện:

**(a) Microsoft JDBC Driver for SQL Server**
1. Tải `mssql-jdbc-12.x.x.jreXX.jar` từ trang chính thức Microsoft
   (Microsoft JDBC Driver for SQL Server, chọn bản `jre11` hoặc cao hơn — tương thích JDK 17).
2. Copy file `.jar` vào thư mục `lib/` của project (thư mục `lib/` đã có sẵn trong bộ code này).

**(b) Jakarta Standard Tag Library (JSTL)** — cần cho các thẻ `<c:forEach>`,
`<c:if>`, `<fmt:formatNumber>` dùng trong toàn bộ JSP.
1. Tải 2 file: `jakarta.servlet.jsp.jstl-api-3.0.0.jar` và
   `jakarta.servlet.jsp.jstl-3.0.1.jar` (hoặc bản tương đương Jakarta EE 10).
2. Copy cả 2 vào thư mục `lib/`.

**(c) Gắn JAR vào project trong NetBeans**
1. Chuột phải vào project → **Properties → Libraries**
2. Tab **Compile**: bấm **Add JAR/Folder**, chọn cả 3 file `.jar` trên trong thư mục `lib/`
3. Đảm bảo các JAR này cũng được đánh dấu **Package** (để NetBeans tự copy vào
   `WEB-INF/lib` khi deploy) — thường NetBeans tự làm điều này khi bạn add qua mục Compile.
4. Nếu sau khi deploy mà bị lỗi `ClassNotFoundException: com.microsoft.sqlserver...`,
   vào thư mục build/dist của project, kiểm tra `WEB-INF/lib/` có đủ 3 jar chưa; nếu
   thiếu thì copy tay file `.jar` thẳng vào `web/WEB-INF/lib/` trong project.

### Bước 4.4 — Cập nhật thông tin kết nối DB
Mở file `src/java/util/DBContext.java`, sửa 3 giá trị cho khớp với máy bạn:
```java
private static final String URL =
        "jdbc:sqlserver://localhost:1433;databaseName=MotorbikeRepairShopDB;"
        + "encrypt=true;trustServerCertificate=true;";
private static final String USERNAME = "sa";
private static final String PASSWORD = "sa";   // <-- đổi thành mật khẩu thật của bạn
```

Bạn có thể chạy nhanh `DBContext.main()` (chuột phải file → Run File) để kiểm tra
kết nối trước khi chạy toàn bộ web app.

### Bước 4.5 — Chạy ứng dụng
1. Chuột phải vào project → **Clean and Build**
2. Chuột phải vào project → **Run** (hoặc nút ▶ Run Project)
3. NetBeans sẽ deploy lên Tomcat và mở browser tại
   `http://localhost:8080/Motobike/`
4. Bạn sẽ được chuyển tới trang đăng nhập. Dùng tài khoản demo ở Bước 3.

---

## 5. Luồng nghiệp vụ chính (đối chiếu với use case/flow trong tài liệu)

- **Customer**: đăng ký/đăng nhập → gửi yêu cầu đặt lịch (Booking Form) → theo dõi
  trạng thái (History booking) → xem đơn sửa chữa của mình (Customer Dashboard) →
  đánh giá dịch vụ sau khi đơn COMPLETED.
- **Admin**: xem danh sách booking → xác nhận (tự động tạo/liên kết Customer + Motorbike
  + tạo RepairOrder) hoặc từ chối → phân công kỹ thuật viên → quản lý dịch vụ/phụ
  tùng/tài khoản/khách hàng → xem báo cáo tài chính và đánh giá khách hàng → in hoá đơn.
- **Staff**: xem đơn được phân công → Tiếp nhận (Accept) / Từ chối (Reject) → Bắt đầu
  sửa (Start) → thêm phụ tùng/dịch vụ vào đơn (tự trừ kho) → ghi nhật ký kiểm tra →
  Hoàn thành (Complete, tự tính tổng tiền).

Trạng thái đơn sửa chữa: `PENDING → ACCEPTED → PROCESSING → COMPLETED`,
nhánh phụ `REJECTED` (khi kỹ thuật viên từ chối tại bước PENDING).

---

## 6. Một số lưu ý kỹ thuật

- Mật khẩu lưu dạng **plain text** (giống code mẫu DBContext gốc) để đơn giản hoá
  cho mục đích học tập/demo. Nâng cấp lên BCrypt là việc nên làm tiếp theo
  (xem ghi chú trong `UserDAO`).
- `AuthFilter` kiểm tra lại `IsActive` của user trên **mỗi request**, nên nếu Admin
  khóa một tài khoản đang đăng nhập, người dùng đó sẽ bị đăng xuất ngay ở request kế tiếp.
- Trừ kho phụ tùng (`PartDAO.deductStock`) dùng câu lệnh
  `UPDATE ... WHERE Quantity >= ?` để đảm bảo tính atomic, tránh tồn kho âm khi có
  nhiều request đồng thời.
- Khi xác nhận một Booking thành đơn sửa chữa (`AdminBookingActionServlet`), toàn bộ
  4 bước (tìm/tạo khách hàng → tìm/tạo xe → tạo đơn → cập nhật booking) chạy trong
  **một transaction** (`Connection.setAutoCommit(false)`), đảm bảo không để lại dữ
  liệu rác nếu một bước giữa đường thất bại.
