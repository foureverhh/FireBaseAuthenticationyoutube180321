package com.nackademin.foureverhh.firebaseauthenticationyoutube180321;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.needSignUp).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent signUp = new Intent(this,SignUpActivity.class);
        startActivity(signUp);
    }
}
