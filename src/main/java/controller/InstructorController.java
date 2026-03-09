package controller;

import dao.InstructorDAO;
import dao.InstructorDAOImpl;
import model.Instructor;
import java.util.List;

public class InstructorController {
    private InstructorDAO dao;

    public InstructorController() { this.dao = new InstructorDAOImpl(); }

    public boolean addInstructor(String id, String name, String deptName, double salary) {
        Instructor newInstructor = new Instructor(id, name, deptName, salary);
        return dao.addInstructor(newInstructor);
    }

    public boolean updateInstructor(String id, String name, String deptName, double salary) {
        Instructor updatedInstructor = new Instructor(id, name, deptName, salary);
        return dao.updateInstructor(updatedInstructor);
    }

    public boolean deleteInstructor(String id) { return dao.deleteInstructor(id);}
    public List<Instructor> getAllInstructors() { return dao.getAllInstructors(); }
    public List<Instructor> searchInstructors(String attribute, String value) { return dao.searchInstructors(attribute, value); }
}