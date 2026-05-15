package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Landlord;
import util.DBUtil;

public class LandlordDAO {
    
    public boolean addLandlord(Landlord landlord) {
        String sql = "INSERT INTO landlord (name, phone, email, address, id_card) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, landlord.getName());
            pstmt.setString(2, landlord.getPhone());
            pstmt.setString(3, landlord.getEmail());
            pstmt.setString(4, landlord.getAddress());
            pstmt.setString(5, landlord.getIdCard());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Landlord> getAllLandlords() {
        List<Landlord> landlords = new ArrayList<>();
        String sql = "SELECT * FROM landlord ORDER BY landlord_id ASC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Landlord landlord = new Landlord();
                landlord.setLandlordId(rs.getInt("landlord_id"));
                landlord.setName(rs.getString("name"));
                landlord.setPhone(rs.getString("phone"));
                landlord.setEmail(rs.getString("email"));
                landlord.setAddress(rs.getString("address"));
                landlord.setIdCard(rs.getString("id_card"));
                landlord.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                landlords.add(landlord);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return landlords;
    }
    
    /**
     * 根据ID删除房东信息
     * @param landlordId 房东ID
     * @return 是否删除成功
     */
    public boolean deleteLandlord(int landlordId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            
            // 检查是否有相关的房屋信息
            String checkSql = "SELECT COUNT(*) FROM house WHERE landlord_id = ?";
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, landlordId);
                ResultSet rs = checkPstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // 有相关房屋信息，无法删除
                    System.out.println("无法删除房东，该房东有相关房屋信息");
                    return false;
                }
            }
            
            // 执行删除操作
            String deleteSql = "DELETE FROM landlord WHERE landlord_id = ?";
            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                deletePstmt.setInt(1, landlordId);
                return deletePstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 更新房东信息
     * @param landlord 房东对象
     * @return 是否更新成功
     */
    public boolean updateLandlord(Landlord landlord) {
        String sql = "UPDATE landlord SET name = ?, phone = ?, email = ?, address = ?, id_card = ? WHERE landlord_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, landlord.getName());
            pstmt.setString(2, landlord.getPhone());
            pstmt.setString(3, landlord.getEmail());
            pstmt.setString(4, landlord.getAddress());
            pstmt.setString(5, landlord.getIdCard());
            pstmt.setInt(6, landlord.getLandlordId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}