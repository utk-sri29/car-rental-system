package carrentalsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RentCarGUI extends JFrame {

    private CarRentalSystem system;

    public RentCarGUI(CarRentalSystem system) {
        this.system = system;

        setTitle("Rent a Car");
        setSize(650, 350);  // wider window
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Center panel with fields and buttons
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10)); // padding

        // Fields
        JTextField custIdField = new JTextField();
        JTextField custNameField = new JTextField();
        JTextField carIdField = new JTextField();
        JTextField startDateField = new JTextField(); // Format: YYYY-MM-DD
        JTextField endDateField = new JTextField();   // Format: YYYY-MM-DD

        formPanel.add(new JLabel("Customer ID:"));
        formPanel.add(custIdField);
        formPanel.add(new JLabel("Customer Name:"));
        formPanel.add(custNameField);
        formPanel.add(new JLabel("Car ID:"));
        formPanel.add(carIdField);
        formPanel.add(new JLabel("Rental Start Date (YYYY-MM-DD):"));
        formPanel.add(startDateField);
        formPanel.add(new JLabel("Rental End Date (YYYY-MM-DD):"));
        formPanel.add(endDateField);

        JButton rentBtn = new JButton("Rent Car");
        JButton cancelBtn = new JButton("Cancel");

        formPanel.add(rentBtn);
        formPanel.add(cancelBtn);

        // Right panel with image
        JLabel imageLabel = new JLabel();
        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/carrentalsystem/resources/luxuy.jpeg")); // image path
        Image scaledImage = rawIcon.getImage().getScaledInstance(180, 250, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaledImage));
        JPanel imagePanel = new JPanel();
        imagePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        imagePanel.add(imageLabel);

        // Add panels
        add(formPanel, BorderLayout.CENTER);
        add(imagePanel, BorderLayout.EAST);

        // Button Logic
        rentBtn.addActionListener(e -> {
            try {
                String custId = custIdField.getText().trim();
                String custName = custNameField.getText().trim();
                String carId = carIdField.getText().trim();
                String startDate = startDateField.getText().trim();
                String endDate = endDateField.getText().trim();

                if (custId.isEmpty() || custName.isEmpty() || carId.isEmpty()
                        || startDate.isEmpty() || endDate.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.");
                    return;
                }

                // ✅ First insert customer
                Customer customer = new Customer(custId, custName);
                system.addCustomer(customer);

                // ✅ Then rent car
                double totalCost = system.rentCar(carId, custId, custName, startDate, endDate);
                JOptionPane.showMessageDialog(this, "Rental successful! Total cost: ₹" + totalCost);
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }
}
