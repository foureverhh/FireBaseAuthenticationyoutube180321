package com.nackademin.foureverhh.firebaseauthenticationyoutube180321;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

        imageView.setOnClickListener(this);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });
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

    private void uploadImageToFirebaseStorage(){

        final StorageReference profileImageRef
                = FirebaseStorage.getInstance().getReference("profile/"+System.currentTimeMillis()+".jpg");

        if(uriProfileImage != null){
            //To show the progressbar
            progressBar.setVisibility(View.VISIBLE);

            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
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
}
