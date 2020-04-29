package edu.edugain.student_login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import edu.edugain.viewholders.StudentGradesViewHolder;


public class StudentGradesFragment extends Fragment {

    private RecyclerView grades_recycler;
    private DatabaseReference database;
    private FirebaseAuth auth;
    private String current_user;
    private String course_name, course_instructor;
    private String course_id;
    private String grade;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_student_grades, container, false);

        grades_recycler = root.findViewById(R.id.fr_student_grades_recycler);
        grades_recycler.setHasFixedSize(true);
        grades_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        database = FirebaseDatabase.getInstance().getReference();
        database.keepSynced(true);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser().getUid();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<String, StudentGradesViewHolder> adapter = new FirebaseRecyclerAdapter<String, StudentGradesViewHolder>
                (String.class, R.layout.list_item_student_grades, StudentGradesViewHolder.class, database.child("student_grades").child(current_user)){

            @Override
            protected void populateViewHolder(final StudentGradesViewHolder gradesViewHolder, String studentGrades, int i) {
                course_id = this.getRef(i).getKey();
                grade = studentGrades;

                database.child("courses").child(course_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Course course = dataSnapshot.getValue(Course.class);
                        course_name = course.getName();
                        gradesViewHolder.setCourseName(course_name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                gradesViewHolder.setGrade(grade);
            }
        };

        grades_recycler.setAdapter(adapter);
    }
}
