package main.java;

import model.DBConnection;
import view.CLIView;
import view.GUIView; // We will build this file next!

import javax.swing.SwingUtilities;

public class MainApp {

    public static void main(String[] args) {
        System.out.println("Starting Database Connection...");

        if (DBConnection.getConnection() != null) {

            // Default to whatever was saved in the app.env file
            boolean isGUI = DBConnection.appMode.equals("gui");

            // Command line arguments completely override the app.env setting
            if (args.length > 0) {
                if (args[0].equals("--gui")) {
                    isGUI = true;
                } else if (args[0].equals("--cli")) {
                    isGUI = false;
                } else {
                    System.out.println("Unknown argument. Use --cli or --gui.");
                    return;
                }
            }

            if (isGUI) {
                System.out.println("Launching GUI Mode...");
                // Swing components must be started on the Event Dispatch Thread to prevent freezing
                SwingUtilities.invokeLater(() -> {
                    GUIView gui = new GUIView();
                    gui.setVisible(true);
                });
            } else {
                System.out.println("Launching CLI Mode...");
                CLIView view = new CLIView();
                view.start();
            }

        } else {
            System.out.println("Could not connect to the database. Exiting...");
        }
    }
}