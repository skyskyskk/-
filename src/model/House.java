package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class House {
    private int houseId;
    private String houseNumber;
    private String address;
    private int typeId;
    private int landlordId;
    private BigDecimal area;
    private BigDecimal price;
    private String status; // 空闲, 已出租, 维修中
    private String description;
    private LocalDateTime createdAt;
    
    public House() {}
    
    public House(String houseNumber, String address, int typeId, int landlordId, 
                 BigDecimal area, BigDecimal price, String status) {
        this.houseNumber = houseNumber;
        this.address = address;
        this.typeId = typeId;
        this.landlordId = landlordId;
        this.area = area;
        this.price = price;
        this.status = status;
    }
    
    // Getters and Setters
    public int getHouseId() { return houseId; }
    public void setHouseId(int houseId) { this.houseId = houseId; }
    
    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    
    public int getLandlordId() { return landlordId; }
    public void setLandlordId(int landlordId) { this.landlordId = landlordId; }
    
    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}