// src/Main.java
import javax.swing.SwingUtilities;
import view.MainFrame;
public class Main {
    public static void main(String[] args) {
        System.out.println("=== 二手房中介管理系统启动 ===");
        System.out.println("版本: 1.0");
        System.out.println("JDK版本: " + System.getProperty("java.version"));
        System.out.println();
        
        // 直接在EDT线程中启动图形界面，无需终端输入
        System.out.println("启动图形化系统...");
        SwingUtilities.invokeLater(() -> new MainFrame());
    }

    // 数据库连接测试方法已移除，系统现在直接启动图形界面
}