package com.app.edugain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.edugain.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView Attempts;
    private Button Login;
    private Spinner spinner;
    private RadioGroup role_group;
    private int counter = 5;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       auth = FirebaseAuth.getInstance();

        Name = findViewById(R.id.etname);
        Password = findViewById(R.id.etpass);
        Login = findViewById(R.id.etlogin);
       // spinner = findViewById(R.id.spinid);
        Attempts = findViewById(R.id.tvattempts);
        role_group = findViewById(R.id.rolegroup);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Usertype, R.layout.support_simple_spinner_dropdown_item);
        //spinner.setAdapter(adapter);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // final String role = spinner.getSelectedItem().toString().trim();
                final int selected_role = role_group.getCheckedRadioButtonId();
                String email = Name.getText().toString().trim();
                String password = Password.getText().toString().trim();

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            final String currentUserId = auth.getCurrentUser().getUid();
                            roleCheck(selected_role, currentUserId);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Invalid Id or Password", Toast.LENGTH_LONG).show();
                            counter--;
                            Attempts.setText("No of Attempts Remaining  "+counter);
                            if(counter == 0){
                                Login.setEnabled(false);
                            }
                        }
                    }
                });
            }
        });
    }

    private void roleCheck(final int role, final String currentUserId)
    {
        DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        switch (role) {
            case R.id.adminRadio:
                currentUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean admExists = dataSnapshot.child("isAdmin").exists();
                        if (admExists) {
                            boolean isAdmin = dataSnapshot.child("isAdmin").getValue(Boolean.class);
                            if (isAdmin) {
                                Toast.makeText(MainActivity.this, "Logged in as Admin", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, AdminLogin.class));
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Not registered as Admin", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();

                    }
                });

                break;
            case R.id.instRadio:
                currentUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean instExists = dataSnapshot.child("isInstructor").exists();
                        if (instExists) {
                            boolean isInstructor = dataSnapshot.child("isInstructor").getValue(Boolean.class);
                            if (isInstructor) {
                                startActivity(new Intent(MainActivity.this, InstructorLogin.class));
                                Toast.makeText(MainActivity.this, "Logged in as Instructor", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Not registered as Instructor", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            case R.id.studRadio:
                currentUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean studExists = dataSnapshot.child("student").exists();
                        if (studExists) {
                            boolean isStudent = dataSnapshot.child("student").getValue(Boolean.class);
                            if (isStudent) {
                                startActivity(new Intent(MainActivity.this, StudentLogin.class));
                                Toast.makeText(MainActivity.this, "Logged in as Student", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Not registered as Student", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            default:
                Toast.makeText(MainActivity.this, "Please Select the Role", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

