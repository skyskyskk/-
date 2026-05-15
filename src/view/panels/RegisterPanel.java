// src/view/panels/RegisterPanel.java
package view.panels;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class RegisterPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField realNameField;
    private JTextField phoneField;
    private JTextField idCardField;
    private JTextField emailField;
    private JTextField workUnitField;
    private JButton registerButton;
    private JButton backButton;

    public RegisterPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题标签
        JLabel titleLabel = new JLabel("租客注册", SwingConstants.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 32));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(new JLabel("用户名:"), gbc);
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("密码:"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("确认密码:"), gbc);
        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        // 真实姓名
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("真实姓名:"), gbc);
        realNameField = new JTextField(20);
        gbc.gridx = 1;
        add(realNameField, gbc);

        // 电话号码
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("电话号码:"), gbc);
        phoneField = new JTextField(20);
        gbc.gridx = 1;
        add(phoneField, gbc);

        // 身份证号码
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("身份证号码:"), gbc);
        idCardField = new JTextField(20);
        gbc.gridx = 1;
        add(idCardField, gbc);

        // 邮箱
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("邮箱:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        add(emailField, gbc);

        // 工作单位
        gbc.gridx = 0;
        gbc.gridy = 8;
        add(new JLabel("工作单位:"), gbc);
        workUnitField = new JTextField(20);
        gbc.gridx = 1;
        add(workUnitField, gbc);

        // 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        registerButton = new JButton("注册");
        registerButton.setFont(new Font("宋体", Font.PLAIN, 16));
        backButton = new JButton("返回登录");
        backButton.setFont(new Font("宋体", Font.PLAIN, 16));

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        gbc.gridx = 0;
        gbc.gridy = 9;
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

    // 获取确认密码
    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    // 获取真实姓名
    public String getRealName() {
        return realNameField.getText();
    }

    // 获取电话号码
    public String getPhone() {
        return phoneField.getText();
    }

    // 获取身份证号码
    public String getIdCard() {
        return idCardField.getText();
    }

    // 获取邮箱
    public String getEmail() {
        return emailField.getText();
    }

    // 获取工作单位
    public String getWorkUnit() {
        return workUnitField.getText();
    }

    // 清除输入
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        realNameField.setText("");
        phoneField.setText("");
        idCardField.setText("");
        emailField.setText("");
        workUnitField.setText("");
    }

    // 为注册按钮添加监听器
    public void addRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    // 为返回按钮添加监听器
    public void addBackButtonListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
}
