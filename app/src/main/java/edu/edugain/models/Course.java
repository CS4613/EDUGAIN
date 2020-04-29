package edu.edugain.models;

public class Course
{
    private String course_id;
    private String department;
    private String instructor;
    private String name;

    public Course(String course_id, String department, String name) {
        this.course_id = course_id;
        this.department = department;
        this.name = name;
    }
    public Course()
    {

    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
