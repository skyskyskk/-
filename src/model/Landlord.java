package model;

import java.time.LocalDateTime;

public class Landlord {
    private int landlordId;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String idCard;
    private LocalDateTime createdAt;
    
    public Landlord() {}
    
    public Landlord(String name, String phone, String idCard) {
        this.name = name;
        this.phone = phone;
        this.idCard = idCard;
    }
    
    // Getters and Setters
    public int getLandlordId() { return landlordId; }
    public void setLandlordId(int landlordId) { this.landlordId = landlordId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}