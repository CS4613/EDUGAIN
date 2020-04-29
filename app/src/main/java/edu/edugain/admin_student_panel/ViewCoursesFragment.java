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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.edugain.R;
import edu.edugain.adapters.CourseNameAdapter;


public class ViewCoursesFragment extends Fragment {

    private RecyclerView course_recycler;
    private Spinner student_spinner;

    private DatabaseReference database;

    private ArrayAdapter<String> student_adapter;
    private List<String> students_list;
    private CourseNameAdapter recyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_admin_view_courses, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        course_recycler = root.findViewById(R.id.admin_view_courses_recyclerview);
        student_spinner = root.findViewById(R.id.admin_view_courses_student_spinner);

        course_recycler.setHasFixedSize(true);
        course_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        students_list = new ArrayList<>();
        students_list.add("Select a Student");

        DatabaseReference ref = database.child("users");
        ref
        .orderByChild("role")
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

        setAdapter(students_list.get(0));
        student_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String student_name = parent.getSelectedItem().toString();
                DatabaseReference ref = database.child("users");
                ref
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
                        recyclerAdapter.getRecyclerAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;

    }

    private void setAdapter(String student_id) {
        recyclerAdapter = new CourseNameAdapter("student_courses", getContext(), student_id);
        course_recycler.setAdapter(recyclerAdapter.getRecyclerAdapter());

    }
}
