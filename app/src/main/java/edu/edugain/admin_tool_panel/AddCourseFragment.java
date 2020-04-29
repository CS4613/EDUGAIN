package edu.edugain.admin_tool_panel;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.edugain.R;
import edu.edugain.models.Course;

public class AddCourseFragment extends Fragment {

    private EditText id, name;
    private Spinner dept_spinner;
    private Button add;

    private DatabaseReference database;

    private ArrayAdapter<String> spinner_adapter;
    private List<String> deptNames;

    private String dept_id;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_course, container, false);

        id = root.findViewById(R.id.add_course_id);
        name = root.findViewById(R.id.add_course_name);
        dept_spinner = root.findViewById(R.id.dept_spinner);
        add = root.findViewById(R.id.btn_add_course);

        database = FirebaseDatabase.getInstance().getReference();

        loadSpinner();

        dept_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDept(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse();
            }
        });
        return root;
    }

    private void setID(String dept_id) {
        id.setText(dept_id);
    }

    private void addCourse() {
        if (!validate()) {
            onAddingFailed();
            return;
        }

        add.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String _name = name.getText().toString();
        final String _id = id.getText().toString();
        Course course = new Course(_id, dept_spinner.getSelectedItem().toString(),_name );
        database
                .child("courses")
                .child(_id)
                .setValue(course)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful())
                            Toast.makeText(getActivity(), "Course Added Successfully!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getActivity(), "Error Adding Course!!!", Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void getDept(String dept_name) {
        database
                .child("departments")
                .orderByChild("name")
                .equalTo(dept_name)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap : dataSnapshot.getChildren())
                        {
                            dept_id = snap.child("dept_id").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void onAddingFailed() {
        Toast.makeText(getContext(), "Adding failed", Toast.LENGTH_LONG).show();
        add.setEnabled(true);
    }

    private void loadSpinner()
    {
        deptNames = new ArrayList<>();
        deptNames.add("Select a Department");

        database.child("departments")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(final DataSnapshot snap : dataSnapshot.getChildren())
                        {
                            deptNames.add(snap.child("name").getValue().toString());
                        }
                        spinner_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, deptNames);
                        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dept_spinner.setAdapter(spinner_adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private boolean validate() {
        boolean valid = true;
        String _name = name.getText().toString();
        String _id = id.getText().toString();

        if (_name.isEmpty() || _name.length() < 3) {
            name.setError("at least 3 characters");
            valid = false;
        } else {
            name.setError(null);
        }

        if (_id.isEmpty() || !_id.contains(dept_id)) {
            id.setError("enter a valid id");
            valid = false;
        } else {
            id.setError(null);
        }

        return valid;
    }
}
