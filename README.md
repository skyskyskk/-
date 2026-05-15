#  软件使用说明书

二手房中介管理系统

## 1. 系统运行环境

| 环境类型        | 配置要求                                                                   |
| --------------- | -------------------------------------------------------------------------- |
| 操作系统        | Windows 10/11                                                              |
| 开发 / 运行环境 | JDK 8 及以上                                                               |
| 数据库          | MySQL 8.0 及以上                                                           |
| 界面依赖        | Swing（JDK 自带，无需额外安装）                                            |
| 其他依赖        | MySQL Connector（JDBC 驱动，lib 文件夹已提供；数据库密码不一致需修改配置） |

---

## 2. 系统安装与配置

### 2.1 数据库配置

1. 安装 MySQL 8.0，记住 root 用户密码，确保 MySQL 服务正常启动（默认端口 3306）。
2. 打开 MySQL 客户端（Navicat、MySQL Workbench 或命令行），登录 root 用户，执行 `SQL/full_database_rebuild.sql` 文件。
   - 自动创建数据库：`estate_management`
   - 自动创建数据表、视图、存储过程、触发器
   - 自动插入测试数据
3. 验证数据库：
   ```sql
   USE estate_management;
   SHOW TABLES;
   ```
   2.2 系统运行配置
   导入项目
   将系统代码导入 IDE（VS Code / IntelliJ IDEA），项目结构如下：
   entity：实体类（User、House、RentalRecord 等）
   dao：数据访问类（UserDAO、HouseDAO 等）
   view：界面类（LoginFrame、AdminMainFrame、TenantMainFrame 等）
   util：工具类（DBUtil 数据库连接工具）
   修改数据库连接配置
   打开 util/DBUtil.java，修改为本地 MySQL 信息：
   private static final String URL = "jdbc:mysql://localhost:3306/estate_management?useSSL=false&serverTimezone=UTC";
   private static final String USER = "这里填你的账号";
   private static final String PASSWORD = "这里填你的密码";
添加 JDBC 驱动
将 MySQL Connector/J 的 jar 包放入项目 lib 目录（已提供）。
## 2.3 系统启动
运行项目入口类 src/Main.java：
import javax.swing.SwingUtilities;
import view.MainFrame;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== 二手房中介管理系统启动 ===");
        System.out.println("版本: 1.0");
        System.out.println("JDK版本: " + System.getProperty("java.version"));
        System.out.println();
        
        System.out.println("启动图形化系统...");
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
## 3. 测试账号
管理员：admin /密码 admin123
租客：zhouba / 密码zhouba123