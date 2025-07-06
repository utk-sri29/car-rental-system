package carrentalsystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarRentalSystem {

    private Connection con;

    public CarRentalSystem() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/carrental", "root", "utkarshsr29103"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCar(Car car) {
        try {
            String sql = "INSERT INTO cars (carId, brand, model, basePricePerDay, status) " +
                         "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE brand=VALUES(brand), model=VALUES(model), status=VALUES(status)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, car.getCarId());
            ps.setString(2, car.getBrand());
            ps.setString(3, car.getModel());
            ps.setDouble(4, car.calculatePrice(1)); // base price
            ps.setString(5, "Available");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCustomer(Customer customer) {
        try {
            String sql = "INSERT INTO customers (customerId, name) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, customer.getCustomerId());
            ps.setString(2, customer.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   public double rentCar(String carId, String customerId, String customerName, String startDateStr, String endDateStr) {
    double totalCost = 0.0;

    try {
        String checkSql = "SELECT * FROM cars WHERE carId=? AND status='Available'";
        PreparedStatement checkStmt = con.prepareStatement(checkSql);
        checkStmt.setString(1, carId);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            double pricePerDay = rs.getDouble("basePricePerDay");

            java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
            java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);

            long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
            int rentalDays = (int) (diffInMillies / (1000 * 60 * 60 * 24)) + 1;

            totalCost = pricePerDay * rentalDays;

            String rentSql = "INSERT INTO rentals (carId, customerId, customerName, rentalDays, totalCost, rentalStart, rentalEnd) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(rentSql);
            ps.setString(1, carId);
            ps.setString(2, customerId);
            ps.setString(3, customerName);      // ✅ FIXED: added missing parameter
            ps.setInt(4, rentalDays);
            ps.setDouble(5, totalCost);
            ps.setDate(6, startDate);
            ps.setDate(7, endDate);             // ✅ FIXED: corrected parameter order
            ps.executeUpdate();

            // Update car status
            String updateSql = "UPDATE cars SET status='Rented' WHERE carId=?";
            PreparedStatement ps2 = con.prepareStatement(updateSql);
            ps2.setString(1, carId);
            ps2.executeUpdate();

            // Console Summary
            System.out.println("----- Rental Summary -----");
            System.out.println("Customer: " + customerName);
            System.out.println("Car: " + carId);
            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);
            System.out.println("Days: " + rentalDays);
            System.out.println("Total Cost: ₹" + totalCost);
            System.out.println("--------------------------");

            // Save Receipt
            try (PrintWriter writer = new PrintWriter("receipt_" + customerId + ".txt")) {
                writer.println("----- Rental Receipt -----");
                writer.println("Customer: " + customerName);
                writer.println("Car ID: " + carId);
                writer.println("Rental Start Date: " + startDate);
                writer.println("Rental End Date: " + endDate);
                writer.println("Total Days: " + rentalDays);
                writer.println("Total Cost: ₹" + totalCost);
                writer.println("--------------------------");
                System.out.println("Receipt saved as receipt_" + customerId + ".txt");
            } catch (IOException e) {
                System.out.println("Failed to write receipt file.");
                e.printStackTrace();
            }

        } else {
            throw new RuntimeException("Car not available.");
        }

    } catch (SQLException | IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());
        throw new RuntimeException(e);
    }

    return totalCost;
}

public void viewCarAvailability() {
    System.out.println("--------------------------------------------------------------------------");
    System.out.println("| Car ID | Brand             | Model         | Price/Day  | Status       |");
    System.out.println("--------------------------------------------------------------------------");

    try {
        String sql = "SELECT carId, brand, model, basePricePerDay, status FROM cars";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            String carId = rs.getString("carId");
            String brand = rs.getString("brand");
            String model = rs.getString("model");
            double pricePerDay = rs.getDouble("basePricePerDay");
            String status = rs.getString("status");

            System.out.printf("| %-6s | %-17s | %-13s | ₹%-9.2f | %-12s |\n",
                    carId, brand, model, pricePerDay, status);
        }

        System.out.println("--------------------------------------------------------------------------");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

              public String returnCar(String customerId, String carId) {
    try {
        String getRental = "SELECT rentalId, rentalEnd FROM rentals WHERE carId=? AND customerId=? ORDER BY rentalId DESC LIMIT 1";
        PreparedStatement getRentalStmt = con.prepareStatement(getRental);
        getRentalStmt.setString(1, carId);
        getRentalStmt.setString(2, customerId);
        ResultSet rs = getRentalStmt.executeQuery();

        if (rs.next()) {
            int rentalId = rs.getInt("rentalId");
            Date rentalEnd = rs.getDate("rentalEnd");
            Date today = Date.valueOf(java.time.LocalDate.now());

            long diffInMillies = today.getTime() - rentalEnd.getTime();
            long lateDays = diffInMillies / (1000 * 60 * 60 * 24);

            double lateFee = 0;
            if (lateDays > 0) {
                lateFee = lateDays * 1000.0;
            }

            String updateRental = "UPDATE rentals SET actualReturnDate=? WHERE rentalId=?";
            PreparedStatement updateStmt = con.prepareStatement(updateRental);
            updateStmt.setDate(1, today);
            updateStmt.setInt(2, rentalId);
            updateStmt.executeUpdate();

            String updateCar = "UPDATE cars SET status='Available' WHERE carId=?";
            PreparedStatement updateCarStmt = con.prepareStatement(updateCar);
            updateCarStmt.setString(1, carId);
            updateCarStmt.executeUpdate();

            if (lateFee > 0) {
                return "✅ Car returned with ₹" + lateFee + " late fee.";
            } else {
                return "✅ Car returned on time. No late fee.";
            }

        } else {
            return "❌ No active rental found for this customer and car.";
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return "⚠️ Error during return: " + e.getMessage();
    }
}

        public void viewAllRentals() {
    System.out.println("----- All Rental Records -----");
    try {
        String sql = "SELECT r.carId, r.customerId, c.name AS customerName, r.rentalDays, r.totalCost, " +
                     "r.rentalStart, r.rentalEnd, r.actualReturnDate, ca.brand, ca.model " +
                     "FROM rentals r " +
                     "JOIN customers c ON r.customerId = c.customerId " +
                     "JOIN cars ca ON r.carId = ca.carId";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            System.out.println("Car ID: " + rs.getString("carId"));
            System.out.println("Brand & Model: " + rs.getString("brand") + " " + rs.getString("model"));
            System.out.println("Customer: " + rs.getString("customerName") +
                               " (ID: " + rs.getString("customerId") + ")");
            System.out.println("Rental Start: " + rs.getDate("rentalStart"));
            System.out.println("Rental End: " + rs.getDate("rentalEnd"));
            System.out.println("Actual Return: " + rs.getDate("actualReturnDate"));
            System.out.println("Total Days: " + rs.getInt("rentalDays"));
            System.out.println("Total Cost: ₹" + rs.getDouble("totalCost"));

            // Show late return fee info
            Date actualReturn = rs.getDate("actualReturnDate");
            Date rentalEnd = rs.getDate("rentalEnd");
            if (actualReturn != null && actualReturn.after(rentalEnd)) {
                long diff = actualReturn.getTime() - rentalEnd.getTime();
                long lateDays = diff / (1000 * 60 * 60 * 24);
                double lateFee = lateDays * 1000.0;
                System.out.println("Late Fee: ₹" + lateFee + " (" + lateDays + " days late)");
            }

            System.out.println("----------------------------");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

  public void menu() {
    Scanner scanner = new Scanner(System.in);

    while (true) {
        System.out.println("===== Car Rental System =====");
        System.out.println("1. Rent a Car");
        System.out.println("2. Return a Car");
        System.out.println("3. Exit");
        System.out.println("4. View All Rentals (Admin)");
        System.out.println("5. View Car Availability Tracker");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice == 1) {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            String custId = "CUST" + System.currentTimeMillis();
            addCustomer(new Customer(custId, name));

            System.out.println("Available Cars:");
            try {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM cars WHERE status='Available'");
                while (rs.next()) {
                    System.out.println(rs.getString("carId") + " - " +
                            rs.getString("brand") + " " +
                            rs.getString("model") + " ($" +
                            rs.getDouble("basePricePerDay") + "/day)");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.print("Enter Car ID: ");
            String carId = scanner.nextLine();
            System.out.print("Enter rental start date (YYYY-MM-DD): ");
            String startDate = scanner.nextLine();
            System.out.print("Enter rental end date (YYYY-MM-DD): ");
            String endDate = scanner.nextLine();

            rentCar(carId, custId, name, startDate, endDate);  // Updated rentCar call

        } else if (choice == 2) {
    System.out.print("Enter Customer ID: ");
    String customerId = scanner.nextLine();
    System.out.print("Enter Car ID to return: ");
    String carId = scanner.nextLine();

    String result = returnCar(customerId, carId); // Get message
    System.out.println(result); // Print success or penalty info


        } else if (choice == 3) {
            System.out.println("Exiting...");
            break;

        } else if (choice == 4) {
            viewAllRentals();

        } else if (choice == 5) {
            viewCarAvailability();

        } else {
            System.out.println("Invalid choice. Please enter 1-5.");
        }
    }
  }
 

public Connection getConnection()
{
return con;
}
}