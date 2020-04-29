package edu.edugain.models;

import android.widget.RadioGroup;

public class TeacherGrades
{
    private String student_id;
    private String student_name;
    private String course_name;
    private String grade;
    private RadioGroup grades;
    private int checkedId = -1;

    public TeacherGrades() {
    }

    public TeacherGrades(String student_name, String course_name, String grade, RadioGroup grades, int checkedId) {
        this.student_name = student_name;
        this.course_name = course_name;
        this.grade = grade;
        this.grades = grades;
        this.checkedId = checkedId;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public int getCheckedId() {
        return checkedId;
    }

    public void setCheckedId(int checkedId) {
        this.checkedId = checkedId;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public RadioGroup getGrades() {
        return grades;
    }

    public void setGrades(RadioGroup grades) {
        this.grades = grades;
    }
}
