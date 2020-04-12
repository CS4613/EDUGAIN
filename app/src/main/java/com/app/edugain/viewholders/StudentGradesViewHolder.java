package com.app.edugain.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edugain.R;

public class StudentGradesViewHolder extends RecyclerView.ViewHolder {

    View mView;
    public StudentGradesViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setCourseName(String courseName)
    {
        TextView course_name = itemView.findViewById(R.id.grades_student_courseName);
        course_name.setText(courseName);
    }

    public void setGrade(String grade)
    {
        TextView course_name = itemView.findViewById(R.id.grades_student_grade);
        course_name.setText(grade);
    }

    /*
    public void setInstructorName(String instructorName)
    {
        TextView course_name = itemView.findViewById(R.id.grades_student_instName);
        course_name.setText(instructorName);
    }

     */



    /*
    public void setRemarks(String remarks)
    {
        TextView course_name = itemView.findViewById(R.id.grades_student_remarks);
        course_name.setText(remarks);
    }

     */
}
