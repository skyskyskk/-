package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.House;
import util.DBUtil;

public class HouseDAO {
    
    // 添加房屋
    public boolean addHouse(House house) {
        String sql = "INSERT INTO house (house_number, address, type_id, landlord_id, area, price, status, description) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, house.getHouseNumber());
            pstmt.setString(2, house.getAddress());
            pstmt.setInt(3, house.getTypeId());
            pstmt.setInt(4, house.getLandlordId());
            pstmt.setBigDecimal(5, house.getArea());
            pstmt.setBigDecimal(6, house.getPrice());
            pstmt.setString(7, house.getStatus());
            pstmt.setString(8, house.getDescription());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 获取所有房屋
    public List<House> getAllHouses() {
        List<House> houses = new ArrayList<>();
        String sql = "SELECT * FROM house ORDER BY house_id ASC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                houses.add(extractHouseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return houses;
    }
    
    // 根据ID获取房屋
    public House getHouseById(int houseId) {
        String sql = "SELECT * FROM house WHERE house_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, houseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractHouseFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // 更新房屋状态
    public boolean updateHouseStatus(int houseId, String status) {
        String sql = "UPDATE house SET status = ? WHERE house_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, houseId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 获取空闲房屋
    public List<House> getAvailableHouses() {
        List<House> houses = new ArrayList<>();
        String sql = "SELECT * FROM house WHERE status = '空闲' ORDER BY house_number";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                houses.add(extractHouseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return houses;
    }
    
    /**
     * 根据ID删除房屋信息
     * @param houseId 房屋ID
     * @return 是否删除成功
     */
    public boolean deleteHouse(int houseId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            
            // 检查是否有相关的出租记录
            String checkSql = "SELECT COUNT(*) FROM rental_record WHERE house_id = ?";
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, houseId);
                ResultSet rs = checkPstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // 有相关出租记录，无法删除
                    System.out.println("无法删除房屋，该房屋有相关出租记录");
                    return false;
                }
            }
            
            // 执行删除操作
            String deleteSql = "DELETE FROM house WHERE house_id = ?";
            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                deletePstmt.setInt(1, houseId);
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
    
    // 从ResultSet中提取House对象
    private House extractHouseFromResultSet(ResultSet rs) throws SQLException {
        House house = new House();
        house.setHouseId(rs.getInt("house_id"));
        house.setHouseNumber(rs.getString("house_number"));
        house.setAddress(rs.getString("address"));
        house.setTypeId(rs.getInt("type_id"));
        house.setLandlordId(rs.getInt("landlord_id"));
        house.setArea(rs.getBigDecimal("area"));
        house.setPrice(rs.getBigDecimal("price"));
        house.setStatus(rs.getString("status"));
        house.setDescription(rs.getString("description"));
        house.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        return house;
    }
    
    /**
     * 更新房屋信息
     * @param house 房屋对象
     * @return 是否更新成功
     */
    public boolean updateHouse(House house) {
        String sql = "UPDATE house SET house_number = ?, address = ?, type_id = ?, landlord_id = ?, " +
                     "area = ?, price = ?, status = ?, description = ? WHERE house_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, house.getHouseNumber());
            pstmt.setString(2, house.getAddress());
            pstmt.setInt(3, house.getTypeId());
            pstmt.setInt(4, house.getLandlordId());
            pstmt.setBigDecimal(5, house.getArea());
            pstmt.setBigDecimal(6, house.getPrice());
            pstmt.setString(7, house.getStatus());
            pstmt.setString(8, house.getDescription());
            pstmt.setInt(9, house.getHouseId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}