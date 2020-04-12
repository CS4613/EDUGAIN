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
import com.app.edugain.models.User;
import com.app.edugain.viewholders.TeacherGradesViewHolder;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradesFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private RecyclerView grades_recycler;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private Spinner course_spinner;
    private Map<String, String> courseId_courseNames = new HashMap<>();
    private Map<String, String> studentId_grade = new HashMap<>();
    private List<String> courseNames = new ArrayList<>();
    private ArrayAdapter<String> spinner_adapter;
    private Button cancel, submit;
    private String current_user;
    private String course_id;
    FirebaseRecyclerAdapter<Boolean, TeacherGradesViewHolder> recyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_teacher_grades, container, false);

        grades_recycler = root.findViewById(R.id.teacher_grades_recyclerview);
        course_spinner = root.findViewById(R.id.teacher_grades_course_spinner);
        cancel = root.findViewById(R.id.btn_cancel_tGrades);
        submit = root.findViewById(R.id.btn_submit_tGrades);

        grades_recycler.setHasFixedSize(true);
        grades_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();

        populateSpinner();
        course_spinner.setOnItemSelectedListener(this);

        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    private void populateSpinner() {
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
                            courseNames.add(course.getName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                spinner_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, courseNames);
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
            new AlertDialog.Builder(getActivity())
                    .setTitle("Grades")
                    .setMessage("Are you sure you want to cancel posting Grades ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(getContext(), "Grades Not posted", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(R.drawable.attendance80)
                    .show();
        }
        else if(v.getId() == R.id.btn_submit_tAttendance)
        {
            setGrades(studentId_grade, course_id);
            Toast.makeText(getContext(), "Attendance taken Successfully", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(),"Error Posting Grades", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
        else
            Toast.makeText(getActivity(), "Error in Database", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.teacher_grades_course_spinner)
        {
            parent.setSelection(position);
            course_id = parent.getItemAtPosition(position).toString();
            recyclerAdapter = new FirebaseRecyclerAdapter<Boolean, TeacherGradesViewHolder>(Boolean.class, R.layout.teacher_grades_list_item, TeacherGradesViewHolder.class, database.child("courses_students").child(course_id)) {
                @Override
                protected void populateViewHolder(final TeacherGradesViewHolder teacherGradesViewHolder, Boolean aBoolean, int i) {
                    final String student_id = this.getRef(i).getKey();
                    database.child("users").child(student_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                User student = dataSnapshot.getValue(User.class);
                                teacherGradesViewHolder.setStudentName(student.getName());
                                teacherGradesViewHolder.setCourseName(course_id);
                                studentId_grade.put(student_id, teacherGradesViewHolder.getGrades());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Error in Database", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            };
            grades_recycler.setAdapter(recyclerAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getContext(), "Please Select an Item", Toast.LENGTH_SHORT).show();
    }
}
