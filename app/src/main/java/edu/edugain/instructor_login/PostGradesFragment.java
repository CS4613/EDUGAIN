package edu.edugain.instructor_login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

import edu.edugain.R;
import edu.edugain.models.Course;
import edu.edugain.viewholders.TeacherGradesViewHolder;

public class PostGradesFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    //UI Elements
    private Button cancel;
    private Spinner course_spinner;
    private RecyclerView grades_recycler;

    // Firebase Elemetns
    private DatabaseReference database;
    private FirebaseAuth auth;
    private String current_user;

    //Spinner elements
    private ArrayAdapter<String> spinner_adapter;
    private List<String> courseNames;

    private FirebaseRecyclerAdapter<Boolean, TeacherGradesViewHolder> recyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_teacher_grades, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();

        course_spinner = root.findViewById(R.id.teacher_grades_course_spinner);
        cancel = root.findViewById(R.id.btn_cancel_tGrades);
        //submit = root.findViewById(R.id.btn_submit_tGrades);
        grades_recycler = root.findViewById(R.id.teacher_grades_recyclerview);

        grades_recycler.setHasFixedSize(true);
        grades_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadSpinner();
        setAdapter(courseNames.get(0));

        course_spinner.setOnItemSelectedListener(this);
        cancel.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_cancel_tGrades)
        {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Grades")
                    .setMessage("Are you sure you want to cancel posting Grades ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Toast.makeText(getActivity(), "Grades Not posted", Toast.LENGTH_SHORT).show();
                            course_spinner.setSelection(0);
                            setAdapter(courseNames.get(0));

                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(R.drawable.grades80)
                    .show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        setAdapter(parent.getSelectedItem().toString());
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadSpinner() {
        courseNames = new ArrayList<>();
        courseNames.add("Select a Course");

        database.child("instructor_courses")
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

    private void setAdapter(final String course_id) {

        recyclerAdapter = new FirebaseRecyclerAdapter<Boolean, TeacherGradesViewHolder>
                (Boolean.class, R.layout.list_item_teacher_grades, TeacherGradesViewHolder.class,database.child("courses_students").child(course_id) ) {
            @Override
            protected void populateViewHolder(final TeacherGradesViewHolder teacherGradesViewHolder, Boolean aBoolean, int i) {
                final String student_id = this.getRef(i).getKey();
                database
                        .child("users")
                        .child(student_id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String student_name = dataSnapshot.child("name").getValue().toString();
                                teacherGradesViewHolder.setStudentName(student_name);
                                teacherGradesViewHolder.setCourseName(course_id);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                teacherGradesViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prompt(student_id, course_id);
                    }
                });
            }
        };
        grades_recycler.setAdapter(recyclerAdapter);
    }

    private void prompt(final String student_id, final String course_id) {

        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.prompt_post_grades, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setView(promptsView);

        final String at = null;
        final RadioGroup group = promptsView.findViewById(R.id.grades_radio_group);


        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Post",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                setGrades(student_id, course_id, getGrade(group.getCheckedRadioButtonId()));
                            }
                        })
                .setNegativeButton("Cancel", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void setGrades(final String student_id, final String course_id, final String grade)
    {
        database
        .child("course_grades")
        .child(course_id)
        .child(student_id)
        .setValue(grade)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    database
                    .child("student_grades")
                    .child(student_id)
                    .child(course_id)
                    .setValue(grade)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(getContext(), "Grades taken Successfully", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(), "Error in Student-Grades!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                    Toast.makeText(getContext(), "Error in Course-Grades!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getGrade(int checkedId)
    {
        String retValue;
        switch (checkedId)
        {
            case(R.id.grade_a_radio) :
                retValue = "A";
                break;

            case(R.id.grade_b_radio) :
                retValue =  "B";
                break;

            case(R.id.grade_c_radio) :
                retValue =  "C";
                break;
            case(R.id.grade_d_radio) :
                retValue =  "D";
                break;
            case(R.id.grade_f_radio) :
                retValue =  "F";
                break;
            default:
                retValue = "none";
        }
        return retValue;
    }

}
