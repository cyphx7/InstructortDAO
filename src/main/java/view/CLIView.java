package view;

import controller.InstructorController;
import model.Instructor;
import java.util.List;
import java.util.Scanner;

public class CLIView {
    private InstructorController controller;
    private Scanner scanner;

    public CLIView() {
        controller = new InstructorController();
        scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n==================================");
            System.out.println("  University Instructor Database  ");
            System.out.println("==================================");
            System.out.println("1. Add Instructor");
            System.out.println("2. Update Instructor");
            System.out.println("3. Delete Instructor");
            System.out.println("4. Search Instructor");
            System.out.println("5. List All Instructors");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            // The enhanced switch statement!
            switch (choice) {
                case "1" -> addInstructorMenu();
                case "2" -> updateInstructorMenu();
                case "3" -> deleteInstructorMenu();
                case "4" -> searchInstructorMenu();
                case "5" -> listAllInstructors();
                case "6" -> {
                    System.out.println("Exiting the program. Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }

            if (running) {
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private String getInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        while (input.trim().isEmpty()) {
            System.out.print("This cannot be blank. " + prompt);
            input = scanner.nextLine();
        }
        return input;
    }

    private void addInstructorMenu() {
        System.out.println("\n--- Add New Instructor ---");
        String id = getInput("Enter ID: ");
        String name = getInput("Enter Name: ");
        String dept = getInput("Enter Department Name: ");

        System.out.print("Enter Salary: ");
        double salary = 0;
        try {
            salary = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Operation Failed: Salary must be a number.");
            return;
        }

        boolean success = controller.addInstructor(id, name, dept, salary);
        if (success) {
            System.out.println("Success! Instructor added to the database.");
        } else {
            System.out.println("Operation Failed: Could not add instructor (ID might already exist).");
        }
    }

    private void updateInstructorMenu() {
        System.out.println("\n--- Update Existing Instructor ---");
        String id = getInput("Enter the ID of the instructor to update: ");
        String name = getInput("Enter New Name: ");
        String dept = getInput("Enter New Department Name: ");

        System.out.print("Enter New Salary: ");
        double salary = 0;
        try {
            salary = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Operation Failed: Salary must be a number.");
            return;
        }

        boolean success = controller.updateInstructor(id, name, dept, salary);
        if (success) {
            System.out.println("Success! Instructor record updated.");
        } else {
            System.out.println("Operation Failed: Could not update (Instructor ID might not exist).");
        }
    }

    private void deleteInstructorMenu() {
        System.out.println("\n--- Delete Instructor ---");
        String id = getInput("Enter the ID of the instructor to delete: ");

        boolean success = controller.deleteInstructor(id);
        if (success) {
            System.out.println("Success! Instructor deleted from the database.");
        } else {
            System.out.println("Operation Failed: Could not delete (Instructor ID might not exist).");
        }
    }

    private void searchInstructorMenu() {
        System.out.println("\n--- Search Instructor ---");
        System.out.println("Select attribute to search by:");
        System.out.println("1. ID");
        System.out.println("2. Name");
        System.out.println("3. Department Name (dept_name)");

        String attrChoice = getInput("Choice: ");
        String attribute = "";

        if (attrChoice.equals("1")) attribute = "ID";
        else if (attrChoice.equals("2")) attribute = "name";
        else if (attrChoice.equals("3")) attribute = "dept_name";
        else {
            System.out.println("Operation Failed: Invalid attribute choice.");
            return;
        }

        String value = getInput("Enter the " + attribute + " to search for: ");

        List<Instructor> results = controller.searchInstructors(attribute, value);

        if (results.isEmpty()) {
            System.out.println("No instructors found matching that criteria.");
        } else {
            System.out.println("\nSearch Results:");
            for (Instructor i : results) {
                System.out.println(i.toString());
            }
        }
    }

    private void listAllInstructors() {
        System.out.println("\n--- All Instructors ---");
        List<Instructor> instructors = controller.getAllInstructors();

        if (instructors.isEmpty()) {
            System.out.println("The instructor table is currently empty.");
        } else {
            for (Instructor i : instructors) {
                System.out.println(i.toString());
            }
        }
    }
}