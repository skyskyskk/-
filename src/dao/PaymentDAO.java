package dao;

import model.Payment;
import util.DBUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {
    
    // 添加收费记录
    public boolean addPayment(Payment payment) {
        try (Connection conn = DBUtil.getConnection()) {
            return addPayment(payment, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 添加收费记录（支持外部Connection，用于事务管理）
    public boolean addPayment(Payment payment, Connection conn) {
        String sql = "INSERT INTO payment (record_id, payment_date, amount, payment_type, remarks) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, payment.getRecordId());
            pstmt.setDate(2, Date.valueOf(payment.getPaymentDate()));
            pstmt.setBigDecimal(3, payment.getAmount());
            pstmt.setString(4, payment.getPaymentType());
            pstmt.setString(5, payment.getRemarks());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 获取所有收费记录
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment ORDER BY payment_date DESC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payments;
    }
    
    // 获取特定出租记录的收费记录
    public List<Payment> getPaymentsByRecordId(int recordId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE record_id = ? ORDER BY payment_date DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, recordId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return payments;
    }
    
    // 从ResultSet中提取Payment对象
    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setRecordId(rs.getInt("record_id"));
        payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentType(rs.getString("payment_type"));
        payment.setRemarks(rs.getString("remarks"));
        payment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        return payment;
    }
}