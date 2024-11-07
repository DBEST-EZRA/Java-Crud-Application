import java.sql.*;
import java.util.Scanner;

public class UniversityDatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/university";
    private static final String USER = "your_username";
    private static final String PASS = "your_password";

    private Connection conn;
    private Scanner scanner;

    public UniversityDatabaseManager() {
        try {
            // Load MySQL JDBC driver explicitly for Java 7 compatibility
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            scanner = new Scanner(System.in);
            System.out.println("Connected to the database successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found. Make sure to include it in your library path.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection to database failed!");
            e.printStackTrace();
        }
    }

    public void mainMenu() {
        while (true) {
            System.out.println("\nUniversity Database Manager");
            System.out.println("1. Display Table Content");
            System.out.println("2. Insert Record into Student Table");
            System.out.println("3. Modify Advisor of a Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1: displayTable(); break;
                case 2: insertStudent(); break;
                case 3: modifyAdvisor(); break;
                case 4: deleteStudent(); break;
                case 5: System.out.println("Exiting..."); closeResources(); return;
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void displayTable() {
        System.out.print("Enter table name (Instructor, Advisor, Student): ");
        String tableName = scanner.nextLine();

        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(", ");
                    System.out.print(rs.getString(i));
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error displaying table. Check table name.");
            e.printStackTrace();
        }
    }

    private void insertStudent() {
        try {
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Student Department: ");
            String deptName = scanner.nextLine();

            System.out.print("Enter Student Total Credits: ");
            int totalCredits = scanner.nextInt();

            String query = "INSERT INTO Student (ID, name, dept_name, tot_cred) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);
                pstmt.setString(2, name);
                pstmt.setString(3, deptName);
                pstmt.setInt(4, totalCredits);
                pstmt.executeUpdate();
                System.out.println("Student record inserted successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error inserting student.");
            e.printStackTrace();
        }
    }

    private void modifyAdvisor() {
        try {
            System.out.print("Enter Student ID to modify advisor: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            String currentAdvisorQuery = "SELECT advisor_id FROM Advisor WHERE s_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(currentAdvisorQuery)) {
                pstmt.setInt(1, studentId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Current Advisor ID: " + rs.getInt("advisor_id"));
                } else {
                    System.out.println("Student has no advisor assigned.");
                }
            }

            System.out.print("Enter new Advisor ID: ");
            int advisorId = scanner.nextInt();

            String updateQuery = "UPDATE Advisor SET advisor_id = ? WHERE s_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setInt(1, advisorId);
                pstmt.setInt(2, studentId);
                pstmt.executeUpdate();
                System.out.println("Advisor updated successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error modifying advisor.");
            e.printStackTrace();
        }
    }

    private void deleteStudent() {
        try {
            System.out.print("Enter Student ID to delete: ");
            int studentId = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            String query = "DELETE FROM Student WHERE ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, studentId);

                System.out.print("Are you sure you want to delete this student? (yes/no): ");
                String confirmation = scanner.nextLine();
                if (confirmation.equalsIgnoreCase("yes")) {
                    pstmt.executeUpdate();
                    System.out.println("Student deleted successfully.");
                } else {
                    System.out.println("Deletion canceled.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting student.");
            e.printStackTrace();
        }
    }

    private void closeResources() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UniversityDatabaseManager manager = new UniversityDatabaseManager();
        manager.mainMenu();
    }
}
