package edu.edugain.admin_tool_panel;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.edugain.R;

public class AddDepartmentFragment extends Fragment {

    private EditText id, name;
    private Button add;

    private DatabaseReference database;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_dept, container, false);

        id = root.findViewById(R.id.add_dept_id);
        name = root.findViewById(R.id.add_dept_name);
        add = root.findViewById(R.id.btn_add_dept);

        database = FirebaseDatabase.getInstance().getReference();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDept();
            }
        });
        return root;
    }

    private void addDept() {
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

        database
                .child("departments")
        .child(_id)
        .child("dept_id")
        .setValue(_id)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    database
                            .child("departments")
                    .child(_id)
                    .child("name")
                    .setValue(_name)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful())
                                Toast.makeText(getActivity(), "Department Added Successfully!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getActivity(), "Error Adding Department!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void onAddingFailed() {
        Toast.makeText(getContext(), "Adding failed", Toast.LENGTH_LONG).show();
        add.setEnabled(true);
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

        if (_id.isEmpty() || _id.length() < 2) {
            id.setError("at least 2 characters");
            valid = false;
        } else {
            id.setError(null);
        }

        return valid;
    }
}
