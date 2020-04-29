package edu.edugain.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.edugain.R;

public class TeacherAttendanceViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    private TextView studName;
    private TextView cName;
    public TeacherAttendanceViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        studName = mView.findViewById(R.id.attendance_student_name);
        cName = mView.findViewById(R.id.attendance_course_name);
    }

    public void setStudentName(String studentName)
    {
        studName.setText(studentName);
    }

    public void setCourseName(String courseName)
    {
        cName.setText(courseName);
    }

}
