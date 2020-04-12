package com.app.edugain.student_login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.edugain.adapters.CourseNameAdapter;
import com.example.edugain.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewCourseFragment extends Fragment {

    private RecyclerView registered_course_list;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private String current_user;
    private CourseNameAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_student_view_course, container, false);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        registered_course_list = root.findViewById(R.id.fr_view_course_recycler);
        registered_course_list.setLayoutManager(new LinearLayoutManager(getContext()));
        registered_course_list.setHasFixedSize(true);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*
        FirebaseRecyclerAdapter<Boolean, CourseViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Boolean, CourseViewHolder>
                (Boolean.class, R.layout.course_list_item, CourseViewHolder.class, database.child("student_courses").child(current_user)) {
            @Override
            protected void populateViewHolder(final CourseViewHolder courseHandler, final Boolean is_course_registered, int i) {
                String course_id = this.getRef(i).getKey();
                database.child("courses").child(course_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Course course = dataSnapshot.getValue(Course.class);
                        courseHandler.setCourseName(course.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(StudentCourseView.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        registered_course_list.setAdapter(recyclerAdapter);

         */
        invokeAdapter();
    }

    public void invokeAdapter()
    {
        adapter = new CourseNameAdapter("student_courses", getContext(), current_user);
        registered_course_list.setAdapter(adapter.getRecyclerAdapter());
    }
}
