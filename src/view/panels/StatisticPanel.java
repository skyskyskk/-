// src/view/panels/StatisticPanel.java
package view.panels;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import service.EstateService;
import util.DBUtil;

public class StatisticPanel extends JPanel {
    private EstateService service;
    private JTable table;
    private DefaultTableModel tableModel;

    public StatisticPanel(EstateService service) {
        this.service = service;
        initUI();
        loadStatistics();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 表格区域
        String[] columns = {"户型名称", "总房屋数", "已出租数", "空闲数", "维修中数"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("刷新统计数据");
        // 正确写法：通过lambda表达式接收事件参数，再调用无参方法
        refreshButton.addActionListener(e -> loadStatistics());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStatistics() {
        tableModel.setRowCount(0);

        String sql = "SELECT ht.type_name, " +
                     "COUNT(DISTINCT h.house_id) as total_houses, " +
                     "COUNT(DISTINCT CASE WHEN h.status = '已出租' THEN h.house_id END) as rented_houses, " +
                     "COUNT(DISTINCT CASE WHEN h.status = '空闲' THEN h.house_id END) as vacant_houses, " +
                     "COUNT(DISTINCT CASE WHEN h.status = '维修中' THEN h.house_id END) as maintenance_houses " +
                     "FROM house_type ht " +
                     "LEFT JOIN house h ON ht.type_id = h.type_id " +
                     "GROUP BY ht.type_id, ht.type_name " +
                     "ORDER BY ht.type_name";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);) {

            while (rs.next()) {
                Object[] row = {
                        rs.getString("type_name"),
                        rs.getInt("total_houses"),
                        rs.getInt("rented_houses"),
                        rs.getInt("vacant_houses"),
                        rs.getInt("maintenance_houses")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "统计失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}