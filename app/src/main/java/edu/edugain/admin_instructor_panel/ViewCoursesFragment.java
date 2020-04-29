package edu.edugain.admin_instructor_panel;

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
    private Spinner instructor_spinner;

    private DatabaseReference database;

    private ArrayAdapter<String> instructor_adapter;
    private List<String> instructors_list;
    private CourseNameAdapter recyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_view_teaching_courses, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        course_recycler = root.findViewById(R.id.admin_view_teaching_courses_recyclerview);
        instructor_spinner = root.findViewById(R.id.admin_view_teaching_courses_spinner);

        course_recycler.setHasFixedSize(true);
        course_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        instructors_list = new ArrayList<>();
        instructors_list.add("Select a Instructor");

        DatabaseReference ref = database.child("users");
        ref
        .orderByChild("role")
        .equalTo("instructor")
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren())
                {
                    instructors_list.add(snap.child("name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        instructor_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, instructors_list);
        instructor_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instructor_spinner.setAdapter(instructor_adapter);

        setAdapter(instructors_list.get(0));
        instructor_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String instructor_name = parent.getSelectedItem().toString();
                DatabaseReference ref = database.child("users");
                ref
                .orderByChild("name")
                .equalTo(instructor_name)
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

    private void setAdapter(String instructor_id) {
        recyclerAdapter = new CourseNameAdapter("instructor_courses", getContext(), instructor_id);
        course_recycler.setAdapter(recyclerAdapter.getRecyclerAdapter());

    }
}
