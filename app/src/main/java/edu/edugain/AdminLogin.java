package edu.edugain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import edu.edugain.admin_instructor_panel.AdminInstructorPanel;
import edu.edugain.admin_student_panel.AdminStudentPanel;
import edu.edugain.admin_tool_panel.AdminToolPanel;

public class AdminLogin extends AppCompatActivity {

    private CardView student;
    private CardView instructor;
    private CardView tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        student = findViewById(R.id.admin_student);
        instructor = findViewById(R.id.admin_instructor);
        tools = findViewById(R.id.admin_tools);

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLogin.this, AdminStudentPanel.class));

            }
        });

        instructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLogin.this, AdminInstructorPanel.class));
            }
        });

        tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLogin.this, AdminToolPanel.class));
            }
        });
    }


}
