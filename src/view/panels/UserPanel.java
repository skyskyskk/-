package view.panels;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import model.User;
import service.EstateService;

public class UserPanel extends JPanel {
    private EstateService service;
    private JTable table;
    private DefaultTableModel tableModel;

    public UserPanel(EstateService service) {
        this.service = service;
        initUI();
        loadUsers();
    }

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JComboBox<String> customerComboBox;

    private void initUI() {
        setLayout(new BorderLayout());

        // 表格区域
        String[] columns = {"用户名", "密码", "角色"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 所有单元格不可编辑
                return false;
            }
        };
        table = new JTable(tableModel);
        
        // 设置密码列显示为星号
        table.getColumnModel().getColumn(2).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel();
            if (value != null) {
                label.setText("••••••••"); // 显示为星号
            }
            label.setOpaque(true);
            label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            label.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            label.setBorder(UIManager.getBorder("Table.cellNoFocusBorder"));
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        });
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 添加用户表单区域
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("添加用户"));
        
        formPanel.add(new JLabel("用户名:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);
        
        formPanel.add(new JLabel("密码:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        
        formPanel.add(new JLabel("角色:"));
        roleComboBox = new JComboBox<>(new String[]{"tenant", "admin"});
        formPanel.add(roleComboBox);
        
        formPanel.add(new JLabel("对应客户ID:"));
        customerComboBox = new JComboBox<>();
        loadCustomers(); // 加载客户列表
        formPanel.add(customerComboBox);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("添加用户");
        addButton.addActionListener(this::addUser);
        JButton deleteButton = new JButton("删除选中用户");
        deleteButton.addActionListener(this::deleteSelectedUser);
        JButton refreshButton = new JButton("刷新列表");
        refreshButton.addActionListener(e -> {
            loadUsers();
            loadCustomers();
        });
        JButton deleteUserAndCustomerButton = new JButton("删除用户及对应客户");
        deleteUserAndCustomerButton.addActionListener(this::deleteUserAndCustomer);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(deleteUserAndCustomerButton);
        buttonPanel.add(refreshButton);

        // 南部面板
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = service.getAllUsers();
        for (User user : users) {
            Object[] row = {
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * 加载客户列表
     */
    private void loadCustomers() {
        customerComboBox.removeAllItems();
        customerComboBox.addItem("无对应客户"); // 默认选项
        
        // 获取所有客户
        List<Customer> customers = service.getAllCustomers();
        for (Customer customer : customers) {
            customerComboBox.addItem(customer.getCustomerId() + " - " + customer.getName());
        }
    }
    
    /**
     * 添加用户
     */
    private void addUser(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();
        String customerSelection = (String) customerComboBox.getSelectedItem();
        Integer customerId = null;
        
        // 验证输入
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空！");
            return;
        }
        
        // 解析客户ID
        if (!"无对应客户".equals(customerSelection)) {
            customerId = Integer.parseInt(customerSelection.split(" - ")[0]);
        }
        
        // 添加用户
        if (service.addUserByAdmin(username, password, role, customerId)) {
            JOptionPane.showMessageDialog(this, "用户添加成功！");
            // 清空输入
            usernameField.setText("");
            passwordField.setText("");
            roleComboBox.setSelectedIndex(0);
            customerComboBox.setSelectedIndex(0);
            // 刷新列表
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "用户添加失败！");
        }
    }

    private void deleteSelectedUser(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的用户！");
            return;
        }
        
        // 获取选中行的用户信息
        String username = (String) tableModel.getValueAt(selectedRow, 0);
        String role = (String) tableModel.getValueAt(selectedRow, 2);
        
        // 不允许删除管理员用户
        if ("admin".equals(role)) {
            JOptionPane.showMessageDialog(this, "不能删除管理员用户！");
            return;
        }
        
        // 确认删除
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "确定要删除用户\"" + username + "\"吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (service.deleteUserByUsername(username)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！该用户对应的客户可能有租房订单！");
            }
        }
    }
    
    private void deleteUserAndCustomer(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的用户！");
            return;
        }
        
        // 获取选中行的用户信息
        String username = (String) tableModel.getValueAt(selectedRow, 0);
        String role = (String) tableModel.getValueAt(selectedRow, 2);
        
        // 不允许删除管理员用户
        if ("admin".equals(role)) {
            JOptionPane.showMessageDialog(this, "不能删除管理员用户！");
            return;
        }
        
        // 确认删除
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "确定要删除用户\"" + username + "\"及其对应的客户记录吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (service.deleteUserAndCustomer(username)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！");
            }
        }
    }
}
