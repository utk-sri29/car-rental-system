package carrentalsystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class AllRentalsGUI extends JFrame {

    public AllRentalsGUI(CarRentalSystem system) {
        setTitle("All Rental Records");
        setSize(950, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String[] columns = { "Car ID", "Customer ID", "Customer", "Start", "End", "Actual Return", "Cost" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try {
            Connection con = system.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM rentals");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("carId"),
                    rs.getString("customerId"),
                    rs.getString("customerName"),
                    rs.getDate("rentalStart"),
                    rs.getDate("rentalEnd"),
                    rs.getDate("actualReturnDate") != null ? rs.getDate("actualReturnDate") : "",
                    rs.getDouble("totalCost")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage());
            return;
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(Color.LIGHT_GRAY);

        // ✅ Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(0, 123, 94));  // dark sea green
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 35));

        // ✅ Alternate row coloring
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color evenColor = new Color(240, 255, 250); // Honeydew

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                comp.setBackground(row % 2 == 0 ? evenColor : Color.WHITE);
                return comp;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(scrollPane);
        setVisible(true);
    }
}
