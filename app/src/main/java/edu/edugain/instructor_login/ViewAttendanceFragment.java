package edu.edugain.instructor_login;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import edu.edugain.viewholders.TeacherViewAttendanceViewHolder;


public class ViewAttendanceFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Spinner course_spinner;
    private TextView course_name;
    private TextView total;
    private RecyclerView attendance_recycler;

    private ArrayAdapter<String> spinner_adapter;
    private List<String> courseNames;

    private DatabaseReference database;
    private FirebaseAuth auth;
    private String current_user;

    private FirebaseRecyclerAdapter<Boolean, TeacherViewAttendanceViewHolder> recyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_instructor_view_attendance, container, false);

        course_spinner = root.findViewById(R.id.teacher_view_attendance_spinner);
        course_name = root.findViewById(R.id.attendance_instructor_courseName);
        total = root.findViewById(R.id.attendance_instructor_total);
        attendance_recycler = root.findViewById(R.id.inst_view_att_recycler);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);
        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();

        attendance_recycler.setHasFixedSize(true);
        attendance_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSpinner();
        setAdapter(courseNames.get(0));

        course_spinner.setOnItemSelectedListener(this);

        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        getCourseName(parent.getSelectedItem().toString());
        getTotalNum(parent.getSelectedItem().toString());
        setAdapter(parent.getSelectedItem().toString());
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setAdapter(final String course_id) {
        recyclerAdapter = new FirebaseRecyclerAdapter<Boolean, TeacherViewAttendanceViewHolder>
                (Boolean.class, R.layout.list_item_teacher_view_attendance, TeacherViewAttendanceViewHolder.class, database.child("courses_students").child(course_id)) {
            @Override
            protected void populateViewHolder(final TeacherViewAttendanceViewHolder t, Boolean s, int i) {
                final String student_id = this.getRef(i).getKey();
                DatabaseReference ref = database.child("course_attendance").child(course_id);
                //Setting up name
                database.child("users").child(student_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        t.setName(dataSnapshot.child("name").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //Setting up Present number
                ref.orderByChild(student_id).equalTo("Present").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                t.setPresent(Long.toString(dataSnapshot.getChildrenCount()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                //Setting up Absent number
                ref.orderByChild(student_id).equalTo("Absent").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                t.setAbsent(Long.toString(dataSnapshot.getChildrenCount()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                //Setting up Excused number
                ref.orderByChild(student_id).equalTo("Excused").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                t.setExcused(Long.toString(dataSnapshot.getChildrenCount()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        };
        attendance_recycler.setAdapter(recyclerAdapter);
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

    private void getTotalNum(String course_id) {
        database
                .child("course_attendance")
                .child(course_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        total.setText("Total: "+Long.toString(dataSnapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
