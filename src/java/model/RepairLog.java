package model;

import java.util.Date;

public class RepairLog {
    private int logID;
    private int orderID;
    private int staffID;
    private String staffName; // via JOIN
    private String note;
    private Date createdDate;

    public RepairLog() {
    }

    public int getLogID() { return logID; }
    public void setLogID(int logID) { this.logID = logID; }

    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public int getStaffID() { return staffID; }
    public void setStaffID(int staffID) { this.staffID = staffID; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
}
