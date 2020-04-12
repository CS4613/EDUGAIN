package com.app.edugain.teacher_login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.edugain.adapters.GradesAdapter;
import com.example.edugain.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TakeAttendance extends AppCompatActivity implements View.OnClickListener {

    private String current_user, course_id, course_name;
    private RecyclerView student_list_recycler;
    private Button submit, cancel;

    private DatabaseReference database;
    private Map<String, String> attendedList = new HashMap<>();;
    private Calendar calendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/DD/YYYY");
    private String date;
    private Intent gotoTeacherLogin = new Intent(TakeAttendance.this, TeacherLogin.class);
    //private FirebaseRecyclerAdapter<Boolean, TeacherAttendanceViewHolder> adapter;
    private GradesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        student_list_recycler = findViewById(R.id.attendance_students_list);
        student_list_recycler.setLayoutManager(new LinearLayoutManager(this));
        student_list_recycler.setHasFixedSize(true);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        submit = findViewById(R.id.btn_submit_teacher_attendance);
        cancel = findViewById(R.id.btn_cancel_teacher_attendance);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            current_user = bundle.getString("current_user");
            course_id = bundle.getString("course_id");
            course_name = bundle.getString("course_name");
        }

        submit.setOnClickListener(this);
        cancel.setOnClickListener(this);

        calendar = Calendar.getInstance();
        date = dateFormat.format(calendar.getTime());

/*
        adapter = new FirebaseRecyclerAdapter<Boolean, TeacherAttendanceViewHolder>
                (Boolean.class,
                        R.layout.teacher_attendance_list_item,
                        TeacherAttendanceViewHolder.class,
                        database.child("courses_students").child(course_id)) {
            @Override
            protected void populateViewHolder(final TeacherAttendanceViewHolder teacherAttendanceViewHolder, Boolean aBoolean, int i) {
                String student_id = getRef(i).getKey();
                database
                .child("users")
                .child(student_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User student = dataSnapshot.getValue(User.class);
                        teacherAttendanceViewHolder.setCourseName(course_name);
                        teacherAttendanceViewHolder.setStudentName(student.getName());
                        attendedList.put(student.getId(), teacherAttendanceViewHolder.getAttendance());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

 */
        adapter = new GradesAdapter(database, course_id, course_name);
        student_list_recycler.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(gotoTeacherLogin);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.btn_cancel_teacher_attendance)
        {
            new AlertDialog.Builder(TakeAttendance.this)
                    .setTitle("Attendance")
                    .setMessage("Are you sure you want to cancel taking Attendance ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(TakeAttendance.this, "Attendance Not taken", Toast.LENGTH_SHORT).show();
                            startActivity(gotoTeacherLogin);
                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(R.drawable.attendance80)
                    .show();
        }
        if(v.getId() == R.id.btn_submit_teacher_attendance)
        {
            setAttendance(attendedList, course_id, date);
            Toast.makeText(TakeAttendance.this, "Attendance taken Successfully", Toast.LENGTH_SHORT).show();
            startActivity(gotoTeacherLogin);
        }
    }

    public void setAttendance(Map<String, String> student_attended,  String course_id, String date)
    {
        if(student_attended != null)
        {
            for (Map.Entry<String, String> entry: attendedList.entrySet())
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
                            Toast.makeText(TakeAttendance.this, "Error Posting Attendance", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else
            Toast.makeText(TakeAttendance.this, "Error Posting Attendance to Database", Toast.LENGTH_SHORT).show();
    }
}
