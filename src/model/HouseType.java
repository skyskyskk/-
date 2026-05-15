package model;

import java.time.LocalDateTime;

public class HouseType {
    private int typeId;
    private String typeName;
    private String description;
    private LocalDateTime createdAt;
    
    public HouseType() {}
    
    public HouseType(String typeName, String description) {
        this.typeName = typeName;
        this.description = description;
    }
    
    // Getters and Setters
    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }
    
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}