package edu.edugain.viewholders;

import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.edugain.R;
import edu.edugain.models.TeacherGrades;

public class TeacherGradesViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    private RadioGroup grades;
    private TextView studName;
    private TextView course;

    private String retValue;
    public TeacherGradesViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        studName = mView.findViewById(R.id.grades_student_name);
        course = mView.findViewById(R.id.grades_course_name);
        grades = mView.findViewById(R.id.grades_radio_group);

    }

    public void setStudentName(String studentName)
    {
        studName.setText(studentName);
    }

    public void setCourseName(String courseName)
    {
        course.setText(courseName);
    }

    public String getGrades(TeacherGrades element, int position)
    {
        grades.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId())
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

            }
        });

        return retValue;
    }
}
