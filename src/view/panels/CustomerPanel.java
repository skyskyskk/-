package view.panels;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import service.EstateService;

public class CustomerPanel extends JPanel {
    private EstateService service;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, phoneField, idCardField, emailField, workUnitField;

    public CustomerPanel(EstateService service) {
        this.service = service;
        initUI();
        loadCustomers();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 表格区域
        String[] columns = {"客户ID", "姓名", "电话", "身份证", "邮箱", "工作单位"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 客户ID不可编辑
                return column != 0;
            }
        };
        table = new JTable(tableModel);
        
        // 添加表格模型监听器，处理单元格编辑
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    
                    // 获取修改后的值
                    Object value = tableModel.getValueAt(row, column);
                    
                    // 获取客户ID
                    int customerId = (int) tableModel.getValueAt(row, 0);
                    
                    // 构建客户对象
                    Customer customer = new Customer();
                    customer.setCustomerId(customerId);
                    customer.setName((String) tableModel.getValueAt(row, 1));
                    customer.setPhone((String) tableModel.getValueAt(row, 2));
                    customer.setIdCard((String) tableModel.getValueAt(row, 3));
                    customer.setEmail((String) tableModel.getValueAt(row, 4));
                    customer.setWorkUnit((String) tableModel.getValueAt(row, 5));
                    
                    // 更新到数据库
                    if (service.updateCustomer(customer)) {
                        System.out.println("更新成功");
                    } else {
                        JOptionPane.showMessageDialog(CustomerPanel.this, "更新失败！");
                        // 刷新表格，恢复原值
                        loadCustomers();
                    }
                }
            }
        });
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 表单区域
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));

        formPanel.add(new JLabel("姓名:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("电话:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("身份证:"));
        idCardField = new JTextField();
        formPanel.add(idCardField);

        formPanel.add(new JLabel("邮箱:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("工作单位:"));
        workUnitField = new JTextField();
        formPanel.add(workUnitField);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("添加客户");
        addButton.addActionListener(this::addCustomer);
        JButton deleteButton = new JButton("删除选中");
        deleteButton.addActionListener(this::deleteSelectedCustomer);
        JButton refreshButton = new JButton("刷新列表");
        refreshButton.addActionListener(e -> loadCustomers());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        List<Customer> customers = service.getAllCustomers();
        for (Customer customer : customers) {
            Object[] row = {
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getIdCard(),
                    customer.getEmail(),
                    customer.getWorkUnit()
            };
            tableModel.addRow(row);
        }
    }

    private void addCustomer(ActionEvent e) {
        try {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String idCard = idCardField.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || idCard.isEmpty()) {
                JOptionPane.showMessageDialog(this, "姓名、电话和身份证不能为空");
                return;
            }

            Customer customer = new Customer(name, phone, idCard);
            customer.setEmail(emailField.getText().trim());
            customer.setWorkUnit(workUnitField.getText().trim());

            if (service.addCustomer(customer)) {
                JOptionPane.showMessageDialog(this, "添加成功！");
                clearForm();
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "添加失败！");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage());
        }
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        idCardField.setText("");
        emailField.setText("");
        workUnitField.setText("");
    }
    
    private void deleteSelectedCustomer(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的客户！");
            return;
        }
        
        // 获取选中行的客户ID
        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        
        // 检查是否有对应的用户记录
        String username = service.getUsernameByCustomerId(customerId);
        boolean hasUser = username != null;
        
        int confirm;
        boolean deleteUser = false;
        
        if (hasUser) {
            // 如果有对应的用户记录，询问是否同时删除用户
            Object[] options = {"仅删除客户", "同时删除客户和用户", "取消"};
            confirm = JOptionPane.showOptionDialog(
                this,
                "客户\"" + name + "\"有对应的用户记录\n用户名：" + username + "\n\n是否同时删除该用户？",
                "确认删除",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (confirm == JOptionPane.CANCEL_OPTION) {
                return; // 用户取消操作
            }
            
            deleteUser = (confirm == 1); // 如果选择第二个选项，同时删除用户
        } else {
            // 如果没有对应的用户记录，只询问是否删除客户
            confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要删除客户\"" + name + "\"吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        boolean success = true;
        
        if (deleteUser) {
            // 同时删除用户和客户
            success = service.deleteUserAndCustomer(username);
        } else {
            // 仅删除客户
            success = service.deleteCustomer(customerId);
        }
        
        if (success) {
            JOptionPane.showMessageDialog(this, deleteUser ? "客户和用户都删除成功！" : "客户删除成功！");
            loadCustomers();
        } else {
            JOptionPane.showMessageDialog(this, "删除失败！该客户可能有相关出租记录。");
        }
    }
}