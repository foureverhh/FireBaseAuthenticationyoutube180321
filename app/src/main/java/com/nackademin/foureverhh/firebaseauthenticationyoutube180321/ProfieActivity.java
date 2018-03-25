package com.nackademin.foureverhh.firebaseauthenticationyoutube180321;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfieActivity extends AppCompatActivity implements View.OnClickListener{

    EditText editText;
    ImageView imageView;
    TextView textView;
    Button btn_save;
    private  static final int CHOOSE_IMAGE = 101;
    ProgressBar progressBar;
    //For user to choose profile
    Uri uriProfileImage;
    String profileImageUrl;

    FirebaseAuth mAuth;

    TextView textViewVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profie);

        editText = findViewById(R.id.editTextDisplayName);
        imageView = findViewById(R.id.portrait);
        textView = findViewById(R.id.setPortrait);
        btn_save = findViewById(R.id.buttonSave);
        progressBar = findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        textViewVerified = findViewById(R.id.textVerified);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView.setOnClickListener(this);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });
        //To load the information of users
        loadUserInformation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            //If the user has not registrated,go back to the main activity
            finish();
            startActivity(new Intent(ProfieActivity.this,MainActivity.class));
        }
    }

    private void loadUserInformation(){
        //make it final so that on row 109 the same user can invoke sendEmailVerification()
        final FirebaseUser user = mAuth.getCurrentUser();
        //If user exist
        if(user != null) {
            if (user.getPhotoUrl() != null) {
               // String photoUri = user.getPhotoUrl().toString();
                // Use glide to load the image to profile
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);

            }
            if (user.getDisplayName() != null) {
                //String displayName = user.getDisplayName();
                editText.setText(user.getDisplayName());
            }

        }

        //To check whether a user is verified
        if(user.isEmailVerified()){  //isEmailVerified if user is verified,then return true
            textViewVerified.setText("Email verified");
        }else{
            textViewVerified.setText("Email not verified(Click to verify)");
            textViewVerified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ProfieActivity.this,"Verification email sent",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

    }

    private void saveUserInformation(){
        String displayName = editText.getText().toString();
        if(displayName.isEmpty()){
            editText.setError("Name is required");
            editText.requestFocus();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null && profileImageUrl != null){
            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ProfieActivity.this,"Prifile Updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }



    @Override
    public void onClick(View v) {
        showImageChooser();
    }

    public void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*"); //Means it is going to handle the type of image
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImageToFirebaseStorage(){
        //Set up a file path
        final StorageReference profileImageRef
                = FirebaseStorage.getInstance().getReference("profile/"+System.currentTimeMillis()+".jpg");

        if(uriProfileImage != null){
            //To show the progressbar
            progressBar.setVisibility(View.VISIBLE);

            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    //uriProfileImage = taskSnapshot.getDownloadUrl();//Can upload picture to firebase as well
                    profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProfieActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //To modify the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut(); //To invoke the sign out
                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
