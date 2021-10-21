package com.example.seg2105;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.email);
        username = findViewById(R.id.user_name);
        password = findViewById(R.id.password);
        instructor = findViewById(R.id.instructor_button);
        gymMember = findViewById(R.id.gymMember_button);
        signUp = findViewById(R.id.signup_button);

        instructor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                role = "instructor";
            }
        });

        gymMember.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                role = "member";
            }
        });

        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addUser(username.getText().toString(), email.getText().toString(), password.getText().toString(), role);
            }
        });
    }

    EditText username, email, password;
    Button instructor, gymMember, signUp;

    public void createDemoUser(){
        addUser("adam3", "adam3@databending.ca", "admin123", "instructor");
    }

    public void addUser(String user_name, String user_email, String user_password, String user_role){
        DocumentReference finalUser_role = null;
        switch(user_role){
            case "instructor":
                finalUser_role = db.document("roles/3xQrDfZhc7Kdjr9TTueY");
                break;
            case "member":
                finalUser_role = db.document("/roles/KhXfzrrVCK2dJtSoQeWX");
                break;
        }
        DocumentReference finalUser_role1 = finalUser_role;
        mAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                System.out.println("Created user successfully");
                String user_id = task.getResult().getUser().getUid();
                Map<String, Object> data1 = new HashMap<>();
                data1.put("user_id", user_id);
                data1.put("username", user_name);
                data1.put("role", finalUser_role1);
                db.collection("users").add(data1);
            }
        });
    }
}