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
import edu.edugain.models.Course;
import edu.edugain.viewholders.StudentGradesViewHolder;


public class ViewGradesFragment extends Fragment {

    private RecyclerView view_grades_recycler;
    private Spinner students_spinner;

    private DatabaseReference database;

    private ArrayAdapter<String> student_adapter;
    private List<String> students_list;
    private String course_id, grade, course_name;

    private FirebaseRecyclerAdapter<String, StudentGradesViewHolder> recyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_admin_view_grades, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        view_grades_recycler = root.findViewById(R.id.admin_view_grades_recyclerview);
        students_spinner = root.findViewById(R.id.admin_view_grades_student_spinner);

        view_grades_recycler.setHasFixedSize(true);
        view_grades_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

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
        students_spinner.setAdapter(student_adapter);

        setAdapter(students_list.get(0));
        students_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });

        return root;
    }

    private void setAdapter(final String student_id) {
        recyclerAdapter
                = new FirebaseRecyclerAdapter<String, StudentGradesViewHolder>
                (String.class,
                 R.layout.list_item_student_grades,
                 StudentGradesViewHolder.class,
                 database.child("student_grades")
                .child(student_id)) {
            @Override
            protected void populateViewHolder(final StudentGradesViewHolder studentGradesViewHolder, String studentGrade, int i) {
                course_id = this.getRef(i).getKey();
                grade = studentGrade;
                database
                .child("courses")
                .child(course_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Course course = dataSnapshot.getValue(Course.class);
                        course_name = course.getName();
                        studentGradesViewHolder.setCourseName(course_name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                studentGradesViewHolder.setGrade(grade);
            }
        };
        view_grades_recycler.setAdapter(recyclerAdapter);

    }
}
