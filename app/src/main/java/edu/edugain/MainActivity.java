package edu.edugain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.edugain.instructor_login.InstructorLogin;
import edu.edugain.student_login.StudentLogin;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextView Attempts;
    private Button Login;
    private RadioGroup role_group;
    private int counter;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.etname);
        password = findViewById(R.id.etpass);
        Login = findViewById(R.id.etlogin);
        Attempts = findViewById(R.id.tvattempts);
        role_group = findViewById(R.id.rolegroup);

        counter = 5;


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        final int selected_role = role_group.getCheckedRadioButtonId();
        String email = MainActivity.this.email.getText().toString().trim();
        String password = MainActivity.this.password.getText().toString().trim();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    final String currentUserId = auth.getCurrentUser().getUid();
                    roleCheck(selected_role, currentUserId);
                    Login.setEnabled(false);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Invalid Id or Password", Toast.LENGTH_LONG).show();
                    counter--;
                    Attempts.setText("No of Attempts "+ counter);
                    if(counter == 0){
                        Login.setEnabled(false);
                    }
                }
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
                        String admExists = dataSnapshot.child("role").getValue(String.class);
                        if (admExists.equals("admin")) {
                                Toast.makeText(MainActivity.this, "Logged in as Admin", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, AdminLogin.class));
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
                        String instExists = dataSnapshot.child("role").getValue(String.class);
                        if (instExists.equals("instructor")) {
                                startActivity(new Intent(MainActivity.this, InstructorLogin.class));
                                Toast.makeText(MainActivity.this, "Logged in as Instructor", Toast.LENGTH_SHORT).show();
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
                        String studExists = dataSnapshot.child("role").getValue(String.class);
                        if (studExists.equals("student")) {
                                startActivity(new Intent(MainActivity.this, StudentLogin.class));
                                Toast.makeText(MainActivity.this, "Logged in as Student", Toast.LENGTH_SHORT).show();
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

    public boolean validate() {
        boolean valid = true;

        String _email = email.getText().toString();
        String _password = password.getText().toString();


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

        return valid;
    }

    public void onLoginFailed() {
        Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_LONG).show();
        //Login.setEnabled(true);
    }
}

