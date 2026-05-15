// src/view/panels/LoginPanel.java
package view.panels;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JRadioButton adminRadioButton;
    private JRadioButton tenantRadioButton;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton registerButton;

    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题标签
        JLabel titleLabel = new JLabel("系统登录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 32));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // 用户名标签和输入框
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(usernameField, gbc);

        // 密码标签和输入框
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(passwordField, gbc);

        // 角色选择
        JLabel roleLabel = new JLabel("角色:");
        roleLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(roleLabel, gbc);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adminRadioButton = new JRadioButton("管理员");
        adminRadioButton.setFont(new Font("宋体", Font.PLAIN, 16));
        tenantRadioButton = new JRadioButton("租客");
        tenantRadioButton.setFont(new Font("宋体", Font.PLAIN, 16));

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(adminRadioButton);
        roleGroup.add(tenantRadioButton);
        adminRadioButton.setSelected(true); // 默认选择管理员

        rolePanel.add(adminRadioButton);
        rolePanel.add(tenantRadioButton);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(rolePanel, gbc);

        // 登录、取消和注册按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        loginButton = new JButton("登录");
        loginButton.setFont(new Font("宋体", Font.PLAIN, 16));
        cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("宋体", Font.PLAIN, 16));
        registerButton = new JButton("租客注册");
        registerButton.setFont(new Font("宋体", Font.PLAIN, 16));

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    // 获取用户名
    public String getUsername() {
        return usernameField.getText();
    }

    // 获取密码
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    // 获取选择的角色
    public String getSelectedRole() {
        if (adminRadioButton.isSelected()) {
            return "admin";
        } else if (tenantRadioButton.isSelected()) {
            return "tenant";
        }
        return "admin"; // 默认返回管理员
    }

    // 清除输入
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        adminRadioButton.setSelected(true);
    }

    // 为登录按钮添加监听器
    public void addLoginButtonListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
    
    // 为注册按钮添加监听器
    public void addRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }
    
    // 为取消按钮添加监听器
    public void addCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
}