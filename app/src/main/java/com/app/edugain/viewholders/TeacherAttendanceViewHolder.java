package com.app.edugain.viewholders;

import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edugain.R;

public class TeacherAttendanceViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    private RadioGroup attendance;
    public TeacherAttendanceViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setStudentName(String studentName)
    {
        TextView studName = mView.findViewById(R.id.attendance_student_name);
        studName.setText(studentName);
    }

    public void setCourseName(String courseName)
    {
        TextView studName = mView.findViewById(R.id.attendance_student_courseName);
        studName.setText(courseName);
    }

    public String getAttendance()
    {
        attendance = mView.findViewById(R.id.attendance_radio_group);
        int attended = attendance.getCheckedRadioButtonId();
        String retValue;
        switch (attended)
        {
            case(R.id.present_radio) :
                retValue = "Present";
            break;

            case(R.id.absent_radio) :
                retValue =  "Absent";
                break;

            case(R.id.excused_radio) :
                retValue =  "Excused";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + attended);
        }

        return retValue;
    }

}
