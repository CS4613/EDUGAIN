package com.app.edugain.teacher_login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.edugain.models.Course;
import com.app.edugain.viewholders.TeacherAttendanceViewHolder;
import com.example.edugain.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private RecyclerView attendance_recycler;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private String current_user;
    private Spinner course_spinner;
    private Map<String, String> courseId_courseNames = new HashMap<>();
    private Map<String, String> studentId_attended = new HashMap<>();
    private List<String> courseNames = new ArrayList<>();
    private ArrayAdapter<String> spinner_adapter;
    private Button cancel, submit;
    private Calendar calendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/DD/YYYY");
    private String date;
    private String course_id;
    FirebaseRecyclerAdapter<Boolean, TeacherAttendanceViewHolder> recyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_teacher_attendance, container, false);
        attendance_recycler = root.findViewById(R.id.attendance_recyclerview);
        attendance_recycler.setHasFixedSize(true);
        attendance_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        course_spinner = root.findViewById(R.id.teacher_attendance_course_spinner);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();

        cancel = root.findViewById(R.id.btn_cancel_tAttendance);
        submit = root.findViewById(R.id.btn_submit_tAttendance);

        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);

        calendar = Calendar.getInstance();
        date = dateFormat.format(calendar.getTime());

        populateSpinner();
        setAdapter("Java");
        course_spinner.setOnItemSelectedListener(this);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    private void populateSpinner()
    {
        database.child("instructor_courses").child(current_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snap : dataSnapshot.getChildren())
                {
                    database.child("courses").child(snap.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            Course course = dataSnapshot.getValue(Course.class);
                            courseId_courseNames.put(snap.getKey(), course.getName());
                            courseNames.add(snap.getKey());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                spinner_adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, courseNames);
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                course_spinner.setAdapter(spinner_adapter);
                course_spinner.setSelection(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel_tAttendance)
        {
            new AlertDialog.Builder(getContext())
                    .setTitle("Attendance")
                    .setMessage("Are you sure you want to cancel taking Attendance ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(getContext(), "Attendance Not taken", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(R.drawable.attendance80)
                    .show();
        }
        else if(v.getId() == R.id.btn_submit_tAttendance)
        {
            setAttendance(studentId_attended, course_id, date);
            Toast.makeText(getContext(), "Attendance taken Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void setAttendance(Map<String, String> student_attended,  String course_id, String date)
    {
        if(student_attended != null)
        {
            for (Map.Entry<String, String> entry: student_attended.entrySet())
            {
                database
                        .child("student_attendance")
                        .child(course_id)
                        .child(date)
                        .child(entry.getKey())
                        .setValue(entry.getValue())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                    Toast.makeText(getContext(), "Error Posting Attendance", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        else
            Toast.makeText(getActivity(), "Error Posting Attendance to Database", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.teacher_attendance_course_spinner)
        {
            parent.setSelection(position);
            course_id = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(), course_id, Toast.LENGTH_SHORT).show();
            setAdapter(course_id);
            recyclerAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getContext(), "Please Select an Item", Toast.LENGTH_SHORT).show();
    }


    private void setAdapter(final String course_id) {
        recyclerAdapter = new FirebaseRecyclerAdapter<Boolean, TeacherAttendanceViewHolder>
                (
                        Boolean.class,
                        R.layout.teacher_attendance_list_item,
                        TeacherAttendanceViewHolder.class,
                        database.child("courses_students").child(course_id))
        {
            @Override
            protected void populateViewHolder(final TeacherAttendanceViewHolder teacherAttendanceViewHolder, Boolean aBoolean, int i) {
                final String student_id = this.getRef(i).getKey();
                database.child("users").child(student_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User student = dataSnapshot.getValue(User.class);
                        try {
                            teacherAttendanceViewHolder.setStudentName(dataSnapshot.child("name").getValue().toString());
                            teacherAttendanceViewHolder.setCourseName(course_id);
                            studentId_attended.put(student_id, teacherAttendanceViewHolder.getAttendance());

                        } catch (Exception e) {
                            e.printStackTrace();
                            teacherAttendanceViewHolder.setStudentName("error");
                            teacherAttendanceViewHolder.setCourseName("error");
                            //studentId_attended.put(student_id, teacherAttendanceViewHolder.getAttendance());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };

         /*
        recyclerAdapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>
                (Course.class, R.layout.course_list_item, CourseViewHolder.class,database.child("courses")) {
            @Override
            protected void populateViewHolder(CourseViewHolder courseHandler, Course course, int i) {
                String course_name = course.getName();
                final String course_id = course.getCourse_id();
                courseHandler.setCourseName(course_name);

                courseHandler.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        database.child("student_courses").child(current_user).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(course_id))
                                {
                                    Toast.makeText(getContext(), "Course Already Registered", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    new android.app.AlertDialog.Builder(getContext())
                                            .setTitle("Register")
                                            .setMessage("Do you want to Register for Course "+course_id+" ?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    database.child("student_courses").child(current_user).child(course_id).setValue(true);
                                                    Toast.makeText(getContext(), course_id+" has been Registered", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .setIcon(R.drawable.course80)
                                            .show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });
            }
        };

         */
        attendance_recycler.setAdapter(recyclerAdapter);
    }

}
