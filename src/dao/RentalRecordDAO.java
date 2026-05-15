package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.RentalRecord;
import util.DBUtil;

public class RentalRecordDAO {
    
    // 添加出租记录并返回生成的记录ID
    public int addRentalRecord(RentalRecord record) {
        try (Connection conn = DBUtil.getConnection()) {
            return addRentalRecord(record, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    // 添加出租记录并返回生成的记录ID（支持外部Connection，用于事务管理）
    public int addRentalRecord(RentalRecord record, Connection conn) {
        String sql = "INSERT INTO rental_record (house_id, customer_id, start_date, end_date, monthly_rent, deposit) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, record.getHouseId());
            pstmt.setInt(2, record.getCustomerId());
            pstmt.setDate(3, Date.valueOf(record.getStartDate()));
            pstmt.setDate(4, Date.valueOf(record.getEndDate()));
            pstmt.setBigDecimal(5, record.getMonthlyRent());
            pstmt.setBigDecimal(6, record.getDeposit());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1; // 添加失败
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    // 获取所有出租记录
    public List<RentalRecord> getAllRentalRecords() {
        List<RentalRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM rental_record ORDER BY start_date DESC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                records.add(extractRentalRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return records;
    }
    
    // 获取当前有效的出租记录
    public List<RentalRecord> getActiveRentalRecords() {
        List<RentalRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM rental_record WHERE end_date >= CURDATE() ORDER BY start_date DESC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                records.add(extractRentalRecordFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return records;
    }
    
    // 根据客户ID获取租房记录
    public List<RentalRecord> getRentalRecordsByCustomerId(int customerId) {
        List<RentalRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM rental_record WHERE customer_id = ? ORDER BY start_date DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(extractRentalRecordFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return records;
    }
    
    // 从ResultSet中提取RentalRecord对象
    private RentalRecord extractRentalRecordFromResultSet(ResultSet rs) throws SQLException {
        RentalRecord record = new RentalRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setHouseId(rs.getInt("house_id"));
        record.setCustomerId(rs.getInt("customer_id"));
        record.setStartDate(rs.getDate("start_date").toLocalDate());
        record.setEndDate(rs.getDate("end_date").toLocalDate());
        record.setMonthlyRent(rs.getBigDecimal("monthly_rent"));
        record.setDeposit(rs.getBigDecimal("deposit"));
        record.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        return record;
    }
    
    // 删除出租记录
    public boolean deleteRentalRecord(int recordId) {
        // 先删除与该出租记录关联的所有收费记录
        String deletePaymentsSql = "DELETE FROM payment WHERE record_id = ?";
        // 获取出租记录对应的房屋ID
        String getHouseIdSql = "SELECT house_id FROM rental_record WHERE record_id = ?";
        // 然后删除出租记录
        String deleteRecordSql = "DELETE FROM rental_record WHERE record_id = ?";
        // 更新房屋状态为空闲
        String updateHouseStatusSql = "UPDATE house SET status = '空闲' WHERE house_id = ?";
        
        try (Connection conn = DBUtil.getConnection()) {
            // 开始事务
            conn.setAutoCommit(false);
            
            try {
                // 获取房屋ID
                int houseId = -1;
                try (PreparedStatement pstmtGetHouse = conn.prepareStatement(getHouseIdSql)) {
                    pstmtGetHouse.setInt(1, recordId);
                    ResultSet rs = pstmtGetHouse.executeQuery();
                    if (rs.next()) {
                        houseId = rs.getInt("house_id");
                    } else {
                        // 出租记录不存在
                        conn.commit();
                        return false;
                    }
                }
                
                // 删除关联的收费记录
                try (PreparedStatement pstmtPayments = conn.prepareStatement(deletePaymentsSql)) {
                    pstmtPayments.setInt(1, recordId);
                    pstmtPayments.executeUpdate();
                }
                
                // 删除出租记录
                try (PreparedStatement pstmtRecord = conn.prepareStatement(deleteRecordSql)) {
                    pstmtRecord.setInt(1, recordId);
                    int affectedRows = pstmtRecord.executeUpdate();
                    if (affectedRows == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                
                // 更新房屋状态为空闲
                if (houseId != -1) {
                    try (PreparedStatement pstmtUpdateHouse = conn.prepareStatement(updateHouseStatusSql)) {
                        pstmtUpdateHouse.setInt(1, houseId);
                        pstmtUpdateHouse.executeUpdate();
                    }
                }
                
                // 提交事务
                conn.commit();
                return true;
            } catch (SQLException e) {
                // 回滚事务
                conn.rollback();
                throw e;
            } finally {
                // 恢复自动提交
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}