// src/view/panels/LandlordPanel.java
package view.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import model.Landlord;
import service.EstateService;

public class LandlordPanel extends JPanel {
    private EstateService service;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, phoneField, idCardField, emailField, addressField;

    public LandlordPanel(EstateService service) {
        this.service = service;
        initUI();
        loadLandlords();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 表格区域
        String[] columns = {"ID", "姓名", "电话", "身份证", "邮箱", "地址"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // ID列不可编辑
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
                    
                    // 获取房东ID
                    int landlordId = (int) tableModel.getValueAt(row, 0);
                    
                    // 构建房东对象
                    Landlord landlord = new Landlord();
                    landlord.setLandlordId(landlordId);
                    landlord.setName((String) tableModel.getValueAt(row, 1));
                    landlord.setPhone((String) tableModel.getValueAt(row, 2));
                    landlord.setIdCard((String) tableModel.getValueAt(row, 3));
                    landlord.setEmail((String) tableModel.getValueAt(row, 4));
                    landlord.setAddress((String) tableModel.getValueAt(row, 5));
                    
                    // 更新到数据库
                    if (service.updateLandlord(landlord)) {
                        System.out.println("更新成功");
                    } else {
                        JOptionPane.showMessageDialog(LandlordPanel.this, "更新失败！");
                        // 刷新表格，恢复原值
                        loadLandlords();
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

        formPanel.add(new JLabel("地址:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("添加房东");
        addButton.addActionListener(this::addLandlord);
        JButton deleteButton = new JButton("删除选中");
        deleteButton.addActionListener(this::deleteSelectedLandlord);
        JButton refreshButton = new JButton("刷新列表");
        refreshButton.addActionListener(e -> loadLandlords());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadLandlords() {
        // 清空表格
        tableModel.setRowCount(0);

        // 加载数据
        List<Landlord> landlords = service.getAllLandlords();
        for (Landlord landlord : landlords) {
            Object[] row = {
                    landlord.getLandlordId(),
                    landlord.getName(),
                    landlord.getPhone(),
                    landlord.getIdCard(),
                    landlord.getEmail(),
                    landlord.getAddress()
            };
            tableModel.addRow(row);
        }
    }

    private void addLandlord(ActionEvent e) {
        try {
            Landlord landlord = new Landlord();
            landlord.setName(nameField.getText());
            landlord.setPhone(phoneField.getText());
            landlord.setIdCard(idCardField.getText());
            landlord.setEmail(emailField.getText());
            landlord.setAddress(addressField.getText());

            if (service.addLandlord(landlord)) {
                JOptionPane.showMessageDialog(this, "添加成功！");
                // 清空表单
                clearForm();
                // 刷新列表
                loadLandlords();
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
        addressField.setText("");
    }
    
    private void deleteSelectedLandlord(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的房东！");
            return;
        }
        
        // 获取选中行的房东ID
        int landlordId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        
        // 确认删除
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "确定要删除房东\"" + name + "\"吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (service.deleteLandlord(landlordId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadLandlords();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！该房东可能有相关房屋信息。");
            }
        }
    }
}