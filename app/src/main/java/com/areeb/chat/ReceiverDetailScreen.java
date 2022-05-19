package com.areeb.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceiverDetailScreen extends AppCompatActivity { // This class is for the viewing profile picture

    FirebaseDatabase database;
    FirebaseStorage storage;

    TextView name, status;
    CircleImageView image;
    ImageView imgBack;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever_detail_screen);
        // getting values from xml

        name = findViewById(R.id.Setting_Name);
        status = findViewById(R.id.txtStatus);
        image = findViewById(R.id.ProfileImage);
        imgBack = findViewById(R.id.img_backbtn);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        String uid = getIntent().getStringExtra("recieverId");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        DatabaseReference reference = database.getReference().child("user").child(uid);
        StorageReference storageReference = storage.getReference().child("upload").child(uid);

        reference.addValueEventListener(new ValueEventListener() { // Values taken from firebase database
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("name").getValue().toString());
                status.setText(snapshot.child("status").getValue().toString());
                Picasso.get().load(snapshot.child("imageUri").getValue().toString()).into(image);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
