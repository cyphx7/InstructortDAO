package model;

public class Instructor {
    private String id;
    private String name;
    private String deptName;
    private double salary;

    public Instructor(String id, String name, String deptName, double salary) {
        this.id = id;
        this.name = name;
        this.deptName = deptName;
        this.salary = salary;
    }

    public Instructor() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Dept: " + deptName + " | Salary: $" + salary;
    }
}