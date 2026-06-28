package model;

import java.math.BigDecimal;

public class OrderDetail {
    private int orderDetailID;
    private int orderID;
    private String itemType;     // SERVICE or PART
    private Integer serviceID;
    private Integer partID;
    private String itemName;     // via JOIN, convenience for display
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal; // computed column in DB

    public OrderDetail() {
    }

    public int getOrderDetailID() { return orderDetailID; }
    public void setOrderDetailID(int orderDetailID) { this.orderDetailID = orderDetailID; }

    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Integer getServiceID() { return serviceID; }
    public void setServiceID(Integer serviceID) { this.serviceID = serviceID; }

    public Integer getPartID() { return partID; }
    public void setPartID(Integer partID) { this.partID = partID; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
