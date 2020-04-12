package com.app.edugain.viewholders;

import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edugain.R;

public class TeacherGradesViewHolder extends RecyclerView.ViewHolder {

    private RadioGroup grades;
    private View mView;
    public TeacherGradesViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        grades = mView.findViewById(R.id.grades_radio_group);
    }

    public void setStudentName(String studentName)
    {
        TextView studName = mView.findViewById(R.id.grades_student_name);
        studName.setText(studentName);
    }

    public void setCourseName(String courseName)
    {
        TextView course = mView.findViewById(R.id.grades_course_name);
        course.setText(courseName);
    }

    public String getGrades()
    {
        String retValue;
        switch (grades.getCheckedRadioButtonId())
        {
            case(R.id.grade_a_radio) :
                retValue = "A";
                break;

            case(R.id.grade_b_radio) :
                retValue =  "B";
                break;

            case(R.id.grade_c_radio) :
                retValue =  "C";
                break;

            case(R.id.grade_d_radio) :
                retValue =  "D";
                break;

            case(R.id.grade_f_radio) :
                retValue =  "F";
                break;
            default:
            retValue = null;
        }

        return retValue;
    }
}
