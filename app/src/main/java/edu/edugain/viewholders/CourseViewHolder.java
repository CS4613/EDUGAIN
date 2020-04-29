package edu.edugain.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.edugain.R;

public class CourseViewHolder extends RecyclerView.ViewHolder {
    View mView;
    public CourseViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setCourseName(String courseName)
    {
        TextView course_name = mView.findViewById(R.id.course_list_item_name);
        course_name.setText(courseName);
    }
/*
    public void setCourseID(String courseName)
    {
        TextView course_name = mView.findViewById(R.id.course_id);
        course_name.setText(courseName);
    }

    public void setCourseDept(String courseName)
    {
        TextView course_name = mView.findViewById(R.id.course_department);
        course_name.setText(courseName);
    }

    public void setCourseInstructor(String courseName)
    {
        TextView course_name = mView.findViewById(R.id.course_instructor);
        course_name.setText(courseName);
    }
*/
}
