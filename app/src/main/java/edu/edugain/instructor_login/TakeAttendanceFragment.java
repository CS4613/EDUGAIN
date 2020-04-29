package edu.edugain.instructor_login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.edugain.R;
import edu.edugain.models.Course;
import edu.edugain.viewholders.TeacherAttendanceViewHolder;

public class TakeAttendanceFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private RecyclerView attendance_recycler;
    private Spinner course_spinner;
    private Button cancel;

    private DatabaseReference database;
    private FirebaseAuth auth;
    private String current_user;

    private ArrayAdapter<String> spinner_adapter;
    private List<String> courseNames;

    private FirebaseRecyclerAdapter<Boolean, TeacherAttendanceViewHolder> recyclerAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_teacher_attendance, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();

        attendance_recycler = root.findViewById(R.id.attendance_recyclerview);
        course_spinner = root.findViewById(R.id.teacher_attendance_course_spinner);
        cancel = root.findViewById(R.id.btn_cancel_tAttendance);
        //submit = root.findViewById(R.id.btn_submit_tAttendance);

        attendance_recycler.setHasFixedSize(true);
        attendance_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSpinner();
        setAdapter(courseNames.get(0));

        course_spinner.setOnItemSelectedListener(this);
        cancel.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        if(v == cancel)
        {
            new AlertDialog.Builder(getContext())
                .setTitle("Attendance")
                .setMessage("Are you sure you want to cancel taking Attendance ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getContext(), "Attendance Not taken", Toast.LENGTH_SHORT).show();
                        course_spinner.setSelection(0);
                        setAdapter(courseNames.get(0));
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(R.drawable.attendance80)
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

        database
        .child("instructor_courses")
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

        recyclerAdapter = new FirebaseRecyclerAdapter<Boolean, TeacherAttendanceViewHolder>
                (Boolean.class, R.layout.list_item_teacher_take_attendance, TeacherAttendanceViewHolder.class, database.child("courses_students").child(course_id))
        {
            @Override
            protected void populateViewHolder(final TeacherAttendanceViewHolder teacherAttendanceViewHolder, final Boolean element, int i) {
                final String student_id = this.getRef(i).getKey();
                database
                .child("users")
                .child(student_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String student_name = dataSnapshot.child("name").getValue().toString();
                        teacherAttendanceViewHolder.setStudentName(student_name);
                        teacherAttendanceViewHolder.setCourseName(course_id);
                        /*
                        attendance = teacherAttendanceViewHolder.getAttendance();
                        attendance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                switch (checkedId)
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
                                        retValue = null;
                                }
                            }
                        });
                        studentId_attended.put(student_id, retValue);
                        TeacherAttendance tA = new TeacherAttendance(student_name, student_id, course_id, retValue);
                        elements.add(tA);

                         */
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                teacherAttendanceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prompt(student_id, course_id);
                    }
                });

            }

        };
        attendance_recycler.setAdapter(recyclerAdapter);
    }

    private void prompt(final String student_id, final String course_id) {

        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.prompt_take_attendance, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setView(promptsView);

        final String at = null;
        final RadioGroup group = promptsView.findViewById(R.id.attendance_radio_group);
        RadioButton present = promptsView.findViewById(R.id.present_radio);
        RadioButton absent = promptsView.findViewById(R.id.absent_radio);
        RadioButton excused = promptsView.findViewById(R.id.excused_radio);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Post",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                setAtt(student_id, course_id, getAttendance(group.getCheckedRadioButtonId()));
                            }
                        })
                .setNegativeButton("Cancel", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void setAtt(final String student_id, final String course_id, final String at) {
        database
        .child("course_attendance")
        .child(course_id)
        .child(getDate())
        .child(student_id)
        .setValue(at)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    database
                    .child("student_attendance")
                    .child(student_id)
                    .child(getDate())
                    .child(course_id)
                    .setValue(at)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(getContext(), "Attendance taken Successfully", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(), "Error in Student Attendance!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                    Toast.makeText(getContext(), "Error in Course Attendance!!!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private String getAttendance(int checkedId) {

        String retValue;
        switch (checkedId)
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
                retValue = "none";
        }
        return retValue;
    }

    private String getDate()
    {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(todayDate);
    }
}
