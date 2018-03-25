package com.nackademin.foureverhh.firebaseauthenticationyoutube180321;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView textViewToSignUp;
    Button btnSignIn;
    EditText userEmailLogIn, userPasswordLogIn;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewToSignUp =findViewById(R.id.needSignUp);
        textViewToSignUp.setOnClickListener(this);

        btnSignIn = findViewById(R.id.btn_login);
        btnSignIn.setOnClickListener(this);

        userEmailLogIn = findViewById(R.id.userEmailLogin);
        userPasswordLogIn = findViewById(R.id.password);
        //Initialise a mAuth
        mAuth = FirebaseAuth.getInstance();
    }

    private void userLogin(){

        String emailSignIn= userEmailLogIn.getText().toString().trim();
        String passwordSignIn = userPasswordLogIn.getText().toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(emailSignIn).matches()){
            userEmailLogIn.setError("Input a valid email");
            userEmailLogIn.requestFocus();
        }
        if(passwordSignIn.length() == 0){
            userPasswordLogIn.setError("Password is required");
            userPasswordLogIn.requestFocus();
        }
        //To check whether email and password matches
        mAuth.signInWithEmailAndPassword(emailSignIn,passwordSignIn).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();//So that man can not go back to the previous activity
                    Intent profile = new Intent(MainActivity.this,ProfieActivity.class);
                    //To make the profile activity on top so that the user will not go back to login activity
                    profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(profile);

                }else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.needSignUp:
                Intent signUp = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(signUp);
                break;
            case R.id.btn_login:
                userLogin();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //If the user has already logged in
        if(mAuth.getCurrentUser() != null){
            //Kill the activity
            finish();
            //Jump to profile activity direct
            startActivity(new Intent(MainActivity.this,ProfieActivity.class));
        }
    }
}
