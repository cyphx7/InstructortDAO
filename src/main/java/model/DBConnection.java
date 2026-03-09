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
    public static String appMode = "cli";

    public static Connection getConnection() {
        if (conn != null) {
            return conn;
        }

        File envFile = new File(FILE_NAME);
        Scanner inputScanner = new Scanner(System.in);

        while (conn == null) {
            if (envFile.exists()) {
                System.out.println("Found app.env file. Attempting automatic login...");
                if (!loginFromEnv(envFile)) {
                    System.out.println("Saved credentials didn't work. Let's set it up again.");
                    envFile.delete();
                }
            } else {
                System.out.println("\n--- Database Setup ---");
                System.out.println("No environment file found. Please initialize your connection.");
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

            if (fileReader.hasNextLine()) {
                String modeLine = fileReader.nextLine();
                appMode = modeLine.replace("app_mode=", "").trim();
            }

            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Login successful!");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void setupEnvFile(File file, Scanner inputScanner) {
        System.out.print("Enter MySQL DB URL (e.g., jdbc:mysql://localhost:3306/university): ");
        String url = inputScanner.nextLine();

        System.out.print("Enter MySQL Username: ");
        String user = inputScanner.nextLine();

        System.out.print("Enter MySQL Password (leave blank if none): ");
        String pass = inputScanner.nextLine();

        System.out.print("Enter default app mode (gui or cli): ");
        appMode = inputScanner.nextLine().toLowerCase();
        if(!appMode.equals("gui") && !appMode.equals("cli")) {
            appMode = "cli";
        }

        try {
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection successful! Saving credentials to app.env...");

            FileWriter writer = new FileWriter(file);
            writer.write(url + "\n" + user + "\n" + pass + "\napp_mode=" + appMode + "\n");
            writer.close();

        } catch (SQLException e) {
            System.out.println("Error: Could not connect to the database.");
        } catch (IOException e) {
            System.out.println("Error saving the env file: " + e.getMessage());
        }
    }
}