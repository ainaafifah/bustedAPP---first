package com.example.bustedapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bustedapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private EditText mFName, mLName, mEmail, mPhone, mPassword;
    private Button mGo, mLogin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFName = findViewById(R.id.fName);
        mLName = findViewById(R.id.lastName);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.phone);
        mPassword = findViewById(R.id.password);
        mGo = findViewById(R.id.btnSignup);
        mLogin = findViewById(R.id.btnLogin);

        firebaseAuth = FirebaseAuth.getInstance();


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        mGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String first_name = "", last_name = "", email = "", phone = "", pwd = "";
    private void validateData() {
        first_name = mFName.getText().toString().trim();
        last_name = mLName.getText().toString().trim();
        email = mEmail.getText().toString().trim();
        phone = mPhone.getText().toString().trim();
        pwd = mPassword.getText().toString().trim();

        if (TextUtils.isEmpty(first_name)){
            Toast.makeText(this, "Enter your first name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(last_name)){
            Toast.makeText(this, "Enter your last name...", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email pattern...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Enter your phone number...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pwd)){
            Toast.makeText(this, "Enter your password...", Toast.LENGTH_SHORT).show();
        }
        else{
            createUserAccount();
        }
    }

    private void createUserAccount() {

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email,pwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Register.this, "Success to Register!", Toast.LENGTH_SHORT).show();
                        //add user in realtime database
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        Toast.makeText(Register.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {

        //timestamp
        long timestamp = System.currentTimeMillis();

        //get current uid
        String uid = firebaseAuth.getUid();

        //set data tp ass in realtime db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("first_name", first_name);
        hashMap.put("last_name", last_name);
        hashMap.put("email", email);
        hashMap.put("phone", phone);
        hashMap.put("password", pwd);
        hashMap.put("profileImage", "");
        hashMap.put("timestamp", timestamp);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //set data to db
        databaseReference= database.getReference("Users");
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //add data on db
                        Toast.makeText(Register.this, "Account created......", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this,MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //data failed to add on db
                        Toast.makeText(Register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }


}