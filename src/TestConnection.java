import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/estate_management";
        String user = "root";
        String password = "123456"; // 改成你的MySQL密码
        
        System.out.println("=== 测试MySQL数据库连接 ===");
        System.out.println("URL: " + url);
        System.out.println("用户名: " + user);
        
        try {
            // 1. 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("  MySQL驱动加载成功");
            
            // 2. 建立连接
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("  数据库连接成功");
            
            // 3. 测试查询
            Statement stmt = conn.createStatement();
            
            // 查看数据库中的所有表
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            System.out.println("\n  数据库表列表:");
            int tableCount = 0;
            while (rs.next()) {
                tableCount++;
                System.out.println("  表" + tableCount + ": " + rs.getString(1));
            }
            
            if (tableCount == 0) {
                System.out.println("  数据库中没有表，需要创建表结构");
            }
            
            // 检查系统版本
            rs = stmt.executeQuery("SELECT VERSION() as version");
            if (rs.next()) {
                System.out.println("\nMySQL版本: " + rs.getString("version"));
            }
            
            // 关闭连接
            conn.close();
            System.out.println("\n测试完成，连接已关闭");
            
        } catch (ClassNotFoundException e) {
            System.out.println("✗ 找不到MySQL驱动");
            System.out.println("  请检查 lib/mysql-connector-j-9.3.0.jar 是否存在");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("✗ 连接失败: " + e.getMessage());
            System.out.println("\n可能的原因：");
            System.out.println("1. MySQL服务未启动");
            System.out.println("2. 用户名或密码错误");
            System.out.println("3. 数据库 'estate_management' 不存在");
            System.out.println("4. MySQL端口不是3306");
        }
    }
}