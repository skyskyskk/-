package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.HouseType;
import util.DBUtil;

public class HouseTypeDAO {
    
    public boolean addHouseType(HouseType type) {
        String sql = "INSERT INTO house_type (type_name, description) VALUES (?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, type.getTypeName());
            pstmt.setString(2, type.getDescription());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<HouseType> getAllHouseTypes() {
        List<HouseType> types = new ArrayList<>();
        String sql = "SELECT * FROM house_type ORDER BY type_id ASC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                HouseType type = new HouseType();
                type.setTypeId(rs.getInt("type_id"));
                type.setTypeName(rs.getString("type_name"));
                type.setDescription(rs.getString("description"));
                type.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                types.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return types;
    }
    
    /**
     * 根据ID删除房屋户型
     * @param typeId 房屋户型ID
     * @return 是否删除成功
     */
    public boolean deleteHouseType(int typeId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            
            // 检查是否有相关的房屋信息
            String checkSql = "SELECT COUNT(*) FROM house WHERE type_id = ?";
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, typeId);
                ResultSet rs = checkPstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // 有相关房屋信息，无法删除
                    System.out.println("无法删除房屋户型，该户型有相关房屋信息");
                    return false;
                }
            }
            
            // 执行删除操作
            String deleteSql = "DELETE FROM house_type WHERE type_id = ?";
            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                deletePstmt.setInt(1, typeId);
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
}