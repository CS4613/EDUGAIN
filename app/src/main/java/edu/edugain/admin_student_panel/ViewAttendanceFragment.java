package edu.edugain.admin_student_panel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.edugain.R;
import edu.edugain.viewholders.TeacherViewAttendanceViewHolder;


public class ViewAttendanceFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Spinner student_spinner;
    private RecyclerView attendance_recycler;

    private DatabaseReference database;

    private ArrayAdapter<String> student_adapter;
    private List<String> students_list;

    private FirebaseRecyclerAdapter<Boolean, TeacherViewAttendanceViewHolder> recyclerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_view_attendance, container, false);

        student_spinner = root.findViewById(R.id.admin_view_attendance_student_spinner);
        attendance_recycler = root.findViewById(R.id.admin_view_attendance_recyclerview);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        attendance_recycler.setHasFixedSize(true);
        attendance_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSpinner();
        setAdapter(students_list.get(0));

        student_spinner.setOnItemSelectedListener(this);

        return root;
    }

    private void loadSpinner() {
        students_list = new ArrayList<>();
        students_list.add("Select a Student");

        database.child("users").orderByChild("role")
                .equalTo("student")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap : dataSnapshot.getChildren())
                        {
                            students_list.add(snap.child("name").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        student_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, students_list);
        student_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        student_spinner.setAdapter(student_adapter);
    }

    private void setAdapter(final String student_id)
    {
        recyclerAdapter = new FirebaseRecyclerAdapter<Boolean, TeacherViewAttendanceViewHolder>
                (Boolean.class, R.layout.list_item_teacher_view_attendance, TeacherViewAttendanceViewHolder.class
                , database.child("student_courses").child(student_id)) {
            @Override
            protected void populateViewHolder(final TeacherViewAttendanceViewHolder t, Boolean s, int i) {
                String course_id = this.getRef(i).getKey();
                DatabaseReference ref = database.child("course_attendance").child(course_id);
                //Setting up name
                database.child("courses").child(course_id).addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String student_name = parent.getSelectedItem().toString();
        database
        .child("users")
        .orderByChild("name")
        .equalTo(student_name)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    String id = snap.child("id").getValue().toString();
                    setAdapter(id);
                }
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
