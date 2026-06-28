package model;

import java.math.BigDecimal;

public class Service {
    private int serviceID;
    private String serviceName;
    private String description;
    private BigDecimal price;
    private boolean isActive;

    public Service() {
    }

    public Service(int serviceID, String serviceName, String description, BigDecimal price, boolean isActive) {
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.isActive = isActive;
    }

    public int getServiceID() { return serviceID; }
    public void setServiceID(int serviceID) { this.serviceID = serviceID; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}
