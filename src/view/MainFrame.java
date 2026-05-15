// src/view/MainFrame.java
package view;

import java.awt.*;
import javax.swing.*;
import model.User;
import service.EstateService;
import view.panels.*;

public class MainFrame extends JFrame {
    private EstateService estateService = new EstateService();
    private CardLayout cardLayout = new CardLayout();
    private JPanel contentPanel;
    private HousePanel housePanel; // 将HousePanel引用设为类成员变量
    private RentalPanel rentalPanel; // 将RentalPanel引用设为类成员变量
    private ReturnPanel returnPanel; // 将ReturnPanel引用设为类成员变量
    private UserPanel userPanel; // 将UserPanel引用设为类成员变量
    private JPanel buttonPanel; // 将按钮面板设为类成员变量，以便控制可见性
    private JButton[] functionButtons; // 功能按钮数组，用于权限控制
    private User currentUser; // 存储当前登录用户信息

    public MainFrame() {
        // 窗口基本设置
        setTitle("二手房中介管理系统");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        // 创建主面板
        initUI();
    }

    private void initUI() {
        // 创建顶部面板
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false); // 设置面板透明，显示背景图片
        topPanel.add(new JLabel("二手房中介管理系统 v1.0"));

        // 创建功能按钮面板
        buttonPanel = new JPanel(new GridLayout(10, 1, 5, 5)); // 修改为10行
        buttonPanel.setOpaque(false); // 设置面板透明，显示背景图片
        String[] functions = {
                "房屋户型管理", "房东信息管理", "客户信息管理",
                "用户信息管理", // 添加用户信息管理功能
                "房屋信息管理", "房屋出租登记", "房屋归还登记",
                "租房收费管理", "统计户型出租数量", "查看房屋视图"
        };
        
        functionButtons = new JButton[functions.length];
        for (int i = 0; i < functions.length; i++) {
            String func = functions[i];
            JButton button = new JButton(func);
            button.addActionListener(e -> showPanel(func));
            buttonPanel.add(button);
            functionButtons[i] = button;
        }

