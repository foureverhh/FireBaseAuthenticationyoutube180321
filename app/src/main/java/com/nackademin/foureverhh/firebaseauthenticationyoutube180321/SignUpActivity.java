package com.nackademin.foureverhh.firebaseauthenticationyoutube180321;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    EditText usernameSignUp,emailSignUp,passwordSignUp;
    Button btnSignUp;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getIntent();
        usernameSignUp = findViewById(R.id.userNameSignUp);
        emailSignUp = findViewById(R.id.signUpEmail);
        passwordSignUp = findViewById(R.id.signUpPassword);
        btnSignUp = findViewById(R.id.btn_signUp);

        btnSignUp.setOnClickListener(this);
        //Initialise a firebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
    }

    public void registerUser(){

        String email = emailSignUp.getText().toString().trim();
        String password = passwordSignUp.getText().toString().trim();

        if(email.isEmpty()){
            emailSignUp.setError("Email is required");
            emailSignUp.requestFocus();
            return;
        }
        //To check whether email is a valid email address
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailSignUp.setError("Input a valid email,please!");
            emailSignUp.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordSignUp.setError("Password is required");
            passwordSignUp.requestFocus();
            return;
        }
        if(password.length()<=6){
            passwordSignUp.setError("Minimum length of password must be 6");
            passwordSignUp.requestFocus();
        }

        //Begin to record the sign up information
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"User Registered Successful",Toast.LENGTH_SHORT).show();
                }else {
                    //Toast.makeText(getApplicationContext(),"Some errors occurred",Toast.LENGTH_SHORT).show();
                    //To check whether the email address is already exist
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(getApplicationContext(),"You have registered",Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        registerUser();
    }
}
