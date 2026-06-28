package model;

import java.util.Date;

public class CustomerBooking {
    private int bookingID;
    private Integer customerID;     // nullable: walk-in not yet linked to account
    private String fullName;
    private String phone;
    private String licensePlate;
    private String vehicleInfo;
    private String description;
    private Date preferredDate;
    private String status;          // PENDING, CONFIRMED, REJECTED, CONVERTED
    private String rejectReason;
    private Date createdDate;
    private Integer repairOrderID;  // set after conversion

    public CustomerBooking() {
    }

    public int getBookingID() { return bookingID; }
    public void setBookingID(int bookingID) { this.bookingID = bookingID; }

    public Integer getCustomerID() { return customerID; }
    public void setCustomerID(Integer customerID) { this.customerID = customerID; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getPreferredDate() { return preferredDate; }
    public void setPreferredDate(Date preferredDate) { this.preferredDate = preferredDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Integer getRepairOrderID() { return repairOrderID; }
    public void setRepairOrderID(Integer repairOrderID) { this.repairOrderID = repairOrderID; }
}
