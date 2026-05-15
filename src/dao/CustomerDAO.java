package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Customer;
import model.User;
import util.DBUtil;

public class CustomerDAO {
    
    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customer (name, phone, email, id_card, work_unit) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getIdCard());
            pstmt.setString(5, customer.getWorkUnit());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customer ORDER BY customer_id ASC";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));
                customer.setIdCard(rs.getString("id_card"));
                customer.setWorkUnit(rs.getString("work_unit"));
                customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return customers;
    }
    
    /**
     * 根据用户名获取客户信息（优先通过customer_id关联， fallback到创建时间关联）
     * @param username 用户名
     * @return 客户对象，如果未找到返回null
     */
    public Customer getCustomerByUserName(String username) {
        // 先获取用户信息，包括customer_id
        String userSql = "SELECT * FROM user WHERE username = ?";
        User user = null;
        Integer customerId = null;
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(userSql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                // 尝试获取customer_id字段（添加异常处理，防止字段不存在）
                try {
                    if (rs.getObject("customer_id") != null) {
                        customerId = rs.getInt("customer_id");
                    }
                } catch (SQLException e) {
                    // customer_id字段不存在，忽略此错误
                    System.out.println("用户表中不存在customer_id字段");
                }
                if (rs.getObject("created_at") != null) {
                    user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
        if (user == null) {
            return null;
        }
        
        // 如果有customer_id，直接使用它获取客户信息
        if (customerId != null) {
            String customerSql = "SELECT * FROM customer WHERE customer_id = ?";
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(customerSql)) {
                
                pstmt.setInt(1, customerId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setName(rs.getString("name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    customer.setIdCard(rs.getString("id_card"));
                    customer.setWorkUnit(rs.getString("work_unit"));
                    if (rs.getObject("created_at") != null) {
                        customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                    return customer;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // 如果没有customer_id或者通过customer_id未找到， fallback到创建时间关联
        if (user.getCreatedAt() != null) {
            String customerSql = "SELECT * FROM customer WHERE created_at > ? ORDER BY created_at ASC LIMIT 1";
            
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(customerSql)) {
                
                pstmt.setTimestamp(1, Timestamp.valueOf(user.getCreatedAt().minusSeconds(10)));
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setName(rs.getString("name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    customer.setIdCard(rs.getString("id_card"));
                    customer.setWorkUnit(rs.getString("work_unit"));
                    if (rs.getObject("created_at") != null) {
                        customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                    return customer;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     * 根据ID删除客户信息
     * @param customerId 客户ID
     * @return 是否删除成功
     */
    public boolean deleteCustomer(int customerId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            
            // 检查是否有相关的出租记录
            String checkSql = "SELECT COUNT(*) FROM rental_record WHERE customer_id = ?";
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, customerId);
                ResultSet rs = checkPstmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // 有相关出租记录，无法删除
                    System.out.println("无法删除客户，该客户有相关出租记录");
                    return false;
                }
            }
            
            // 执行删除操作
            String deleteSql = "DELETE FROM customer WHERE customer_id = ?";
            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSql)) {
                deletePstmt.setInt(1, customerId);
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
     * 更新客户信息
     * @param customer 客户对象
     * @return 是否更新成功
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customer SET name = ?, phone = ?, email = ?, id_card = ?, work_unit = ? WHERE customer_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getIdCard());
            pstmt.setString(5, customer.getWorkUnit());
            pstmt.setInt(6, customer.getCustomerId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据客户ID获取客户信息
     * @param customerId 客户ID
     * @return 客户对象，如果未找到返回null
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setName(rs.getString("name"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));
                customer.setIdCard(rs.getString("id_card"));
                customer.setWorkUnit(rs.getString("work_unit"));
                customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}