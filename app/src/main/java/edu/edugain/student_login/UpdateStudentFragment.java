package edu.edugain.student_login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.edugain.MainActivity;
import edu.edugain.R;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class UpdateStudentFragment extends Fragment {

    private EditText phone, address;
    private Button btnUpdate;
    private CheckBox deleteProfile;
    private TextView updatePassword;

    private DatabaseReference database;
    private FirebaseAuth auth;
    private FirebaseUser current_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_update_student, container, false);

        phone = root.findViewById(R.id.editStudentPhone);
        address = root.findViewById(R.id.editStudentAddress);
        btnUpdate = root.findViewById(R.id.btn_updateStudent);
        deleteProfile = root.findViewById(R.id.chk_delete_profile);
        updatePassword = root.findViewById(R.id.updateStudentPassword);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        current_user = auth.getCurrentUser();

        database
        .child("users")
        .child(current_user.getUid())
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                phone.setText(dataSnapshot.child("phone").getValue().toString());
                address.setText(dataSnapshot.child("address").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteProfile.isChecked())
                {
                    new AlertDialog.Builder(getContext())
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete your profile ?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete();
                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(R.drawable.student80)
                    .show();
                }
                else
                    update();
            }
        });

        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reauth();
            }
        });

        return root;
    }
    private void reauth() {

        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.prompt_update_password, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setView(promptsView);

        EditText updateNewPassword = promptsView.findViewById(R.id.update_new_password);
        EditText updateOldPassword = promptsView.findViewById(R.id.update_old_password);
        EditText updateNewPasswordConf = promptsView.findViewById(R.id.update_new_password_conf);

        final String newPassword = updateNewPassword.getText().toString().trim();
        final String newPasswordConf = updateNewPasswordConf.getText().toString().trim();
        final String oldPassword = updateOldPassword.getText().toString().trim();

        alertDialogBuilder
        .setCancelable(false)
        .setPositiveButton("SignIn",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // get user input and set it to result
                        // edit text
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String email = user.getEmail();
                        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
                        user
                        .reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    if(newPassword.equals(newPasswordConf))
                                    {
                                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                    Toast.makeText(getActivity(), "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(getActivity(), "Password Not Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                    else
                                        Toast.makeText(getActivity(), "Current Password Not Correct", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {

                                }
                            }
                        });

                    }
                })
        .setNegativeButton("Cancel", null);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void delete() {

        // Realtime database
        database
        .child("users")
        .child(current_user.getUid())
        .removeValue()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    current_user
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                startActivity(new Intent(getContext(), MainActivity.class));
                        }
                    });
                }
            }
        });
    }

    private void update() {
        Log.d(TAG, "update");

        if(!validate())
        {
            onSignupFailed();
            return;
        }

        btnUpdate.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating Account...");
        progressDialog.show();

        final String _phone = phone.getText().toString();
        final String _address = address.getText().toString();

        //User user = new User(_address, email, current_user.getUid(), name, _phone, "student");
        database
        .child("users")
        .child(current_user.getUid())
        .child("phone")
        .setValue(_phone)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                database
                .child("users")
                .child(current_user.getUid())
                .child("address")
                .setValue(_address)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(getActivity(), "Student Details Updated Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        if (!task.isSuccessful())
                            Toast.makeText(getActivity(), "Error updating Student", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void onSignupFailed() {
        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();
        btnUpdate.setEnabled(true);
    }

    private boolean validate()
    {
        boolean valid = true;

        String _phone = phone.getText().toString();
        String _address = address.getText().toString();


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
