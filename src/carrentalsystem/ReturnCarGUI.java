package carrentalsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ReturnCarGUI extends JFrame {

    private CarRentalSystem system;
    private JLabel statusLabel;

    public ReturnCarGUI(CarRentalSystem system) {
        this.system = system;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Return a Car");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JTextField custIdField = new JTextField();
        JTextField carIdField = new JTextField();

        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(custIdField);
        formPanel.add(new JLabel("Car ID:"));
        formPanel.add(carIdField);

        JButton checkBtn = new JButton("Check Penalty");
        JButton returnBtn = new JButton("Return Car");

        formPanel.add(checkBtn);
        formPanel.add(returnBtn);

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        JButton cancelBtn = new JButton("Cancel");
        bottomPanel.add(cancelBtn);

        add(formPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.PAGE_END);

        // ✅ Check Penalty Logic
        checkBtn.addActionListener(e -> {
            String custId = custIdField.getText().trim();
            String carId = carIdField.getText().trim();

            if (custId.isEmpty() || carId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both Customer ID and Car ID.");
                return;
            }

            try {
                String query = "SELECT rentalEnd FROM rentals WHERE carId=? AND customerId=? ORDER BY rentalId DESC LIMIT 1";
                PreparedStatement stmt = system.getConnection().prepareStatement(query);
                stmt.setString(1, carId);
                stmt.setString(2, custId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Date rentalEnd = rs.getDate("rentalEnd");
                    Date today = Date.valueOf(java.time.LocalDate.now());
                    long diff = today.getTime() - rentalEnd.getTime();
                    long lateDays = diff / (1000 * 60 * 60 * 24);

                    if (lateDays > 0) {
                        statusLabel.setText("<html><b>Expected Return:</b> " + rentalEnd +
                                " | <b>Today:</b> " + today +
                                " <br><b>Late:</b> " + lateDays + " days — ₹" + (lateDays * 1000) + " penalty</html>");
                    } else {
                        statusLabel.setText("<html><b>Expected Return:</b> " + rentalEnd +
                                " | <b>Today:</b> " + today + " — <b>No Late Fee</b></html>");
                    }
                } else {
                    statusLabel.setText("❌ No rental record found.");
                }
            } catch (SQLException ex) {
                statusLabel.setText("⚠️ SQL Error: " + ex.getMessage());
            }
        });

        // ✅ Return Button Logic
        returnBtn.addActionListener(e -> {
            String custId = custIdField.getText().trim();
            String carId = carIdField.getText().trim();

            if (custId.isEmpty() || carId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try {
                String result = system.returnCar(custId, carId);
                JOptionPane.showMessageDialog(this, result);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            }
        });

        // ❌ Cancel closes the window
        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}
