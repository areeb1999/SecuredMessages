package com.areeb.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity { // Settings screen to change status, profile picture and name

    ImageView img_backbtn;
    CircleImageView savebtn;
    CircleImageView profileImg;
    TextView Status , Setting_Name;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri Selected_Image_Uri;
    String email;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Initialising variables
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        img_backbtn = findViewById(R.id.img_backbtn);
        savebtn = findViewById(R.id.savebtn_img);
        profileImg = findViewById(R.id.ProfileImage);
        Status = findViewById(R.id.txtStatus);
        Setting_Name = findViewById(R.id.Setting_Name);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading....");
        progressDialog.show();



        DatabaseReference reference =  database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

        reference.addValueEventListener(new ValueEventListener() { // Loading values from firebase
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                email = snapshot.child("email").getValue().toString();
                String image = snapshot.child("imageUri").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();

                Setting_Name.setText(name);
                Status.setText(status);
                Picasso.get().load(image).into(profileImg);

                progressDialog.dismiss();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                progressDialog.dismiss();
            }
        });






        img_backbtn.setOnClickListener(new View.OnClickListener() { // back to chat screens
            @Override
            public void onClick(View view) {
                progressDialog.show();
                finish();
                progressDialog.dismiss();
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() { // change picture
            @Override
            public void onClick(View view) {
                progressDialog.show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);

            }
        });





        savebtn.setOnClickListener(new View.OnClickListener() { // save changes
            @Override
            public void onClick(View view) {

                progressDialog.show(); // Show loading dialog


                String name =Setting_Name.getText().toString(); // Getting text for new name
                String status = Status.getText().toString(); // Getting Text for new status

                if (Selected_Image_Uri !=null) // Getting new profile picture
                {

                    storageReference.putFile(Selected_Image_Uri) // Add selected profile picture
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String finalImageUri =uri.toString();
                                            UserModel userModel = new UserModel(auth.getUid(),name,email,finalImageUri,status);

                                            reference.setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(getApplicationContext(),"Info Updated",Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        progressDialog.dismiss();
                                                    }
                                                    else {

                                                        Toast.makeText(getApplicationContext(),"Something Went Wrong",Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    }

                                                }
                                            });


                                        }
                                    });



                                }
                            });


                }
                else { // If no image is selected

                    progressDialog.show();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String finalImageUri =uri.toString();
                            UserModel userModel = new UserModel(auth.getUid(),name,email,finalImageUri,status);

                            reference.setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getApplicationContext(),"Info Updated",Toast.LENGTH_SHORT).show();
                                        finish();
                                        progressDialog.dismiss();
                                    }
                                    else {

                                        Toast.makeText(getApplicationContext(),"Something Went Wrong",Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();
                                    }

                                }
                            });


                        }
                    });

                }



            }
        });









    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10)
        {
            if (data!=null)
            {
                Selected_Image_Uri = data.getData();
                profileImg.setImageURI(Selected_Image_Uri); // Setting new image to XML
                progressDialog.dismiss(); // Dismiss loading dialog
            }
        }
        progressDialog.dismiss();
    }

}