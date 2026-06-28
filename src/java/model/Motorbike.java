package model;

public class Motorbike {
    private int motorbikeID;
    private int customerID;
    private String customerName;  // convenience, via JOIN
    private String licensePlate;
    private String brand;
    private String model;
    private String color;
    private Integer year;

    public Motorbike() {
    }

    public Motorbike(int motorbikeID, int customerID, String licensePlate, String brand,
                      String model, String color, Integer year) {
        this.motorbikeID = motorbikeID;
        this.customerID = customerID;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.year = year;
    }

    public int getMotorbikeID() { return motorbikeID; }
    public void setMotorbikeID(int motorbikeID) { this.motorbikeID = motorbikeID; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}