        // 创建内容面板（卡片布局）
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false); // 设置面板透明，显示背景图片
        
        // 添加登录面板
        LoginPanel loginPanel = new LoginPanel();
        loginPanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(loginPanel, "登录");
        
        // 添加注册面板
        RegisterPanel registerPanel = new RegisterPanel();
        registerPanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(registerPanel, "注册");
        
        // 添加各个功能面板并设置透明背景
        WelcomePanel welcomePanel = new WelcomePanel();
        welcomePanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(welcomePanel, "欢迎");
        
        HouseTypePanel houseTypePanel = new HouseTypePanel(estateService);
        houseTypePanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(houseTypePanel, "房屋户型管理");
        
        LandlordPanel landlordPanel = new LandlordPanel(estateService);
        landlordPanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(landlordPanel, "房东信息管理");
        
        CustomerPanel customerPanel = new CustomerPanel(estateService);
        customerPanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(customerPanel, "客户信息管理");
        
        housePanel = new HousePanel(estateService);
        housePanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(housePanel, "房屋信息管理");
        
        rentalPanel = new RentalPanel(estateService);
        rentalPanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(rentalPanel, "房屋出租登记");
        
        returnPanel = new ReturnPanel(estateService);
        returnPanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(returnPanel, "房屋归还登记");
        
        PaymentPanel paymentPanel = new PaymentPanel(estateService);
        paymentPanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(paymentPanel, "租房收费管理");
        
        StatisticPanel statisticPanel = new StatisticPanel(estateService);
        statisticPanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(statisticPanel, "统计户型出租数量");
        
        HouseViewPanel houseViewPanel = new HouseViewPanel(estateService);
        houseViewPanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(houseViewPanel, "查看房屋视图");
        
        // 添加用户管理面板
        userPanel = new UserPanel(estateService);
        userPanel.setOpaque(false); // 设置面板透明，显示背景图片
        contentPanel.add(userPanel, "用户信息管理");

        // 创建背景面板
        BackgroundPanel backgroundPanel = new BackgroundPanel(new BorderLayout());
        
        // 设置默认背景图片（如果存在）
        backgroundPanel.setBackgroundImage("images/background.jpg");
        
        // 将其他面板添加到背景面板中
        backgroundPanel.add(topPanel, BorderLayout.NORTH);
        backgroundPanel.add(buttonPanel, BorderLayout.WEST);
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);

        // 设置主窗口内容面板
        setContentPane(backgroundPanel);

        // 初始显示登录面板
        showPanel("登录");
        
        // 默认隐藏功能按钮面板，登录后根据权限显示
        buttonPanel.setVisible(false);

        // 登录按钮监听器
        loginPanel.addLoginButtonListener(e -> {
            String username = loginPanel.getUsername();
            String password = loginPanel.getPassword();
            String selectedRole = loginPanel.getSelectedRole();
            
            // 验证输入
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "用户名和密码不能为空！", "登录失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 调用服务层进行登录验证
            User user = estateService.login(username, password);
            
            if (user != null) {
                // 验证角色是否匹配
                if (user.getRole().equals(selectedRole)) {
                    currentUser = user; // 存储当前登录用户
                    
                    JOptionPane.showMessageDialog(this, "登录成功！", "登录成功", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 根据角色设置功能按钮的可见性
                    setFunctionButtonsVisibility(user.getRole());
                    
                    // 更新面板的用户信息
                    rentalPanel.setCurrentUser(currentUser);
                    returnPanel.setCurrentUser(currentUser);
                    
                    // 显示功能按钮面板和欢迎面板
                    buttonPanel.setVisible(true);
                    showPanel("欢迎");
                    
                    // 更新窗口标题显示当前用户信息
                    setTitle("二手房中介管理系统 v1.0 - " + ("admin".equals(user.getRole()) ? "管理员" : "租客") + ": " + username);
                } else {
                    JOptionPane.showMessageDialog(this, "角色选择错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // 取消按钮监听器
        loginPanel.addCancelButtonListener(e -> {
            loginPanel.clearFields();
        });
        
        // 注册按钮监听器
        loginPanel.addRegisterButtonListener(e -> {
            // 显示注册面板
            cardLayout.show(contentPanel, "注册");
        });
        
        // 注册面板的注册按钮监听器
        registerPanel.addRegisterButtonListener(e -> {
            String username = registerPanel.getUsername();
            String password = registerPanel.getPassword();
            String confirmPassword = registerPanel.getConfirmPassword();
            String realName = registerPanel.getRealName();
            String phone = registerPanel.getPhone();
            String idCard = registerPanel.getIdCard();
            String email = registerPanel.getEmail();
            String workUnit = registerPanel.getWorkUnit();
            
            // 验证输入
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || realName.isEmpty() || phone.isEmpty() || idCard.isEmpty()) {
                JOptionPane.showMessageDialog(this, "带*号的字段不能为空！", "注册失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "两次输入的密码不一致！", "注册失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 调用服务层进行注册
            boolean success = estateService.register(username, password, realName, phone, idCard, email, workUnit);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "注册成功！", "注册成功", JOptionPane.INFORMATION_MESSAGE);
                // 注册成功后返回登录面板
                cardLayout.show(contentPanel, "登录");
                registerPanel.clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "注册失败！用户名可能已存在。", "注册失败", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // 注册面板的返回按钮监听器
        registerPanel.addBackButtonListener(e -> {
            // 返回登录面板
            cardLayout.show(contentPanel, "登录");
            registerPanel.clearFields();
        });

        setVisible(true);
    }

    // 显示对应的功能面板
    private void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        
        // 当切换到房屋信息管理面板时，刷新下拉选择框数据
        if ("房屋信息管理".equals(panelName)) {
            housePanel.loadComboBoxData();
        }
    }
    
    /**
     * 根据用户角色设置功能按钮的可见性
     * @param role 用户角色 (admin/tenant)
     */
    private void setFunctionButtonsVisibility(String role) {
        // 管理员可以使用所有功能
        if ("admin".equals(role)) {
            for (JButton button : functionButtons) {
                button.setVisible(true);
            }
        } 
        // 租客只能使用部分功能
        else if ("tenant".equals(role)) {
            for (int i = 0; i < functionButtons.length; i++) {
                // 租客只能查看房屋出租登记、房屋归还登记、房屋视图
                if (i == 5 || i == 6 || i == 9) { // 索引对应"房屋出租登记"、"房屋归还登记"和"查看房屋视图"
                    functionButtons[i].setVisible(true);
                } else {
                    functionButtons[i].setVisible(false);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}