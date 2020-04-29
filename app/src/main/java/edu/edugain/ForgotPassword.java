package edu.edugain;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private Button sendEmail;
    private EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        sendEmail = findViewById(R.id.btn_reset);
        email = findViewById(R.id.reset_email);
        final String reset_email = email.getText().toString().trim();
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(reset_email))
                    Toast.makeText(ForgotPassword.this, "Field should not be empty", Toast.LENGTH_SHORT).show();
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(reset_email).matches())
                    Toast.makeText(ForgotPassword.this, "Not a Valid Email", Toast.LENGTH_SHORT).show();
                else
                {
                    FirebaseAuth
                    .getInstance()
                    .sendPasswordResetEmail(reset_email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(ForgotPassword.this, "Password Reset Email sent to "+reset_email, Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(ForgotPassword.this, "Password Reset Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
