// src/view/panels/ReturnPanel.java
package view.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import model.RentalRecord;
import model.User;
import service.EstateService;

public class ReturnPanel extends JPanel {
    private EstateService service;
    private User currentUser;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField recordIdField;

    public ReturnPanel(EstateService service) {
        this.service = service;
        this.currentUser = null;
        initUI();
        loadAllRentalRecords();
    }
    
    // 设置当前用户
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        loadAllRentalRecords();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 表格区域（显示所有出租记录）
        String[] columns = {"记录ID", "房屋ID", "客户ID", "开始日期", "结束日期", "月租金", "状态"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 表单区域
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        formPanel.add(new JLabel("请输入要归还的记录ID:"));
        recordIdField = new JTextField(10);
        formPanel.add(recordIdField);

        JButton returnButton = new JButton("确认归还");
        returnButton.addActionListener(this::processReturn);
        formPanel.add(returnButton);

        JButton refreshButton = new JButton("刷新列表");
        refreshButton.addActionListener(e -> loadAllRentalRecords());
        formPanel.add(refreshButton);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadAllRentalRecords() {
        tableModel.setRowCount(0);
        List<RentalRecord> records;
        LocalDate currentDate = LocalDate.now();
        
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
            String status = record.getEndDate().isAfter(currentDate) ? "租赁中" : "已归还";
            Object[] row = {
                    record.getRecordId(),
                    record.getHouseId(),
                    record.getCustomerId(),
                    record.getStartDate(),
                    record.getEndDate(),
                    record.getMonthlyRent(),
                    status
            };
            tableModel.addRow(row);
        }
    }

    private void processReturn(ActionEvent e) {
        try {
            int recordId = Integer.parseInt(recordIdField.getText().trim());

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确定要归还该房屋吗？",
                    "确认",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // 调用service层的归还处理方法
                if (service.returnHouse(recordId)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "归还登记已完成！\n系统已自动将房屋状态更新为空闲",
                            "成功",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    recordIdField.setText("");
                loadAllRentalRecords();
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "归还失败！该记录可能不存在或已过期",
                            "错误",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的记录ID");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage());
        }
    }
}