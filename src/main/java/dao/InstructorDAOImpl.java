package dao;

import model.Instructor;
import model.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAOImpl implements InstructorDAO {
    @Override
    public boolean addInstructor(Instructor instructor) {
        String sql = "INSERT INTO instructor (ID, name, dept_name, salary) VALUES (?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, instructor.getId());
            pstmt.setString(2, instructor.getName());
            pstmt.setString(3, instructor.getDeptName());
            pstmt.setDouble(4, instructor.getSalary());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error adding instructor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateInstructor(Instructor instructor) {
        String sql = "UPDATE instructor SET name = ?, dept_name = ?, salary = ? WHERE ID = ?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, instructor.getName());
            pstmt.setString(2, instructor.getDeptName());
            pstmt.setDouble(3, instructor.getSalary());
            pstmt.setString(4, instructor.getId());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating instructor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteInstructor(String id) {
        String sql = "DELETE FROM instructor WHERE ID = ?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting instructor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Instructor> getAllInstructors() {
        List<Instructor> list = new ArrayList<>();
        String sql = "SELECT * FROM instructor";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Instructor inst = new Instructor(
                        rs.getString("ID"),
                        rs.getString("name"),
                        rs.getString("dept_name"),
                        rs.getDouble("salary")
                );
                list.add(inst);
            }

        } catch (SQLException e) {
            System.out.println("Error getting all instructors: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Instructor> searchInstructors(String attribute, String value) {
        List<Instructor> list = new ArrayList<>();
        String sql = "SELECT * FROM instructor WHERE " + attribute + " = ?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Instructor inst = new Instructor(
                        rs.getString("ID"),
                        rs.getString("name"),
                        rs.getString("dept_name"),
                        rs.getDouble("salary")
                );
                list.add(inst);
            }

        } catch (SQLException e) {
            System.out.println("Error searching instructors: " + e.getMessage());
        }
        return list;
    }
}