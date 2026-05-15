package model;

import java.time.LocalDateTime;

public class Customer {
    private int customerId;
    private String name;
    private String phone;
    private String email;
    private String idCard;
    private String workUnit;
    private LocalDateTime createdAt;
    
    public Customer() {}
    
    public Customer(String name, String phone, String idCard) {
        this.name = name;
        this.phone = phone;
        this.idCard = idCard;
    }
    
    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    
    public String getWorkUnit() { return workUnit; }
    public void setWorkUnit(String workUnit) { this.workUnit = workUnit; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}