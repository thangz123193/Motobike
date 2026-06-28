/* ============================================================
   SEED DATA for MotorbikeRepairShopDB
   Run AFTER 01_create_database.sql
   ============================================================ */

USE MotorbikeRepairShopDB;
GO

/* ---------- Roles ---------- */
INSERT INTO Roles (RoleName) VALUES ('Admin'), ('Staff'), ('Customer');
GO

/* ---------- Users ----------
   Passwords are plain text for demo purposes (matches starter DBContext style).
   RoleID: 1 = Admin, 2 = Staff, 3 = Customer
*/
INSERT INTO Users (Username, Password, FullName, Email, Phone, Address, RoleID, IsActive) VALUES
('admin',    '123456', N'Nguyen Van Admin',  'admin@motorshop.vn',  '0900000001', N'12 Le Loi, Q1, TP.HCM', 1, 1),
('staff01',  '123456', N'Tran Van Tho',      'tho.staff@motorshop.vn', '0900000002', N'45 Nguyen Trai, Q5, TP.HCM', 2, 1),
('staff02',  '123456', N'Le Van Hung',       'hung.staff@motorshop.vn', '0900000003', N'78 Vo Van Tan, Q3, TP.HCM', 2, 1),
('customer01','123456', N'Pham Thi Lan',     'lan.pham@gmail.com',  '0911111111', N'10 Tran Hung Dao, Q1, TP.HCM', 3, 1),
('customer02','123456', N'Hoang Van Nam',    'nam.hoang@gmail.com', '0922222222', N'25 Cach Mang Thang 8, Q10, TP.HCM', 3, 1);
GO

/* ---------- Motorbikes ---------- */
INSERT INTO Motorbikes (CustomerID, LicensePlate, Brand, Model, Color, Year) VALUES
((SELECT UserID FROM Users WHERE Username='customer01'), '59-X1 123.45', N'Honda', N'Wave Alpha', N'Đỏ', 2019),
((SELECT UserID FROM Users WHERE Username='customer02'), '59-P2 678.90', N'Yamaha', N'Exciter 155', N'Xanh', 2021);
GO

/* ---------- Services ---------- */
INSERT INTO Services (ServiceName, Description, Price, IsActive) VALUES
(N'Thay nhớt máy', N'Thay nhớt động cơ và lọc nhớt', 120000, 1),
(N'Bảo dưỡng định kỳ', N'Kiểm tra tổng quát, vệ sinh kim phun, lọc gió', 250000, 1),
(N'Sửa hệ thống phanh', N'Kiểm tra và sửa chữa hệ thống phanh trước/sau', 150000, 1),
(N'Thay lốp xe', N'Thay lốp trước hoặc sau (chưa tính giá lốp)', 50000, 1),
(N'Sửa hệ thống điện', N'Kiểm tra và sửa chữa hệ thống điện, đèn, còi', 180000, 1),
(N'Cân chỉnh xe số/CVT', N'Cân chỉnh dây curoa, nhông xích, côn', 200000, 1);
GO

/* ---------- Parts ---------- */
INSERT INTO Parts (PartName, Description, Unit, Price, Quantity, IsActive) VALUES
(N'Nhớt máy Castrol 1L', N'Nhớt động cơ tổng hợp', N'lon', 95000, 50, 1),
(N'Lọc nhớt', N'Lọc nhớt động cơ phổ thông', N'cái', 35000, 40, 1),
(N'Lọc gió', N'Lọc gió động cơ', N'cái', 45000, 35, 1),
(N'Bố phanh trước', N'Má phanh đĩa trước', N'bộ', 80000, 30, 1),
(N'Bố phanh sau', N'Má phanh đĩa sau', N'bộ', 70000, 30, 1),
(N'Lốp Michelin 80/90-17', N'Lốp xe máy phổ thông', N'cái', 420000, 20, 1),
(N'Bóng đèn pha LED', N'Đèn pha LED thay thế', N'cái', 150000, 25, 1),
(N'Dây curoa CVT', N'Dây curoa cho xe tay côn tự động', N'cái', 220000, 15, 1),
(N'Nhông xích sên dĩa', N'Bộ nhông xích dĩa', N'bộ', 350000, 12, 1),
(N'Bugi', N'Bugi đánh lửa', N'cái', 40000, 60, 1);
GO

/* ---------- Sample Customer Bookings ---------- */
INSERT INTO CustomerBookings (CustomerID, FullName, Phone, LicensePlate, VehicleInfo, Description, PreferredDate, Status, CreatedDate) VALUES
((SELECT UserID FROM Users WHERE Username='customer01'), N'Pham Thi Lan', '0911111111', '59-X1 123.45', N'Honda Wave Alpha, đỏ', N'Xe có tiếng kêu lạ ở động cơ, muốn kiểm tra tổng quát', DATEADD(day, 1, GETDATE()), 'PENDING', GETDATE()),
(NULL, N'Vo Thi Mai', '0933333333', '51-G8 246.81', N'Honda Vision, trắng', N'Xe không nổ máy được, nghi do hệ thống điện', DATEADD(day, 2, GETDATE()), 'PENDING', GETDATE());
GO

/* ---------- Sample Repair Order (already in progress, for demo) ---------- */
DECLARE @CustID INT = (SELECT UserID FROM Users WHERE Username='customer02');
DECLARE @BikeID INT = (SELECT MotorbikeID FROM Motorbikes WHERE LicensePlate='59-P2 678.90');
DECLARE @StaffID INT = (SELECT UserID FROM Users WHERE Username='staff01');

INSERT INTO RepairOrders (CustomerID, MotorbikeID, AssignedStaffID, Status, Description, EstimatedCost, CreatedDate, AssignedDate, AcceptedDate)
VALUES (@CustID, @BikeID, @StaffID, 'PROCESSING', N'Thay nhớt và kiểm tra phanh', 270000, GETDATE(), GETDATE(), GETDATE());
GO

PRINT 'Seed data inserted successfully.';
