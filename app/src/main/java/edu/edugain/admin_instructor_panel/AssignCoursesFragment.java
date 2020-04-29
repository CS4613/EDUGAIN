package edu.edugain.admin_instructor_panel;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.edugain.R;
import edu.edugain.models.Course;
import edu.edugain.viewholders.CourseViewHolder;

public class AssignCoursesFragment extends Fragment {

    private RecyclerView course_recycler;
    private Spinner instructor_spinner;

    private DatabaseReference database;

    private ArrayAdapter<String> instructor_adapter;
    private List<String> instructors_list;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_assign_courses, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        course_recycler = root.findViewById(R.id.admin_assign_courses_recycler);
        instructor_spinner = root.findViewById(R.id.admin_assign_courses_spinner);

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

    public void setAdapter(final String instructor_id)
    {
        FirebaseRecyclerAdapter<Course, CourseViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>
                (Course.class, R.layout.list_item_course, CourseViewHolder.class,database.child("courses")) {
            @Override
            protected void populateViewHolder(CourseViewHolder courseHandler, Course course, int i) {
                String course_name = course.getName();
                final String course_id = course.getCourse_id();
                courseHandler.setCourseName(course_name);

                courseHandler.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        database.child("instructor_courses").child(instructor_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(course_id))
                                {
                                    Toast.makeText(getContext(), "Course Already Assigned", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Assign")
                                            .setMessage("Do you want to Assign Course "+course_id+" ?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    database.child("instructor_courses").child(instructor_id).child(course_id).setValue(true);
                                                    Toast.makeText(getContext(), course_id+" has been Registered", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .setIcon(R.drawable.course80)
                                            .show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                });
            }
        };

        course_recycler.setAdapter(recyclerAdapter);
    }
}
