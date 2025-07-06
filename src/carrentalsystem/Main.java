package carrentalsystem;

public class Main {
    public static void main(String[] args) {
        CarRentalSystem system = new CarRentalSystem();

        // Adding luxury and updated car entries
        system.addCar(new Car("C001", "Lexus", "LE 5000", 5000.0));
        system.addCar(new Car("C002", "Rolls Royce", "Phantom", 10000.0));
        system.addCar(new Car("C003", "BYD", "Seal", 3000.0));
        system.addCar(new Car("C004", "Skoda", "Superb", 4500.0));
        system.addCar(new Car("C005", "Mercedes-Benz", "S-Class", 9000.0));

         new MainMenu(system);
    }
}
