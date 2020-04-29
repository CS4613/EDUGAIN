package edu.edugain.student_login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import edu.edugain.R;
import edu.edugain.models.Course;
import edu.edugain.viewholders.CourseViewHolder;

public class RegisterCourseFragment extends Fragment {

    private RecyclerView available_course_list;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private String current_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View root = inflater.inflate(R.layout.fragment_register_course, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();

        available_course_list = root.findViewById(R.id.fr_register_recycler);
        available_course_list.setHasFixedSize(true);
        available_course_list.setLayoutManager(new LinearLayoutManager(getContext()));

         return root;
    }

    @Override
    public void onStart() {
        super.onStart();
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
                        database.child("student_courses").child(current_user).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(course_id))
                                {
                                    Toast.makeText(getContext(), "Course Already Registered", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Register")
                                            .setMessage("Do you want to Register for Course "+course_id+" ?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    database.child("student_courses").child(current_user).child(course_id).setValue(true);
                                                    database.child("courses_students").child(course_id).child(current_user).setValue(true);
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

        available_course_list.setAdapter(recyclerAdapter);
    }
}
