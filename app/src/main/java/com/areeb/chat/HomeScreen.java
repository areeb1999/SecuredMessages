package com.areeb.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {
    FirebaseAuth auth;

    RecyclerView MainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<UserModel> userArrayList;
    ImageView img_logout , img_setting;

    TextView TxtNobtn ,TxtYesbtn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



        if (auth.getCurrentUser() == null) //If there is no active user send to Sign Up class
        {
            startActivity(new Intent(HomeScreen.this,SignUpScreen.class));
        }




        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading....");
        progressDialog.show();



        img_logout = findViewById(R.id.img_logout);
        img_setting = findViewById(R.id.settings);


        userArrayList = new ArrayList<>();

        DatabaseReference reference = database.getReference().child("user"); // Reference for array list identified

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) // For loop to add all users
                {

                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    userArrayList.add(userModel);
                    progressDialog.dismiss();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(),"Error in getting data.",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });


        MainUserRecyclerView = findViewById(R.id.MainUserRecyclerView); // Recycler View in XML that will display all users
        MainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter= new UserAdapter(HomeScreen.this,userArrayList);
        MainUserRecyclerView.setAdapter(adapter);

        img_setting.setOnClickListener(new View.OnClickListener() { // On click settings button go to settings page
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),Settings.class));
            }
        });




        img_logout.setOnClickListener(new View.OnClickListener() { // Linked to log out function on top right
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(HomeScreen.this,R.style.Dialog);
                dialog.setContentView(R.layout.dialog_layout_logout);

                TxtNobtn = dialog.findViewById(R.id.TxtNoBtn);
                TxtYesbtn = dialog.findViewById(R.id.TxtYesBtn);

                TxtYesbtn.setOnClickListener(new View.OnClickListener() { // Log out user and go back to log in page
                    @Override
                    public void onClick(View view) {

                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(),LoginScreen.class));

                    }
                });

                TxtNobtn.setOnClickListener(new View.OnClickListener() {  // Go back to home page with list of users
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });



                dialog.show();
            }
        });






    }
}