// src/view/panels/WelcomePanel.java
package view.panels;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {
    public WelcomePanel() {
        setLayout(new BorderLayout());
        JLabel welcomeLabel = new JLabel("欢迎使用二手房中介管理系统", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("宋体", Font.BOLD, 32)); // 增大字体大小
        add(welcomeLabel, BorderLayout.CENTER);

        JLabel infoLabel = new JLabel("请从左侧选择功能", SwingConstants.CENTER);
        infoLabel.setFont(new Font("宋体", Font.PLAIN, 20)); // 增大字体大小
        add(infoLabel, BorderLayout.SOUTH);
    }
}