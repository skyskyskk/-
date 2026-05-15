// src/view/panels/RentalPanel.java
package view.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import model.House;
import model.RentalRecord;
import model.User;
import service.EstateService;

public class RentalPanel extends JPanel {
    private EstateService service;
    private User currentUser;
    private JTable houseTable, customerTable, activeRecordTable, returnedRecordTable;
    private DefaultTableModel houseModel, customerModel, activeRecordModel, returnedRecordModel;
    private JTextField startDateField, endDateField, rentField, depositField;
    private JComboBox<String> houseComboBox, customerComboBox;

    public RentalPanel(EstateService service) {
        this.service = service;
        this.currentUser = null;
        initUI();
        loadAvailableHouses();
        loadCustomers();
        loadRentalRecords();
    }
    
    public RentalPanel(EstateService service, User currentUser) {
        this.service = service;
        this.currentUser = currentUser;
        initUI();
        loadAvailableHouses();
        if (currentUser != null && "tenant".equals(currentUser.getRole())) {
            // 租客只能看到自己，所以只加载当前租客
            loadCurrentCustomer();
        } else {
            // 管理员可以看到所有客户
            loadCustomers();
        }
        loadRentalRecords();
        updateComponentVisibility();
    }
    
    // 设置当前用户
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        if (currentUser != null && "tenant".equals(currentUser.getRole())) {
            // 租客只能看到自己，所以只加载当前租客
            loadCurrentCustomer();
        } else {
            // 管理员可以看到所有客户
            loadCustomers();
        }
        loadRentalRecords();
        updateComponentVisibility();
    }

    private JPanel customerPanel; // 客户表格面板
    private JLabel customerLabel; // 选择客户标签
    private JPanel rentPanel; // 月租金面板
    private JPanel depositPanel; // 押金面板
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题区域
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("房屋出租记录管理");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 中间主体区域
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // 左侧选择区域
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        // 客户表格（调整到上方）
        customerPanel = new JPanel(new BorderLayout());
        JLabel customerTitle = new JLabel("客户列表");
        customerTitle.setFont(new Font("宋体", Font.BOLD, 14));
        customerPanel.add(customerTitle, BorderLayout.NORTH);
        
        String[] customerColumns = {"客户ID", "姓名", "电话", "身份证"};
        customerModel = new DefaultTableModel(customerColumns, 0);
        customerTable = new JTable(customerModel);
        customerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // 设置列宽
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        // 设置表格高度，使其更紧凑
        customerTable.setRowHeight(20);
        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        customerScrollPane.setPreferredSize(new Dimension(0, 180)); // 设置固定高度
        customerPanel.add(customerScrollPane, BorderLayout.CENTER);
        leftPanel.add(customerPanel);

        // 空闲房屋表格（调整到下方）
        JPanel housePanel = new JPanel(new BorderLayout());
        JLabel houseTitle = new JLabel("空闲房屋列表");
        houseTitle.setFont(new Font("宋体", Font.BOLD, 14));
        housePanel.add(houseTitle, BorderLayout.NORTH);
        
        String[] houseColumns = {"房屋ID", "房号", "地址", "价格"};
        houseModel = new DefaultTableModel(houseColumns, 0);
        houseTable = new JTable(houseModel);
        houseTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // 设置列宽
        houseTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        houseTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        houseTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        houseTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        // 设置表格高度，使其更紧凑
        houseTable.setRowHeight(20);
        JScrollPane houseScrollPane = new JScrollPane(houseTable);
        houseScrollPane.setPreferredSize(new Dimension(0, 180)); // 设置固定高度
        housePanel.add(houseScrollPane, BorderLayout.CENTER);
        leftPanel.add(housePanel);

        mainPanel.add(leftPanel, BorderLayout.WEST);

        // 右侧记录和表单区域
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));

        // 出租记录表格区域（分为两部分）
        JPanel recordsContainer = new JPanel(new GridLayout(2, 1, 10, 10));
        
        // 正在出租的记录
        JPanel activeRecordPanel = new JPanel(new BorderLayout());
        JLabel activeRecordTitle = new JLabel("正在出租的记录");
        activeRecordTitle.setFont(new Font("宋体", Font.BOLD, 14));
        activeRecordPanel.add(activeRecordTitle, BorderLayout.NORTH);
        
        String[] recordColumns = {"记录ID", "房屋ID", "客户ID", "开始日期", "结束日期", "月租金", "押金"};
        activeRecordModel = new DefaultTableModel(recordColumns, 0);
        activeRecordTable = new JTable(activeRecordModel);
        activeRecordTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // 设置列宽
        activeRecordTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        activeRecordTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        activeRecordTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        activeRecordTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        activeRecordTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        activeRecordTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        activeRecordTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        activeRecordPanel.add(new JScrollPane(activeRecordTable), BorderLayout.CENTER);
        recordsContainer.add(activeRecordPanel);
        
        // 已归还的记录
        JPanel returnedRecordPanel = new JPanel(new BorderLayout());
        JLabel returnedRecordTitle = new JLabel("已归还的记录");
        returnedRecordTitle.setFont(new Font("宋体", Font.BOLD, 14));
        returnedRecordPanel.add(returnedRecordTitle, BorderLayout.NORTH);
        
        returnedRecordModel = new DefaultTableModel(recordColumns, 0);
        returnedRecordTable = new JTable(returnedRecordModel);
        returnedRecordTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // 设置列宽
        returnedRecordTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        returnedRecordTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        returnedRecordTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        returnedRecordTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        returnedRecordTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        returnedRecordTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        returnedRecordTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        returnedRecordPanel.add(new JScrollPane(returnedRecordTable), BorderLayout.CENTER);
        recordsContainer.add(returnedRecordPanel);
        
        rightPanel.add(recordsContainer, BorderLayout.CENTER);

        // 底部表单区域
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        JLabel formTitle = new JLabel("添加出租记录");
        formTitle.setFont(new Font("宋体", Font.BOLD, 14));
        formContainer.add(formTitle, BorderLayout.NORTH);
        
        // 使用GridBagLayout实现整齐的两列布局
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 组件之间的内边距
        gbc.anchor = GridBagConstraints.WEST; // 左对齐
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充

        // 房屋ID下拉选择
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("选择房屋:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        houseComboBox = new JComboBox<>();
        houseComboBox.setPreferredSize(new Dimension(180, 25)); // 设置统一宽度
        formPanel.add(houseComboBox, gbc);

        // 客户ID下拉选择
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        customerLabel = new JLabel("选择客户:");
        formPanel.add(customerLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(180, 25)); // 设置统一宽度
        formPanel.add(customerComboBox, gbc);

        // 开始日期
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("开始日期:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        startDateField = new JTextField();
        startDateField.setToolTipText("例如: 2024-01-01");
        startDateField.setPreferredSize(new Dimension(180, 25)); // 设置统一宽度
        formPanel.add(startDateField, gbc);

        // 结束日期
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("结束日期:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        endDateField = new JTextField();
        endDateField.setToolTipText("例如: 2024-12-31");
        endDateField.setPreferredSize(new Dimension(180, 25)); // 设置统一宽度
        formPanel.add(endDateField, gbc);

        // 月租金
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        rentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rentPanel.add(new JLabel("月租金:"));
        formPanel.add(rentPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1;
        gbc.gridwidth = 1;
        rentField = new JTextField();
        rentField.setToolTipText("数字格式");
        rentField.setPreferredSize(new Dimension(180, 25)); // 设置统一宽度
        formPanel.add(rentField, gbc);

        // 月租金单位
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(new JLabel("元"), gbc);

        // 押金
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        depositPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        depositPanel.add(new JLabel("押金:"));
        formPanel.add(depositPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1;
        gbc.gridwidth = 1;
        depositField = new JTextField();
        depositField.setToolTipText("数字格式");
        depositField.setPreferredSize(new Dimension(180, 25)); // 设置统一宽度
        formPanel.add(depositField, gbc);

        // 押金单位
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.weightx = 0;
        formPanel.add(new JLabel("元"), gbc);

        formContainer.add(formPanel, BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("添加出租记录");
        addButton.addActionListener(this::addRentalRecord);
        JButton refreshButton = new JButton("刷新数据");
        refreshButton.addActionListener(e -> {
            loadAvailableHouses();
            loadCustomers();
            loadRentalRecords();
        });
        JButton deleteButton = new JButton("删除选中记录");
        deleteButton.addActionListener(this::deleteSelectedRecord);
        
        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        formContainer.add(buttonPanel, BorderLayout.SOUTH);

        rightPanel.add(formContainer, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
        
        // 初始化组件可见性
        updateComponentVisibility();
    }
    
    // 根据用户角色更新组件可见性
    private void updateComponentVisibility() {
        if (currentUser != null && "tenant".equals(currentUser.getRole())) {
            // 租客隐藏客户表格、客户选择、月租金和押金输入
            customerPanel.setVisible(false);
            customerLabel.setVisible(false);
            customerComboBox.setVisible(false);
            rentPanel.setVisible(false);
            rentField.setVisible(false);
            depositPanel.setVisible(false);
            depositField.setVisible(false);
        } else {
            // 管理员显示所有组件
            customerPanel.setVisible(true);
            customerLabel.setVisible(true);
            customerComboBox.setVisible(true);
            rentPanel.setVisible(true);
            rentField.setVisible(true);
            depositPanel.setVisible(true);
            depositField.setVisible(true);
        }
    }

    private void loadAvailableHouses() {
        houseModel.setRowCount(0);
        houseComboBox.removeAllItems();
        List<House> houses = service.getAvailableHouses();
        for (House house : houses) {
            Object[] row = {
                    house.getHouseId(),
                    house.getHouseNumber(),
                    house.getAddress(),
                    house.getPrice()
            };
            houseModel.addRow(row);
            
            // 添加到房屋下拉框
            houseComboBox.addItem(house.getHouseNumber() + " (ID: " + house.getHouseId() + ")");
        }
    }

    private void loadCustomers() {
        customerModel.setRowCount(0);
        customerComboBox.removeAllItems();
        List<Customer> customers = service.getAllCustomers();
        for (Customer customer : customers) {
            Object[] row = {
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getIdCard()
            };
            customerModel.addRow(row);
            
            // 添加到客户下拉框
            customerComboBox.addItem(customer.getName() + " (ID: " + customer.getCustomerId() + ")");
        }
    }
    
    // 加载当前登录租客的信息
    private void loadCurrentCustomer() {
        customerModel.setRowCount(0);
        customerComboBox.removeAllItems();
        
        if (currentUser != null && "tenant".equals(currentUser.getRole())) {
            // 根据用户名查找对应的客户信息
            Customer currentCustomer = service.getCustomerByUserName(currentUser.getUsername());
            
            if (currentCustomer != null) {
                Object[] row = {
                        currentCustomer.getCustomerId(),
                        currentCustomer.getName(),
                        currentCustomer.getPhone(),
                        currentCustomer.getIdCard()
                };
                customerModel.addRow(row);
                
                // 添加到客户下拉框
                customerComboBox.addItem(currentCustomer.getName() + " (ID: " + currentCustomer.getCustomerId() + ")");
                customerComboBox.setEnabled(false); // 租客不能选择其他客户
            }
        }
    }

    private void loadRentalRecords() {
        // 清空表格数据
        activeRecordModel.setRowCount(0);
        returnedRecordModel.setRowCount(0);
        
        // 获取当前日期用于判断是否已归还
        LocalDate currentDate = LocalDate.now();
        
        List<RentalRecord> records;
        
        // 根据用户角色获取对应的出租记录
        if (currentUser != null && "tenant".equals(currentUser.getRole())) {
            // 租客只能看到自己的记录
            // 查找当前租客对应的客户信息
            Customer currentCustomer = service.getCustomerByUserName(currentUser.getUsername());
            
            if (currentCustomer != null) {
                records = service.getRentalRecordsByCustomerId(currentCustomer.getCustomerId());
            } else {
                records = List.of(); // 如果找不到对应客户，不显示任何记录
            }
        } else {
            // 管理员可以看到所有记录
            records = service.getAllRentalRecords();
        }
        
        for (RentalRecord record : records) {
            Object[] row = {
                    record.getRecordId(),
                    record.getHouseId(),
                    record.getCustomerId(),
                    record.getStartDate(),
                    record.getEndDate(),
                    record.getMonthlyRent(),
                    record.getDeposit()
            };
            
            // 根据结束日期判断是否已归还
            if (record.getEndDate().isAfter(currentDate)) {
                // 未归还，添加到正在出租表格
                activeRecordModel.addRow(row);
            } else {
                // 已归还，添加到已归还表格
                returnedRecordModel.addRow(row);
            }
        }
    }

    private void addRentalRecord(ActionEvent e) {
        try {
            // 从下拉框中获取房屋ID
            String selectedHouse = (String) houseComboBox.getSelectedItem();
            if (selectedHouse == null) {
                JOptionPane.showMessageDialog(this, "请选择房屋");
                return;
            }
            int houseId = Integer.parseInt(selectedHouse.substring(selectedHouse.indexOf("(ID: ") + 5, selectedHouse.indexOf(")")));
            
            int customerId;
            
            if (currentUser != null && "tenant".equals(currentUser.getRole())) {
                // 租客角色：自动获取当前登录用户的客户信息
                Customer currentCustomer = service.getCustomerByUserName(currentUser.getUsername());
                if (currentCustomer == null) {
                    JOptionPane.showMessageDialog(this, "无法获取您的客户信息，请联系管理员！");
                    return;
                }
                customerId = currentCustomer.getCustomerId();
            } else {
                // 管理员角色：从下拉框中获取客户ID
                String selectedCustomer = (String) customerComboBox.getSelectedItem();
                if (selectedCustomer == null) {
                    JOptionPane.showMessageDialog(this, "请选择客户");
                    return;
                }
                customerId = Integer.parseInt(selectedCustomer.substring(selectedCustomer.indexOf("(ID: ") + 5, selectedCustomer.indexOf(")")));
            }
            
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
            LocalDate endDate = LocalDate.parse(endDateField.getText().trim());
            
            BigDecimal monthlyRent;
            BigDecimal deposit;

            if (startDate.isAfter(endDate)) {
                JOptionPane.showMessageDialog(this, "开始日期不能晚于结束日期");
                return;
            }

            if (currentUser != null && "tenant".equals(currentUser.getRole())) {
                // 租客角色：自动从房屋信息获取租金，自动计算押金
                House house = service.getHouseById(houseId);
                if (house == null) {
                    JOptionPane.showMessageDialog(this, "获取房屋信息失败！");
                    return;
                }
                monthlyRent = house.getPrice();
                // 系统统一押金为租金的两倍
                deposit = monthlyRent.multiply(new BigDecimal(2));
                
                // 显示应缴纳的租金和押金信息
                JOptionPane.showMessageDialog(this,
                    "租房信息确认：\n" +
                    "月租金：" + monthlyRent + "元\n" +
                    "押金（租金的2倍）：" + deposit + "元\n" +
                    "租赁期限：" + startDate + " 至 " + endDate + "\n" +
                    "请确认您需要缴纳上述费用",
                    "缴纳信息",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // 管理员角色：手动输入租金和押金
                monthlyRent = new BigDecimal(rentField.getText().trim());
                deposit = new BigDecimal(depositField.getText().trim());
            }

            RentalRecord record = new RentalRecord(houseId, customerId, startDate, endDate, monthlyRent);
            record.setDeposit(deposit);

            if (service.addRentalRecord(record)) {
                JOptionPane.showMessageDialog(this, "出租登记成功！");
                clearForm();
                loadAvailableHouses(); // 刷新空闲房屋列表（已出租房屋会被移除）
                loadRentalRecords();
            } else {
                JOptionPane.showMessageDialog(this, "出租登记失败！");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage());
        }
    }
    
    // 删除选中的出租记录
    private void deleteSelectedRecord(ActionEvent e) {
        // 检查是否为管理员
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "只有管理员才有资格删除记录！");
            return;
        }
        
        // 检查哪个表格有选中行
        int selectedRecordId = -1;
        JTable selectedTable = null;
        
        // 检查正在出租的表格
        int activeRow = activeRecordTable.getSelectedRow();
        if (activeRow != -1) {
            selectedRecordId = (int) activeRecordModel.getValueAt(activeRow, 0);
            selectedTable = activeRecordTable;
        } else {
            // 检查已归还的表格
            int returnedRow = returnedRecordTable.getSelectedRow();
            if (returnedRow != -1) {
                selectedRecordId = (int) returnedRecordModel.getValueAt(returnedRow, 0);
                selectedTable = returnedRecordTable;
            }
        }
        
        if (selectedRecordId == -1) {
            JOptionPane.showMessageDialog(this, "请先选中一条记录！");
            return;
        }
        
        // 确认删除
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "确定要删除记录ID为" + selectedRecordId + "的出租记录吗？", 
            "确认删除", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (service.deleteRentalRecord(selectedRecordId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadRentalRecords(); // 刷新数据
                loadAvailableHouses(); // 刷新可用房屋列表（如果删除的是活跃记录，房屋会变为可用）
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！可能该记录已被修改或删除。");
            }
        }
    }

    private void clearForm() {
        // 重置下拉框选择
        if (houseComboBox.getItemCount() > 0) {
            houseComboBox.setSelectedIndex(0);
        }
        if (customerComboBox.getItemCount() > 0) {
            customerComboBox.setSelectedIndex(0);
        }
        
        startDateField.setText("");
        endDateField.setText("");
        rentField.setText("");
        depositField.setText("");
    }
}