package com.app.edugain;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edugain.R;

public class Register extends AppCompatActivity {
    public EditText Username;
    public EditText Password;
    public EditText Email;
    public EditText Number;
    public Button Register;
    public Button Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Username = (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPass);
        Email = (EditText) findViewById(R.id.etEmail);
        Number = (EditText) findViewById(R.id.etNum);
        Login = (Button) findViewById(R.id.btLog);
        Register = (Button) findViewById(R.id.btSignin);

        Username.addTextChangedListener(buttontext);
        Password.addTextChangedListener(buttontext);
        Number.addTextChangedListener(buttontext);
        Email.addTextChangedListener(buttontext);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public TextWatcher buttontext = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String user = Username.getText().toString();
            String Pass = Password.getText().toString();
            String emai = Email.getText().toString();
            String no = Number.getText().toString();
            Register.setEnabled(!user.isEmpty() && !Pass.isEmpty() && !emai.isEmpty() && !no.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

}


