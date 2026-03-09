package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DBConnection {
    private static final String FILE_NAME = "app.env";
    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn != null) {
            return conn;
        }

        File envFile = new File(FILE_NAME);
        Scanner inputScanner = new Scanner(System.in);

        while (conn == null) {
            if (envFile.exists()) {
                System.out.println("Found app.env file. Attempting automatic login");
                if (!loginFromEnv(envFile)) {
                    System.out.println("Saved credentials didn't work. Set it up again.");
                    envFile.delete();
                }
            } else {
                System.out.println("\n--- Database Setup ---");
                System.out.println("No environment file found. Initialize your connection.");
                setupEnvFile(envFile, inputScanner);
            }
        }
        return conn;
    }

    private static boolean loginFromEnv(File file) {
        try (Scanner fileReader = new Scanner(file)) {
            String url = fileReader.nextLine();
            String user = fileReader.nextLine();
            String pass = fileReader.nextLine();

            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Login successful");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void setupEnvFile(File file, Scanner inputScanner) {
        System.out.print("Enter MySQL DB URL (jdbc:mysql://localhost:3306/university): ");
        String url = inputScanner.nextLine();

        System.out.print("Enter MySQL Username: ");
        String user = inputScanner.nextLine();

        System.out.print("Enter MySQL Password: ");
        String pass = inputScanner.nextLine();

        try {
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection successful! Saving credentials to app.env...");

            FileWriter writer = new FileWriter(file);
            writer.write(url + "\n" + user + "\n" + pass + "\n");
            writer.close();

        } catch (SQLException e) {
            System.out.println("Error: Could not connect to the database. Please check your credentials and make sure MySQL is running.");
        } catch (IOException e) {
            System.out.println("Error saving the env file: " + e.getMessage());
        }
    }
}