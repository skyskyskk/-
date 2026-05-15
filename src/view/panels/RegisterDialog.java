package view.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton cancelButton;
    private boolean registered = false;
    
    public RegisterDialog(Frame owner) {
        super(owner, "租客注册", true);
        initUI();
        setSize(400, 300);
        setLocationRelativeTo(owner);
    }
    
    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 用户名标签和输入框
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);
        
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);
        
        // 密码标签和输入框
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);
        
        // 确认密码标签和输入框
        JLabel confirmPasswordLabel = new JLabel("确认密码:");
        confirmPasswordLabel.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(confirmPasswordLabel, gbc);
        
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("宋体", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(confirmPasswordField, gbc);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        registerButton = new JButton("注册");
        registerButton.setFont(new Font("宋体", Font.PLAIN, 16));
        cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("宋体", Font.PLAIN, 16));
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
        
        // 取消按钮监听器
        cancelButton.addActionListener(e -> dispose());
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
    
    // 为注册按钮添加监听器
    public void addRegisterButtonListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }
    
    // 设置注册状态
    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
    
    // 获取注册状态
    public boolean isRegistered() {
        return registered;
    }
    
    // 清除输入
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }
}