package model;

import java.math.BigDecimal;
import java.util.Date;

public class RepairOrder {
    private int orderID;
    private Integer customerID;
    private String customerName;     // via JOIN
    private String customerPhone;    // via JOIN
    private Integer motorbikeID;
    private String licensePlate;     // via JOIN
    private Integer bookingID;
    private Integer assignedStaffID;
    private String assignedStaffName; // via JOIN
    private String status;            // PENDING, ACCEPTED, PROCESSING, COMPLETED, REJECTED
    private String description;
    private BigDecimal estimatedCost;
    private BigDecimal totalCost;
    private String rejectReason;
    private Date createdDate;
    private Date assignedDate;
    private Date acceptedDate;
    private Date completedDate;

    public RepairOrder() {
    }

    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public Integer getCustomerID() { return customerID; }
    public void setCustomerID(Integer customerID) { this.customerID = customerID; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Integer getMotorbikeID() { return motorbikeID; }
    public void setMotorbikeID(Integer motorbikeID) { this.motorbikeID = motorbikeID; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public Integer getBookingID() { return bookingID; }
    public void setBookingID(Integer bookingID) { this.bookingID = bookingID; }

    public Integer getAssignedStaffID() { return assignedStaffID; }
    public void setAssignedStaffID(Integer assignedStaffID) { this.assignedStaffID = assignedStaffID; }

    public String getAssignedStaffName() { return assignedStaffName; }
    public void setAssignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Date getAssignedDate() { return assignedDate; }
    public void setAssignedDate(Date assignedDate) { this.assignedDate = assignedDate; }

    public Date getAcceptedDate() { return acceptedDate; }
    public void setAcceptedDate(Date acceptedDate) { this.acceptedDate = acceptedDate; }

    public Date getCompletedDate() { return completedDate; }
    public void setCompletedDate(Date completedDate) { this.completedDate = completedDate; }
}
