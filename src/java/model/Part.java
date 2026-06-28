package model;

import java.math.BigDecimal;

public class Part {
    private int partID;
    private String partName;
    private String description;
    private String unit;
    private BigDecimal price;
    private int quantity;
    private boolean isActive;

    public Part() {
    }

    public Part(int partID, String partName, String description, String unit,
                BigDecimal price, int quantity, boolean isActive) {
        this.partID = partID;
        this.partName = partName;
        this.description = description;
        this.unit = unit;
        this.price = price;
        this.quantity = quantity;
        this.isActive = isActive;
    }

    public int getPartID() { return partID; }
    public void setPartID(int partID) { this.partID = partID; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
}
