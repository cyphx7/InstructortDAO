package dao;

import model.Instructor;
import java.util.List;

public interface InstructorDAO {
    boolean addInstructor(Instructor instructor);
    boolean updateInstructor(Instructor instructor);
    boolean deleteInstructor(String id);

    List<Instructor> getAllInstructors();

    List<Instructor> searchInstructors(String attribute, String value);
}