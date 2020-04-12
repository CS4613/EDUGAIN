package com.app.edugain.adapters;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.edugain.models.Course;
import com.app.edugain.viewholders.CourseViewHolder;
import com.example.edugain.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseNameAdapter {

    private DatabaseReference database;
    private String course_id;
    private String role;
    private FirebaseRecyclerAdapter<Boolean, CourseViewHolder> recyclerAdapter;


    public FirebaseRecyclerAdapter<Boolean, CourseViewHolder> getRecyclerAdapter() {
        return recyclerAdapter;
    }

    public CourseNameAdapter
            (final String role, final Context context, String current_user)
    {
        this.role = role;
        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);
        recyclerAdapter = new FirebaseRecyclerAdapter<Boolean, CourseViewHolder>
                (Boolean.class, R.layout.course_list_item, CourseViewHolder.class,database.child(role).child(current_user)) {
            @Override
            protected void populateViewHolder(final CourseViewHolder courseHandler, Boolean is_course_registered, int i) {
                course_id = this.getRef(i).getKey();
                database.child("courses").child(course_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Course course = dataSnapshot.getValue(Course.class);
                        courseHandler.setCourseName(course.getName());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

    }

    public CourseNameAdapter() {
    }
}
