import model.DBConnection;
import view.CLIView;

public class MainApp {

    public static void main(String[] args) {
        System.out.println("Starting Database Connection");
        if (DBConnection.getConnection() != null) {
            CLIView view = new CLIView();
            view.start();
        } else {
            System.out.println("Could not connect to the database. Exiting");
        }
    }
}