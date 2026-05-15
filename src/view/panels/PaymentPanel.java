// src/view/panels/PaymentPanel.java
package view.panels;

import model.Payment;
import model.RentalRecord;
import service.EstateService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PaymentPanel extends JPanel {
    private EstateService service;
    private JTable recordTable, paymentTable;
    private DefaultTableModel recordModel, paymentModel;
    private JTextField recordIdField, dateField, amountField, typeField, remarksField;

    public PaymentPanel(EstateService service) {
        this.service = service;
        initUI();
        loadRentalRecords();
        loadPayments();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 顶部表格区域（显示出租记录）
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("出租记录列表"));

        String[] recordColumns = {"记录ID", "房屋ID", "客户ID", "租金"};
        recordModel = new DefaultTableModel(recordColumns, 0);
        recordTable = new JTable(recordModel);
        topPanel.add(new JScrollPane(recordTable), BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // 中间显示收费记录
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("收费记录列表"));

        String[] paymentColumns = {"收费ID", "记录ID", "日期", "金额", "类型", "备注"};
        paymentModel = new DefaultTableModel(paymentColumns, 0);
        paymentTable = new JTable(paymentModel);
        centerPanel.add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // 底部表单区域
        JPanel formPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        formPanel.add(new JLabel("出租记录ID:"));
        recordIdField = new JTextField();
        formPanel.add(recordIdField);

        formPanel.add(new JLabel("收费日期(YYYY-MM-DD):"));
        dateField = new JTextField();
        formPanel.add(dateField);

        formPanel.add(new JLabel("金额:"));
        amountField = new JTextField();
        formPanel.add(amountField);

        formPanel.add(new JLabel("类型(租金/押金/其他):"));
        typeField = new JTextField();
        formPanel.add(typeField);

        formPanel.add(new JLabel("备注:"));
        remarksField = new JTextField();
        formPanel.add(remarksField);

        // 按钮区域
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("添加收费记录");
        addButton.addActionListener(this::addPayment);
        JButton refreshButton = new JButton("刷新数据");
        refreshButton.addActionListener(e -> {
            loadRentalRecords();
            loadPayments();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadRentalRecords() {
        recordModel.setRowCount(0);
        List<RentalRecord> records = service.getAllRentalRecords();
        for (RentalRecord record : records) {
            Object[] row = {
                    record.getRecordId(),
                    record.getHouseId(),
                    record.getCustomerId(),
                    record.getMonthlyRent()
            };
            recordModel.addRow(row);
        }
    }

    private void loadPayments() {
        paymentModel.setRowCount(0);
        List<Payment> payments = service.getAllPayments();
        for (Payment payment : payments) {
            Object[] row = {
                    payment.getPaymentId(),
                    payment.getRecordId(),
                    payment.getPaymentDate(),
                    payment.getAmount(),
                    payment.getPaymentType(),
                    payment.getRemarks()
            };
            paymentModel.addRow(row);
        }
    }

    private void addPayment(ActionEvent e) {
        try {
            int recordId = Integer.parseInt(recordIdField.getText().trim());
            LocalDate paymentDate = LocalDate.parse(dateField.getText().trim());
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            String paymentType = typeField.getText().trim();
            String remarks = remarksField.getText().trim();

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "金额必须大于0");
                return;
            }

            Payment payment = new Payment(recordId, paymentDate, amount, paymentType);
            payment.setRemarks(remarks);

            if (service.addPayment(payment)) {
                JOptionPane.showMessageDialog(this, "收费记录添加成功！");
                clearForm();
                loadPayments();
            } else {
                JOptionPane.showMessageDialog(this, "收费记录添加失败！");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage());
        }
    }

    private void clearForm() {
        recordIdField.setText("");
        dateField.setText("");
        amountField.setText("");
        typeField.setText("");
        remarksField.setText("");
    }
}