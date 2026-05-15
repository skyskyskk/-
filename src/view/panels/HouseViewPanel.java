// src/view/panels/HouseViewPanel.java
package view.panels;

import service.EstateService;
import util.DBUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class HouseViewPanel extends JPanel {
    private EstateService service;
    private JTable table;
    private DefaultTableModel tableModel;

    public HouseViewPanel(EstateService service) {
        this.service = service;
        initUI();
        loadHouseView();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 表格区域
        String[] columns = {"房号", "房东", "电话", "户型", "面积", "状态"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("刷新房屋信息");
        refreshButton.addActionListener(e->loadHouseView());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadHouseView() {
        tableModel.setRowCount(0);

        String sql = "SELECT * FROM HouseInfoView";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getString("house_number"),
                        rs.getString("landlord_name"),
                        rs.getString("landlord_phone"),
                        rs.getString("type_name"),
                        rs.getBigDecimal("area"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "查询失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}