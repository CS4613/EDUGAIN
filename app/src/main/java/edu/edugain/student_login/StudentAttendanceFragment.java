package edu.edugain.student_login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.edugain.R;
import edu.edugain.models.Course;

public class StudentAttendanceFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner course_spinner;
    private TextView course_name;
    private TextView total;
    private TextView present;
    private TextView absent;
    private TextView excused;
    private TextView get_detailed_report;

    private DatabaseReference database;
    private FirebaseAuth auth;
    private String current_user;

    private List<String> courseNames;
    private ArrayAdapter<String> spinner_adapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_student_attendance, container, false);

        course_spinner = root.findViewById(R.id.student_attendance_spinner);
        course_name = root.findViewById(R.id.attendance_student_courseName);
        total = root.findViewById(R.id.student_attendance_total);
        present = root.findViewById(R.id.student_attendance_present);
        absent = root.findViewById(R.id.student_attendance_absent);
        excused = root.findViewById(R.id.student_attendance_excused);
        get_detailed_report = root.findViewById(R.id.student_get_detailed_report);

        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();

        courseNames = new ArrayList<>();
        courseNames.add("Select a Course");
        loadSpinner();

        get_detailed_report.setOnClickListener(this);
        course_spinner.setOnItemSelectedListener(this);

        return root;
    }
    private void loadSpinner() {

        database.child("student_courses")
                .child(current_user)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snap : dataSnapshot.getChildren())
                        {
                            database.child("courses").child(snap.getKey()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    Course course = dataSnapshot.getValue(Course.class);
                                    courseNames.add(course.getCourse_id());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        spinner_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, courseNames);
                        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course_spinner.setAdapter(spinner_adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onClick(View v)
    {
        if(v == get_detailed_report)
        {

        }

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        getNumbers(parent.getSelectedItem().toString());
        Toast.makeText(getActivity(), "Selected Course: "+parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getNumbers(String course_id) {
        getCourseName(course_id);
        getTotalNum(course_id);
        getPresentNum(course_id);
        getAbsentNum(course_id);
        getExcusedNum(course_id);
    }

    private void getCourseName(String course_id) {
        database
        .child("courses")
        .child(course_id)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Course course = dataSnapshot.getValue(Course.class);
                    course_name.setText(course.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    course_name.setText("No Course Found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getExcusedNum(String course_id) {
        database
        .child("course_attendance")
        .child(course_id)
        .orderByChild(current_user)
        .equalTo("Excused")
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                excused.setText(Long.toString(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAbsentNum(String course_id) {
        database
                .child("course_attendance")
                .child(course_id)
                .orderByChild(current_user)
                .equalTo("Absent")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        absent.setText(Long.toString(dataSnapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getPresentNum(String course_id) {
        database
        .child("course_attendance")
        .child(course_id)
        .orderByChild(current_user)
        .equalTo("Present")
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                present.setText(Long.toString(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getTotalNum(String course_id) {
        database
        .child("course_attendance")
        .child(course_id)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                total.setText(Long.toString(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
