// src/view/panels/HousePanel.java
package view.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import model.House;
import model.HouseType;
import model.Landlord;
import service.EstateService;

public class HousePanel extends JPanel {
    private EstateService service;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField houseNumberField, addressField, areaField, priceField, descriptionField;
    private JComboBox<String> typeComboBox, landlordComboBox, statusComboBox;

    public HousePanel(EstateService service) {
        this.service = service;
        initUI();
        loadHouses();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 表格区域
        String[] columns = {"房屋ID", "房号", "地址", "户型ID", "房东ID", "面积", "价格", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 房屋ID不可编辑
                return column != 0;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // 设置列数据类型，特别是数值类型
                if (columnIndex == 5 || columnIndex == 6) { // 面积和价格列
                    return BigDecimal.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        table = new JTable(tableModel);
        
        // 设置状态列的编辑器为下拉框
        JComboBox<String> statusEditor = new JComboBox<>();
        statusEditor.addItem("空闲");
        statusEditor.addItem("已出租");
        statusEditor.addItem("维修中");
        table.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(statusEditor));
        
        // 添加表格模型监听器，处理单元格编辑
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    
                    // 获取修改后的值
                    Object value = tableModel.getValueAt(row, column);
                    
                    // 获取房屋ID
                    int houseId = (int) tableModel.getValueAt(row, 0);
                    
                    try {
                        // 构建房屋对象
                        House house = new House();
                        house.setHouseId(houseId);
                        house.setHouseNumber((String) tableModel.getValueAt(row, 1));
                        house.setAddress((String) tableModel.getValueAt(row, 2));
                        
                        // 转换类型ID
                        Object typeIdObj = tableModel.getValueAt(row, 3);
                        int typeId = typeIdObj instanceof Integer ? (Integer) typeIdObj : Integer.parseInt(typeIdObj.toString());
                        house.setTypeId(typeId);
                        
                        // 转换房东ID
                        Object landlordIdObj = tableModel.getValueAt(row, 4);
                        int landlordId = landlordIdObj instanceof Integer ? (Integer) landlordIdObj : Integer.parseInt(landlordIdObj.toString());
                        house.setLandlordId(landlordId);
                        
                        // 转换面积
                        Object areaObj = tableModel.getValueAt(row, 5);
                        BigDecimal area = areaObj instanceof BigDecimal ? (BigDecimal) areaObj : new BigDecimal(areaObj.toString());
                        house.setArea(area);
                        
                        // 转换价格
                        Object priceObj = tableModel.getValueAt(row, 6);
                        BigDecimal price = priceObj instanceof BigDecimal ? (BigDecimal) priceObj : new BigDecimal(priceObj.toString());
                        house.setPrice(price);
                        
                        house.setStatus((String) tableModel.getValueAt(row, 7));
                        
                        // 更新到数据库
                        if (service.updateHouse(house)) {
                            System.out.println("更新成功");
                        } else {
                            JOptionPane.showMessageDialog(HousePanel.this, "更新失败！");
                            // 刷新表格，恢复原值
                            loadHouses();
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(HousePanel.this, "数字格式错误，请检查输入");
                        // 刷新表格，恢复原值
                        loadHouses();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(HousePanel.this, "更新失败: " + ex.getMessage());
                        // 刷新表格，恢复原值
                        loadHouses();
                    }
                }
            }
        });
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 表单区域
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 5, 5));

        formPanel.add(new JLabel("房号:"));
        houseNumberField = new JTextField();
        formPanel.add(houseNumberField);

        formPanel.add(new JLabel("地址:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        // 户型下拉选择
        formPanel.add(new JLabel("户型:"));
        typeComboBox = new JComboBox<>();
        formPanel.add(typeComboBox);

        // 房东下拉选择
        formPanel.add(new JLabel("房东:"));
        landlordComboBox = new JComboBox<>();
        formPanel.add(landlordComboBox);
        
        // 加载户型和房东数据到下拉框
        loadComboBoxData();

        formPanel.add(new JLabel("面积:"));
        areaField = new JTextField();
        formPanel.add(areaField);

        formPanel.add(new JLabel("价格:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        formPanel.add(new JLabel("状态:"));
        statusComboBox = new JComboBox<>();
        // 添加预定义状态选项
        statusComboBox.addItem("空闲");
        statusComboBox.addItem("已出租");
        statusComboBox.addItem("维修中");
        formPanel.add(statusComboBox);

        formPanel.add(new JLabel("描述:"));
        descriptionField = new JTextField();
        formPanel.add(descriptionField);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("添加房屋");
        addButton.addActionListener(this::addHouse);
        JButton deleteButton = new JButton("删除选中");
        deleteButton.addActionListener(this::deleteSelectedHouse);
        JButton refreshButton = new JButton("刷新列表");
        refreshButton.addActionListener(e -> loadHouses());
        JButton availableButton = new JButton("查看空闲房屋");
        availableButton.addActionListener(e -> loadAvailableHouses());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(availableButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadHouses() {
        tableModel.setRowCount(0);
        List<House> houses = service.getAllHouses();
        for (House house : houses) {
            Object[] row = {
                    house.getHouseId(),
                    house.getHouseNumber(),
                    house.getAddress(),
                    house.getTypeId(),
                    house.getLandlordId(),
                    house.getArea(),
                    house.getPrice(),
                    house.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void loadAvailableHouses() {
        tableModel.setRowCount(0);
        List<House> houses = service.getAvailableHouses();
        for (House house : houses) {
            Object[] row = {
                    house.getHouseId(),
                    house.getHouseNumber(),
                    house.getAddress(),
                    house.getTypeId(),
                    house.getLandlordId(),
                    house.getArea(),
                    house.getPrice(),
                    house.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    public void loadComboBoxData() {
        // 先清除现有数据
        typeComboBox.removeAllItems();
        landlordComboBox.removeAllItems();
        
        // 加载户型数据
        List<HouseType> houseTypes = service.getAllHouseTypes();
        for (HouseType type : houseTypes) {
            typeComboBox.addItem(type.getTypeName() + " (ID: " + type.getTypeId() + ")");
        }
        
        // 加载房东数据
        List<Landlord> landlords = service.getAllLandlords();
        for (Landlord landlord : landlords) {
            landlordComboBox.addItem(landlord.getName() + " (ID: " + landlord.getLandlordId() + ")");
        }
    }
    
    private void addHouse(ActionEvent e) {
        try {
            String houseNumber = houseNumberField.getText().trim();
            String address = addressField.getText().trim();
            
            // 从下拉框中获取选中的户型ID
            String selectedType = (String) typeComboBox.getSelectedItem();
            int typeId = Integer.parseInt(selectedType.substring(selectedType.indexOf("(ID: ") + 5, selectedType.indexOf(")")));
            
            // 从下拉框中获取选中的房东ID
            String selectedLandlord = (String) landlordComboBox.getSelectedItem();
            int landlordId = Integer.parseInt(selectedLandlord.substring(selectedLandlord.indexOf("(ID: ") + 5, selectedLandlord.indexOf(")")));
            
            BigDecimal area = new BigDecimal(areaField.getText().trim());
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            String status = (String) statusComboBox.getSelectedItem();

            if (houseNumber.isEmpty() || address.isEmpty() || status.isEmpty()) {
                JOptionPane.showMessageDialog(this, "房号、地址和状态不能为空");
                return;
            }

            House house = new House(houseNumber, address, typeId, landlordId, area, price, status);
            house.setDescription(descriptionField.getText().trim());

            if (service.addHouse(house)) {
                JOptionPane.showMessageDialog(this, "添加成功！");
                clearForm();
                loadHouses();
            } else {
                JOptionPane.showMessageDialog(this, "添加失败！");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "数字格式错误，请检查输入");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage());
        }
    }

    private void clearForm() {
        houseNumberField.setText("");
        addressField.setText("");
        typeComboBox.setSelectedIndex(0); // 重置为第一个选项
        landlordComboBox.setSelectedIndex(0); // 重置为第一个选项
        areaField.setText("");
        priceField.setText("");
        statusComboBox.setSelectedIndex(0); // 重置为第一个选项（空闲）
        descriptionField.setText("");
    }
    
    private void deleteSelectedHouse(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的房屋！");
            return;
        }
        
        // 获取选中行的房屋ID
        int houseId = (int) tableModel.getValueAt(selectedRow, 0);
        String houseNumber = (String) tableModel.getValueAt(selectedRow, 1);
        
        // 确认删除
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "确定要删除房屋\"" + houseNumber + "\"吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (service.deleteHouse(houseId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadHouses();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！该房屋可能有相关出租记录。");
            }
        }
    }
}