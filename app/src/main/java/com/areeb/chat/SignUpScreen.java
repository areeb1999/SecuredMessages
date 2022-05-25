package com.areeb.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpScreen extends AppCompatActivity { //Declaring Variables to be used in this class

    Button signUpbtn;
    TextView signInbtn;
    CircleImageView profilepic;
    TextInputEditText EditUserName,EditEmail,EditPhone,EditPassword,EditConfirmPassword;
    Uri imageUri;
    String imageURI;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Linking declared variables to XML Files
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        signUpbtn = findViewById(R.id.ButtonSignUp);
        signInbtn = findViewById(R.id.TextSignIn);
        profilepic = findViewById(R.id.ProfilePic);
        EditUserName = findViewById(R.id.EditUserName);
        EditEmail = findViewById(R.id.EditEmail);
        EditPhone = findViewById(R.id.EditPassword);
        EditPassword = findViewById(R.id.EditPassword);
        EditConfirmPassword = findViewById(R.id.EditConfirmPassword);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading....");
        progressDialog.setCancelable(false);




        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = EditUserName.getText().toString().trim();
                String email = EditEmail.getText().toString().trim();
                String password = EditPassword.getText().toString().trim();
                String cpassword = EditConfirmPassword.getText().toString().trim();
                String Status = "Hey! I am New Here.";

                progressDialog.show();

                if (TextUtils.isEmpty(name))
                {
                    EditUserName.setError("Fill the Field"); //Error if text is empty
                    progressDialog.dismiss();
                }else if(TextUtils.isEmpty(email))
                {
                    EditEmail.setError("Fill the Field"); //Error if text is empty
                    progressDialog.dismiss();
                }else if (TextUtils.isEmpty(password))
                {
                    EditPassword.setError("Fill the Field"); //Error if text is empty
                    progressDialog.dismiss();
                }else if (TextUtils.isEmpty(cpassword))
                {
                    EditConfirmPassword.setError("Fill the Field"); //Error if password and confirm password are not the same
                    progressDialog.dismiss();
                }else if (!password.equals(cpassword))
                {
                    Toast.makeText(getApplicationContext(),"Password do not Match",Toast.LENGTH_SHORT).show();
                    EditPassword.setError("Password do not Match");
                    EditConfirmPassword.setError("Password do not Match");
                    progressDialog.dismiss();
                }else if (password.length()<6)
                {
                    EditPassword.setError(password);
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Please Enter More than 6 Characters",Toast.LENGTH_SHORT).show();
                }
                else {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() { //Method to add user to firebase database
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) { //User is added if successful
                                Toast.makeText(getApplicationContext(), "User Created Successfully", Toast.LENGTH_SHORT).show();


                                DatabaseReference reference = database.getReference().child("user").child(auth.getUid()); // Under the user child the id will be added which will have further details
                                StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());

                                if (imageUri != null) { // If image is provided then file is added to firebase
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                            if (task.isSuccessful()) { // Method to add custom profile image
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI = uri.toString();
                                                        UserModel userModel = new UserModel(auth.getUid(),name,email,imageURI,Status); // User data is added to a user model
                                                        reference.setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() { // All sub values are added to the reference identified in user model
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) // Redirect to home class once successful
                                                                {
                                                                    startActivity(new Intent(getApplicationContext(), HomeScreen.class));

                                                                }
                                                                else {
                                                                    Toast.makeText(getApplicationContext(),"Error in Creating User",Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });


                                                    }
                                                });
                                            }

                                        }
                                    });
                                }else { // Same method with default image

                                    String Status = "Hey! I am New Here.";
                                    imageURI = "https://firebasestorage.googleapis.com/v0/b/messages-a8e5f.appspot.com/o/upload%2Fimg.png?alt=media&token=84221b2a-8352-4343-a95f-2f896d5ff2dd";
                                    UserModel userModel = new UserModel(auth.getUid(),name,email,imageURI,Status);
                                    reference.setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) // Send to home screen if successful
                                            {
                                                startActivity(new Intent(getApplicationContext(), HomeScreen.class));

                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(),"Error in Creating User",Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }



                                progressDialog.dismiss();

                            } else {

                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Something went wrong or may be this user already Exists", Toast.LENGTH_SHORT).show();


                            }


                        }
                    });

                }
            }
        });



        profilepic.setOnClickListener(new View.OnClickListener() { // To select a picture
            @Override
            public void onClick(View view) {
                progressDialog.show();

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });





        signInbtn.setOnClickListener(new View.OnClickListener() { // Sign in button to go to log in class
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginScreen.class));
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // Dismissing progress dialog if taking too long
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==10)
        {
            progressDialog.dismiss();
            if (data!=null)
            {
                imageUri = data.getData();
                profilepic.setImageURI(imageUri);
            }
        }
        progressDialog.dismiss();
    }





}