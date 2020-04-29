package edu.edugain.instructor_login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import edu.edugain.viewholders.StudentGradesViewHolder;


public class ViewGradesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private RecyclerView grades_recycler;
    private Spinner course_spinner;

    private DatabaseReference database;
    private FirebaseAuth auth;
    private String current_user;

    private List<String> course_list;

    private FirebaseRecyclerAdapter<String, StudentGradesViewHolder> recyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_instructor_view_grades, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();

        grades_recycler = root.findViewById(R.id.instructor_view_grades_recycler);
        course_spinner = root.findViewById(R.id.instructor_view_grades_spinner);

        grades_recycler.setHasFixedSize(true);
        grades_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadSpinner();
        setAdapter(course_list.get(0));

        course_spinner.setOnItemSelectedListener(this);

        return root;
    }

    private void loadSpinner() {
        course_list = new ArrayList<>();
        course_list.add("Select a Course");

        database
        .child("instructor_courses")
                .child(current_user)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snap : dataSnapshot.getChildren())
                        {
                            database.child("courses").child(snap.getKey()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    Course course = dataSnapshot.getValue(Course.class);
                                    course_list.add(course.getCourse_id());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        ArrayAdapter<String> course_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, course_list);
        course_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course_spinner.setAdapter(course_adapter);
    }

    private void setAdapter(String course_id)
    {
        recyclerAdapter
                = new FirebaseRecyclerAdapter<String, StudentGradesViewHolder>
                (String.class,
                 R.layout.list_item_student_grades,
                 StudentGradesViewHolder.class,
                 database.child("course_grades").child(course_id)) {
            @Override
            protected void populateViewHolder(final StudentGradesViewHolder studentGradesViewHolder, final String grade, int i) {
                String student_id = this.getRef(i).getKey();

                database
                .child("users")
                .child(student_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        studentGradesViewHolder.setCourseName(dataSnapshot.child("name").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                studentGradesViewHolder.setGrade(grade);
            }
        };
        grades_recycler.setAdapter(recyclerAdapter);
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
}
