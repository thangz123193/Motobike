/* ============================================================
   MOTORBIKE REPAIR SHOP MANAGEMENT SYSTEM
   Full database creation script (SQL Server)
   Run this file FIRST, then 02_seed_data.sql
   ============================================================ */

IF EXISTS (SELECT name FROM sys.databases WHERE name = 'MotorbikeRepairShopDB')
BEGIN
    ALTER DATABASE MotorbikeRepairShopDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE MotorbikeRepairShopDB;
END
GO

CREATE DATABASE MotorbikeRepairShopDB;
GO

USE MotorbikeRepairShopDB;
GO

/* ============================================================
   1. ROLES
   ============================================================ */
CREATE TABLE Roles (
    RoleID      INT IDENTITY(1,1) PRIMARY KEY,
    RoleName    NVARCHAR(50) NOT NULL UNIQUE   -- Admin, Staff, Customer
);
GO

/* ============================================================
   2. USERS  (Admin & Staff & Customer all share this table,
      differentiated by RoleID, to make AuthFilter simple)
   ============================================================ */
CREATE TABLE Users (
    UserID          INT IDENTITY(1,1) PRIMARY KEY,
    Username        NVARCHAR(50)  NOT NULL UNIQUE,
    Password        NVARCHAR(100) NOT NULL,          -- plain text for demo; TODO: BCrypt in production
    FullName        NVARCHAR(100) NOT NULL,
    Email           NVARCHAR(100) NULL,
    Phone           VARCHAR(20)   NULL,
    Address         NVARCHAR(255) NULL,
    RoleID          INT NOT NULL,
    IsActive        BIT NOT NULL DEFAULT 1,           -- lock/unlock account
    CreatedDate     DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Users_Roles FOREIGN KEY (RoleID) REFERENCES Roles(RoleID)
);
GO

/* ============================================================
   3. MOTORBIKES (owned by Customers)
   ============================================================ */
CREATE TABLE Motorbikes (
    MotorbikeID     INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID      INT NOT NULL,
    LicensePlate    VARCHAR(20) NOT NULL UNIQUE,
    Brand           NVARCHAR(50) NULL,
    Model           NVARCHAR(50) NULL,
    Color           NVARCHAR(30) NULL,
    Year            INT NULL,
    CONSTRAINT FK_Motorbikes_Users FOREIGN KEY (CustomerID) REFERENCES Users(UserID)
);
GO

/* ============================================================
   4. SERVICES (repair services offered by the shop)
   ============================================================ */
CREATE TABLE Services (
    ServiceID       INT IDENTITY(1,1) PRIMARY KEY,
    ServiceName     NVARCHAR(100) NOT NULL,
    Description     NVARCHAR(500) NULL,
    Price           DECIMAL(18,0) NOT NULL DEFAULT 0,  -- VND, no decimals needed
    IsActive        BIT NOT NULL DEFAULT 1
);
GO

/* ============================================================
   5. PARTS (spare parts inventory)
   ============================================================ */
CREATE TABLE Parts (
    PartID          INT IDENTITY(1,1) PRIMARY KEY,
    PartName        NVARCHAR(100) NOT NULL,
    Description     NVARCHAR(500) NULL,
    Unit            NVARCHAR(20) NULL,             -- pcs, set, liter...
    Price           DECIMAL(18,0) NOT NULL DEFAULT 0,
    Quantity        INT NOT NULL DEFAULT 0,        -- stock on hand
    IsActive        BIT NOT NULL DEFAULT 1
);
GO

/* ============================================================
   6. CUSTOMER BOOKINGS (appointment requests from customers
      or walk-ins entered by Admin)
   ============================================================ */
CREATE TABLE CustomerBookings (
    BookingID       INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID      INT NULL,                       -- NULL if walk-in not yet linked to an account
    FullName        NVARCHAR(100) NOT NULL,          -- snapshot, in case CustomerID is null
    Phone           VARCHAR(20)   NOT NULL,
    LicensePlate    VARCHAR(20)   NOT NULL,
    VehicleInfo     NVARCHAR(150) NULL,              -- brand/model/color text
    Description     NVARCHAR(500) NULL,              -- customer's issue description
    PreferredDate   DATETIME NULL,
    Status          NVARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, CONFIRMED, REJECTED, CONVERTED
    RejectReason    NVARCHAR(255) NULL,
    CreatedDate     DATETIME NOT NULL DEFAULT GETDATE(),
    RepairOrderID   INT NULL,                        -- set after conversion to an order
    CONSTRAINT FK_Bookings_Customer FOREIGN KEY (CustomerID) REFERENCES Users(UserID),
    CONSTRAINT CK_Bookings_Status CHECK (Status IN ('PENDING','CONFIRMED','REJECTED','CONVERTED'))
);
GO

/* ============================================================
   7. REPAIR ORDERS  (core workflow entity)
      PENDING -> ACCEPTED -> PROCESSING -> COMPLETED
                         \-> REJECTED (branch state, technician refusal)
   ============================================================ */
