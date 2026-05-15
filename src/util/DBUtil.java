package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtil {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // 静态代码块加载配置文件
    static {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new RuntimeException("未找到数据库配置文件: db.properties");
            }
            
            Properties props = new Properties();
            props.load(is);
            
            // 从配置文件读取数据库连接信息
            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

            // 加载数据库驱动
            Class.forName(props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    /**
     * 获取数据库连接
     * @return 数据库连接对象
     * @throws SQLException 连接获取失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("useSSL", "false");
        props.setProperty("serverTimezone", "UTC");
        props.setProperty("allowPublicKeyRetrieval", "true");
        
        return DriverManager.getConnection(URL, props);
    }

    /**
     * 关闭数据库资源
     * @param conn 数据库连接
     * @param stmt 语句对象
     * @param rs 结果集
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭数据库资源（无结果集）
     * @param conn 数据库连接
     * @param stmt 语句对象
     */
    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}