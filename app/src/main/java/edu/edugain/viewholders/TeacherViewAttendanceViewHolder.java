package edu.edugain.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.edugain.R;

public class TeacherViewAttendanceViewHolder extends RecyclerView.ViewHolder {
    private View mView;
    private TextView name;
    private TextView present;
    private TextView absent;
    private TextView excused;

    public TeacherViewAttendanceViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        name = mView.findViewById(R.id.inst_view_att_name);
        present = mView.findViewById(R.id.inst_view_att_present);
        absent = mView.findViewById(R.id.inst_view_att_absent);
        excused = mView.findViewById(R.id.inst_view_att_excused);
    }

    public void setName(String _name)
    {
        name.setText(_name);
    }

    public void setPresent(String _present)
    {
        present.setText(_present);
    }

    public void setAbsent(String _absent)
    {
        absent.setText(_absent);
    }

    public void setExcused(String _excused)
    {
        excused.setText(_excused);
    }

}
