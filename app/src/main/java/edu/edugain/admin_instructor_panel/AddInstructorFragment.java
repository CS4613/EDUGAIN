package edu.edugain.admin_instructor_panel;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.edugain.R;
import edu.edugain.models.User;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AddInstructorFragment extends Fragment {

    private EditText name, email, password, phone, address;
    private Button addInstructor;

    private FirebaseAuth auth;
    private DatabaseReference database;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_add_instructor, container, false);

        name = root.findViewById(R.id.instructorName);
        email = root.findViewById(R.id.instructorEmail);
        password = root.findViewById(R.id.instructorPassword);
        phone = root.findViewById(R.id.instructorPhone);
        address = root.findViewById(R.id.instructorAddress);
        addInstructor = root.findViewById(R.id.btn_addInstructor);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        addInstructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        return root;
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        addInstructor.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String _name = name.getText().toString();
        final String _email = email.getText().toString();
        final String _password = password.getText().toString();
        final String _phone = phone.getText().toString();
        final String _address = address.getText().toString();

        auth.createUserWithEmailAndPassword(_email, _password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String uid = task.getResult().getUser().getUid();
                        User user = new User(_address, _email, uid, _name, _phone, "instructor");
                        database
                                .child("users")
                                .child(uid)
                                .setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                            Toast.makeText(getActivity(), "Instructor Added Successfully", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                        if (!task.isSuccessful())
                                            Toast.makeText(getActivity(), "Error adding Instructor", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

    }

    public void onSignupFailed() {
        Toast.makeText(getContext(), "Adding failed", Toast.LENGTH_LONG).show();
        addInstructor.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String _name = name.getText().toString();
        String _email = email.getText().toString();
        String _password = password.getText().toString();
        String _phone = phone.getText().toString();
        String _address = address.getText().toString();

        if (_name.isEmpty() || _name.length() < 3) {
            name.setError("at least 3 characters");
            valid = false;
        } else {
            name.setError(null);
        }

        if (_email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(_email).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (_password.isEmpty() || _password.length() < 4 ) {
            password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
        }

        if(_phone.isEmpty() || _phone.length() < 10)
        {
            phone.setError("Phone Number should be 10 digits ONLY");
        }
        else
        {
            phone.setError(null);
        }

        if(_address.isEmpty() || _address.length() < 5)
        {
            address.setError("Enter Valid Address");
        }
        else
            address.setError(null);

        return valid;
    }
}
