package view.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.HouseType;
import service.EstateService;

public class HouseTypePanel extends JPanel {
    private final EstateService estateService;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField tfTypeName;
    private JTextArea taDescription;

    public HouseTypePanel(EstateService estateService) {
        this.estateService = estateService;
        initUI();
        loadHouseTypeData();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 表格区域
        String[] columns = {"户型ID", "户型名称", "户型描述", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 表单区域
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));

        formPanel.add(new JLabel("户型名称:"));
        tfTypeName = new JTextField();
        formPanel.add(tfTypeName);

        formPanel.add(new JLabel("户型描述:"));
        taDescription = new JTextArea(3, 20);
        taDescription.setLineWrap(true);
        formPanel.add(new JScrollPane(taDescription));

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("新增户型");
        btnAdd.addActionListener(this::addHouseType);
        JButton btnDelete = new JButton("删除选中");
        btnDelete.addActionListener(this::deleteSelectedHouseType);
        JButton btnRefresh = new JButton("刷新列表");
        btnRefresh.addActionListener(e -> loadHouseTypeData());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadHouseTypeData() {
        // 清空表格
        tableModel.setRowCount(0);

        // 加载数据
        List<HouseType> houseTypeList = estateService.getAllHouseTypes();
        for (HouseType type : houseTypeList) {
            Object[] row = {
                    type.getTypeId(),
                    type.getTypeName(),
                    type.getDescription(),
                    type.getCreatedAt()
            };
            tableModel.addRow(row);
        }
    }

    private void addHouseType(ActionEvent e) {
        try {
            String typeName = tfTypeName.getText().trim();
            String description = taDescription.getText().trim();

            if (typeName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "户型名称不能为空！");
                return;
            }

            HouseType newHouseType = new HouseType();
            newHouseType.setTypeName(typeName);
            newHouseType.setDescription(description);

            if (estateService.addHouseType(newHouseType)) {
                JOptionPane.showMessageDialog(this, "添加成功！");
                clearForm();
                loadHouseTypeData();
            } else {
                JOptionPane.showMessageDialog(this, "添加失败！");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage());
        }
    }

    private void clearForm() {
        tfTypeName.setText("");
        taDescription.setText("");
    }
    
    private void deleteSelectedHouseType(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的户型！");
            return;
        }
        
        // 获取选中行的户型ID
        int typeId = (int) tableModel.getValueAt(selectedRow, 0);
        String typeName = (String) tableModel.getValueAt(selectedRow, 1);
        
        // 确认删除
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "确定要删除户型\"" + typeName + "\"吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (estateService.deleteHouseType(typeId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                loadHouseTypeData();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！该户型可能有相关房屋信息。");
            }
        }
    }
}