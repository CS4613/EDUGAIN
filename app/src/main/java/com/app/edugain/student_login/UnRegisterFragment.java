package com.app.edugain.student_login;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.edugain.adapters.CourseNameAdapter;
import com.app.edugain.models.Course;
import com.app.edugain.viewholders.CourseViewHolder;
import com.example.edugain.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UnRegisterFragment extends Fragment {

    private RecyclerView unregister_recycler;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private String current_user;
    private CourseNameAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_un_register, container, false);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        unregister_recycler = root.findViewById(R.id.fr_unregister_course);
        unregister_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        unregister_recycler.setHasFixedSize(true);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Boolean, CourseViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<Boolean, CourseViewHolder>
                (Boolean.class, R.layout.course_list_item, CourseViewHolder.class,database.child("student_courses").child(current_user)) {
            @Override
            protected void populateViewHolder(final CourseViewHolder courseHandler, final Boolean course, int i) {
                final String course_id = this.getRef(i).getKey();
                database.child("courses").child(course_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Course course = dataSnapshot.getValue(Course.class);
                        courseHandler.setCourseName(course.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                courseHandler.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Registration")
                                .setMessage("Do you want to UnRegister for Course "+course_id+" ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        database.child("student_courses").child(current_user).child(course_id).removeValue();
                                        Toast.makeText(getContext(), course_id+" has been Un-Registered", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .setIcon(R.drawable.course80)
                                .show();
                    }
                });
            }
        };

        unregister_recycler.setAdapter(recyclerAdapter);
    }

    public void invokeAdapter()
    {
        adapter = new CourseNameAdapter("student_courses", getContext(), current_user);
        unregister_recycler.setAdapter(adapter.getRecyclerAdapter());
    }
}