CREATE TABLE RepairOrders (
    OrderID         INT IDENTITY(1,1) PRIMARY KEY,
    CustomerID      INT NULL,
    MotorbikeID     INT NULL,
    BookingID       INT NULL,                       -- if created from a booking
    AssignedStaffID INT NULL,                       -- technician assigned
    Status          NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    Description     NVARCHAR(500) NULL,             -- problem description
    EstimatedCost   DECIMAL(18,0) NOT NULL DEFAULT 0,
    TotalCost       DECIMAL(18,0) NOT NULL DEFAULT 0, -- final cost after parts+services
    RejectReason    NVARCHAR(255) NULL,
    CreatedDate     DATETIME NOT NULL DEFAULT GETDATE(),
    AssignedDate    DATETIME NULL,
    AcceptedDate    DATETIME NULL,
    CompletedDate   DATETIME NULL,
    CONSTRAINT FK_Orders_Customer FOREIGN KEY (CustomerID) REFERENCES Users(UserID),
    CONSTRAINT FK_Orders_Motorbike FOREIGN KEY (MotorbikeID) REFERENCES Motorbikes(MotorbikeID),
    CONSTRAINT FK_Orders_Booking FOREIGN KEY (BookingID) REFERENCES CustomerBookings(BookingID),
    CONSTRAINT FK_Orders_Staff FOREIGN KEY (AssignedStaffID) REFERENCES Users(UserID),
    CONSTRAINT CK_Orders_Status CHECK (Status IN ('PENDING','ACCEPTED','PROCESSING','COMPLETED','REJECTED'))
);
GO

/* ============================================================
   8. ORDER PART/SERVICE LINES (vehicle inspection report
      content: services performed + parts replaced on an order)
   ============================================================ */
CREATE TABLE OrderDetails (
    OrderDetailID   INT IDENTITY(1,1) PRIMARY KEY,
    OrderID         INT NOT NULL,
    ItemType        NVARCHAR(10) NOT NULL,          -- 'SERVICE' or 'PART'
    ServiceID       INT NULL,
    PartID          INT NULL,
    Quantity        INT NOT NULL DEFAULT 1,
    UnitPrice       DECIMAL(18,0) NOT NULL DEFAULT 0, -- price snapshot at time of use
    LineTotal       AS (Quantity * UnitPrice) PERSISTED,
    CONSTRAINT FK_OrderDetails_Order FOREIGN KEY (OrderID) REFERENCES RepairOrders(OrderID),
    CONSTRAINT FK_OrderDetails_Service FOREIGN KEY (ServiceID) REFERENCES Services(ServiceID),
    CONSTRAINT FK_OrderDetails_Part FOREIGN KEY (PartID) REFERENCES Parts(PartID),
    CONSTRAINT CK_OrderDetails_Type CHECK (ItemType IN ('SERVICE','PART'))
);
GO

/* ============================================================
   9. REPAIR LOGS (technician progress notes / inspection report)
   ============================================================ */
CREATE TABLE RepairLogs (
    LogID           INT IDENTITY(1,1) PRIMARY KEY,
    OrderID         INT NOT NULL,
    StaffID         INT NOT NULL,
    Note            NVARCHAR(1000) NOT NULL,
    CreatedDate     DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_RepairLogs_Order FOREIGN KEY (OrderID) REFERENCES RepairOrders(OrderID),
    CONSTRAINT FK_RepairLogs_Staff FOREIGN KEY (StaffID) REFERENCES Users(UserID)
);
GO

/* ============================================================
   10. FEEDBACK (customer rating/review after order completed)
   ============================================================ */
CREATE TABLE Feedback (
    FeedbackID      INT IDENTITY(1,1) PRIMARY KEY,
    OrderID         INT NOT NULL,
    CustomerID      INT NOT NULL,
    Rating          INT NOT NULL,                   -- 1..5
    Comment         NVARCHAR(500) NULL,
    CreatedDate     DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Feedback_Order FOREIGN KEY (OrderID) REFERENCES RepairOrders(OrderID),
    CONSTRAINT FK_Feedback_Customer FOREIGN KEY (CustomerID) REFERENCES Users(UserID),
    CONSTRAINT CK_Feedback_Rating CHECK (Rating BETWEEN 1 AND 5)
);
GO

/* ============================================================
   11. AUDIT LOGS (track admin/staff actions for accountability)
   ============================================================ */
CREATE TABLE AuditLogs (
    AuditID         INT IDENTITY(1,1) PRIMARY KEY,
    UserID          INT NULL,
    Action          NVARCHAR(100) NOT NULL,
    Detail          NVARCHAR(500) NULL,
    CreatedDate     DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_AuditLogs_User FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
GO

/* ============================================================
   Helpful indexes
   ============================================================ */
CREATE INDEX IX_Orders_Status ON RepairOrders(Status);
CREATE INDEX IX_Orders_AssignedStaff ON RepairOrders(AssignedStaffID);
CREATE INDEX IX_Bookings_Status ON CustomerBookings(Status);
CREATE INDEX IX_Users_Role ON Users(RoleID);
GO

PRINT 'Database MotorbikeRepairShopDB created successfully.';
