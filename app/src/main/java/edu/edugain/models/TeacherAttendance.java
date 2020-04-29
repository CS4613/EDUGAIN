package edu.edugain.models;

import android.widget.RadioGroup;

public class TeacherAttendance
{
    private String student_name;
    private String student_id;
    private String course_id;
    private String attended;
    private RadioGroup attendance;
    private int checkedId = -1;

    public TeacherAttendance() {
    }

    public TeacherAttendance(String student_name, String student_id, String course_id, String attended) {
        this.student_name = student_name;
        this.student_id = student_id;
        this.course_id = course_id;
        this.attended = attended;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getAttended() {
        return attended;
    }

    public void setAttended(String attended) {
        this.attended = attended;
    }

    public RadioGroup getAttendance() {
        return attendance;
    }

    public void setAttendance(RadioGroup attendance) {
        this.attendance = attendance;
    }

    public int getCheckedId() {
        return checkedId;
    }

    public void setCheckedId(int checkedId) {
        this.checkedId = checkedId;
    }
}
