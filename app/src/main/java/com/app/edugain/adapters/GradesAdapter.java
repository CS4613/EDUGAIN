package com.app.edugain.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.edugain.models.User;
import com.app.edugain.viewholders.TeacherGradesViewHolder;
import com.example.edugain.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class GradesAdapter extends RecyclerView.Adapter<TeacherGradesViewHolder> {

    private DatabaseReference database;
    private String course_id;
    private String course_name;
    private int itemCount;
    public GradesAdapter(DatabaseReference databaseReference, String course_id, String course_name)
    {
        this.database = databaseReference;
        this.course_id = course_id;
        this.course_name = course_name;
    }

    @NonNull
    @Override
    public TeacherGradesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_grades_list_item, parent, false);
        TeacherGradesViewHolder t = new TeacherGradesViewHolder(view);
        return t;
    }

    @Override
    public void onBindViewHolder(@NonNull final TeacherGradesViewHolder holder, final int position) {
        database
                .child("courses_students")
                .child(course_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        itemCount = (int) dataSnapshot.getChildrenCount();
                        for (DataSnapshot snap: dataSnapshot.getChildren())
                        {
                            String student_id = snap.getKey();
                            database
                                    .child("users")
                                    .child(student_id)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User studentData = dataSnapshot.getValue(User.class);
                                            holder.setCourseName(course_name);
                                            holder.setStudentName(studentData.getName());
                                            //student_grade.put(student_id, holder.getGrades());
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
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }
}
