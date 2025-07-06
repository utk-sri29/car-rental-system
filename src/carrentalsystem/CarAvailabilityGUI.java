package carrentalsystem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class CarAvailabilityGUI extends JFrame {

    public CarAvailabilityGUI(CarRentalSystem system) {
        setTitle("Available Cars");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String[] columns = { "Car ID", "Brand", "Model", "Price/Day (₹)", "Status" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        try {
            Connection con = system.getConnection();
            Statement stmt = con.createStatement();

            // ✅ Fetch only available cars
            String query = "SELECT carId, brand, model, basePricePerDay, status FROM cars WHERE status = 'Available'";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getString("carId"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getDouble("basePricePerDay"),
                    rs.getString("status")  // Already 'Available'
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading car data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setGridColor(Color.LIGHT_GRAY);

        // 🌟 HEADER STYLING
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(46, 139, 87)); // SeaGreen
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 35));

        // 🟡 ALTERNATE ROW COLORING
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Color evenColor = new Color(245, 255, 250); // MintCream

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
