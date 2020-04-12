package com.app.edugain.student_login;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.edugain.R;

import java.util.Calendar;

public class StudentAttendanceFragment extends Fragment implements View.OnClickListener {

    private Button get_report;
    private DatePickerDialog datePicker;
    private EditText from;
    private EditText to;
    private Calendar calendar;
    private String fromDate, toDate;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_student_attendance, container, false);

        get_report = root.findViewById(R.id.get_report);
        from = root.findViewById(R.id.attendance_from);
        to = root.findViewById(R.id.attendance_to);
        from.setOnClickListener(this);
        to.setOnClickListener(this);
        get_report.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v)
    {
        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if(v == from)
        {
            datePicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                {
                    fromDate = (month + 1) + "/" + dayOfMonth + "/" +  + year;
                    from.setText(fromDate);
                }
            }, year, month, day);
            datePicker.show();
        }
        if(v == to)
        {
            datePicker = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                        {
                            toDate = (month + 1) + "/" + dayOfMonth + "/" +  + year;
                            to.setText(toDate);
                        }
                    }, year, month, day);
            datePicker.show();
        }
        if(v == get_report)
        {
            Toast.makeText(getContext(), "Range is "+fromDate+" to "+toDate, Toast.LENGTH_SHORT).show();
        }

    }
}
