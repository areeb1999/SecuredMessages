package com.areeb.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity { //Declare variable


    TextView regiButton;
    TextInputEditText login_Email,login_Password;
    Button LoginButton;
    FirebaseAuth auth;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; //Email pattern must match string


    @Override
    protected void onCreate(Bundle savedInstanceState) { //Linking variables to XML files
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        auth = FirebaseAuth.getInstance();

        regiButton = findViewById(R.id.TextSignUp);
        LoginButton = findViewById(R.id.ButtonLogin);

        login_Email = findViewById(R.id.EditEmail);
        login_Password = findViewById(R.id.EditPassword);


        LoginButton.setOnClickListener(new View.OnClickListener() {  // Method getting text from log in fields and checking if valid
            @Override
            public void onClick(View view) {

                String email = login_Email.getText().toString(); // Get string from login email
                String password = login_Password.getText().toString();

                if (TextUtils.isEmpty(email)) // If fields are empty
                {
                    login_Email.setError("Email Required");
                }else if (TextUtils.isEmpty(email))
                {
                    login_Password.setError("Password Required");
                }else if (!email.matches(emailPattern))
                {
                    login_Email.setError("Email Required");
                    Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_SHORT).show();
                }else if (password.length()<=6)
                {
                    login_Password.setError("Password Required greater than 6 characters"); // Error if password is too short
                }else {
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                String uid= auth.getUid();
                                SharedPreferences.Editor editor = getSharedPreferences("Message Folder", MODE_PRIVATE).edit(); // Saves ID in temporary storage
                                editor.putString("uid", uid);
                                startActivity(new Intent(LoginScreen.this,HomeScreen.class));

                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Error Login!",Toast.LENGTH_SHORT).show(); // Invalid Credentials
                            }
                        }
                    });

                }



            }
        });







        regiButton.setOnClickListener(new View.OnClickListener() { // Go to sign up screen
            @Override
            public void onClick(View view) {

                Intent i = new Intent(LoginScreen.this, SignUpScreen.class);
                startActivity(i);
                finish();
            }
        });

    }

}