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
import com.example.edugain.R;
import com.example.edugain.R.layout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AssignGrades extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView student_list;
    private Button grades_cancel, grades_submit;
    private String course_id, current_user, course_name;
    private DatabaseReference database;
    //private FirebaseRecyclerAdapter<Boolean, TeacherGradesViewHolder> adapter;
    private Map<String, String> student_grade = new HashMap<>();
    private Intent backToTeacherLogin = new Intent(AssignGrades.this, TeacherLogin.class);

    private GradesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_grades);

        student_list = findViewById(R.id.grades_student_list_recycler);
        student_list.setHasFixedSize(true);
        student_list.setLayoutManager(new LinearLayoutManager(this));

        grades_cancel = findViewById(R.id.btn_cancel_teacher_grades);
        grades_submit = findViewById(R.id.btn_submit_teacher_grades);

        grades_submit.setOnClickListener(this);
        grades_cancel.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            course_name = bundle.getString("course_name");
            course_id = bundle.getString("course_id");
            current_user = bundle.getString("current_user");
        }

        database = FirebaseDatabase.getInstance().getReference();
        /*
        adapter = new FirebaseRecyclerAdapter<Boolean, TeacherGradesViewHolder>
                (Boolean.class,
                 layout.teacher_grades_list_item,
                 TeacherGradesViewHolder.class,
                 database.child("courses_students").child(course_id)) {
            @Override
            protected void populateViewHolder(final TeacherGradesViewHolder teacherGradesViewHolder, Boolean aBoolean, int i) {
                final String student_id = getRef(i).getKey();
                database
                .child("users")
                .child(student_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User studentData = dataSnapshot.getValue(User.class);
                        teacherGradesViewHolder.setCourseName(course_name);
                        teacherGradesViewHolder.setStudentName(studentData.getName());
                        student_grade.put(student_id, teacherGradesViewHolder.getGrades());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

         */

        adapter = new GradesAdapter(database, course_id, course_name);
        student_list.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(backToTeacherLogin);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_cancel_teacher_grades)
        {
            new AlertDialog.Builder(this)
            .setTitle("Grades")
            .setMessage("Are you sure you want to cancel Grading")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Toast.makeText(AssignGrades.this, "Grades Not Taken", Toast.LENGTH_SHORT).show();
                    startActivity(backToTeacherLogin);
                }
            })
            .setNegativeButton("No", null)
            .setIcon(R.drawable.grades80)
            .show();
        }

        if(v.getId() == R.id.btn_submit_teacher_grades)
        {
            setGrades(student_grade, course_id);
            Toast.makeText(AssignGrades.this, "Grades Posted Successfully", Toast.LENGTH_SHORT).show();
            startActivity(backToTeacherLogin);
        }

    }

    private void setGrades(Map<String, String> student_grade, String course_id)
    {
        if(student_grade != null)
        {
            for (Map.Entry<String, String> entry: student_grade.entrySet())
            {
                database
                        .child("student_grades")
                        .child(entry.getKey())
                        .child(course_id)
                        .setValue(entry.getValue())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful())
                                    Toast.makeText(AssignGrades.this,"Error Posting Grades", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        else
            Toast.makeText(AssignGrades.this, "Error in Database", Toast.LENGTH_SHORT).show();

    }
}
