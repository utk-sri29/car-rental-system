package carrentalsystem;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainMenu extends JFrame {

    private CarRentalSystem system;

    public MainMenu(CarRentalSystem system) {
        this.system = system;

        setTitle("Car Rental System - Main Menu");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Custom background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                URL imageUrl = getClass().getResource("/carrentalsystem/resources/fantom.jpg");
                if (imageUrl != null) {
                    ImageIcon bgIcon = new ImageIcon(imageUrl);
                    Image bgImage = bgIcon.getImage();
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    System.out.println("⚠️ Background image not found.");
                }
            }
        };

        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel);

        // GridBag setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        Font btnFont = new Font("Arial", Font.BOLD, 16);

        // 1️⃣ Rent a Car
        JButton rentBtn = new JButton("Rent a Car");
        rentBtn.setFont(btnFont);
        gbc.gridy = 0;
        rentBtn.addActionListener(e -> showRentWindow());
        backgroundPanel.add(rentBtn, gbc);

        // 2️⃣ Return a Car
        JButton returnBtn = new JButton("Return a Car");
        returnBtn.setFont(btnFont);
        gbc.gridy = 1;
        returnBtn.addActionListener(e -> showReturnWindow());
        backgroundPanel.add(returnBtn, gbc);

        // 3️⃣ Car Availability
        JButton availabilityBtn = new JButton("Car Availability");
        availabilityBtn.setFont(btnFont);
        gbc.gridy = 2;
        availabilityBtn.addActionListener(e -> new CarAvailabilityGUI(system));
        backgroundPanel.add(availabilityBtn, gbc);

        // 4️⃣ Admin - View All Rentals
        JButton adminBtn = new JButton("Admin - View All Rentals");
       adminBtn.setFont(btnFont);
        gbc.gridy = 3;

// ✅ Only one action listener: First login, then open rental GUI
      adminBtn.addActionListener(e -> new AdminLoginGUI(system));

     backgroundPanel.add(adminBtn, gbc);

        // 5️⃣ Exit
        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(btnFont);
        gbc.gridy = 4;
        exitBtn.addActionListener(e -> System.exit(0));
        backgroundPanel.add(exitBtn, gbc);

        setVisible(true);
    }

    // Separate methods for each button's action
    private void showRentWindow() {
        new RentCarGUI(system);
    }

    private void showReturnWindow() {
        new ReturnCarGUI(system);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarRentalSystem system = new CarRentalSystem();  // Connect to DB
            new MainMenu(system);  // Launch main GUI
        });
    }
}
