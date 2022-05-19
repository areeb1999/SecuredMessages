package com.areeb.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreen extends AppCompatActivity {

    String ReceiverName;
    String ReciverImage;
    String ReceiverUid;
    String SenderUid;
    String SenderRoom;
    String receiverRoom;

    public static String sImage;
    public static String rImage;

    // Loading Profile Image, Contact Name, Send Button and Text Field

    CircleImageView ProfileImage;
    TextView reciverName;

    CardView Sendbtn;
    EditText editMessage;

    // Database Variables
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    // Linking chat from previous adapter
    RecyclerView securemessageAdapter;
    ArrayList<SecureMessagesModel> secureMessagesModelArrayList;

    SecureMessageAdapter securemessagesAdapter;

    ImageView Imgbackbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Name Received from Previous Activity of list
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        ReceiverName =getIntent().getStringExtra("name");
        ReciverImage=getIntent().getStringExtra("ReciverImage");
        ReceiverUid =getIntent().getStringExtra("uid");
        secureMessagesModelArrayList = new ArrayList<>();

        SenderUid = firebaseAuth.getUid();

        SenderRoom = SenderUid+ ReceiverUid;
        receiverRoom = ReceiverUid +SenderUid;

        // Initialisation of variables to be used in chat from XML

        ProfileImage = findViewById(R.id.ProfileImage);
        reciverName = findViewById(R.id.reciverName);
        Sendbtn = findViewById(R.id.Sendbtn);
        editMessage = findViewById(R.id.editMessage);
        securemessageAdapter = findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        securemessageAdapter.setLayoutManager(linearLayoutManager);
        securemessagesAdapter = new SecureMessageAdapter(ChatScreen.this, secureMessagesModelArrayList);
        securemessageAdapter.setAdapter(securemessagesAdapter);
        Imgbackbtn = findViewById(R.id.Imgbackbtn);

        // Picasso to load recipients image from firebase into profile image
        Picasso.get().load(ReciverImage).into(ProfileImage);
        reciverName.setText(""+ ReceiverName);
        reciverName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReceiverDetailScreen.class);
                intent.putExtra("recieverId", ReceiverUid);
                startActivity(intent);
            }
        });
        

        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatreference = database.getReference().child("chats").child(SenderRoom).child("messages");

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                secureMessagesModelArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    SecureMessagesModel secureMessagesModel = dataSnapshot.getValue(SecureMessagesModel.class);
                    String msg= decryptString(secureMessagesModel.message);
                    secureMessagesModel.message=msg;
                    secureMessagesModelArrayList.add(secureMessagesModel);
                }
                securemessagesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                sImage = snapshot.child("imageUri").getValue().toString();
                rImage=ReciverImage;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Sendbtn.setOnClickListener(new View.OnClickListener() { // Method for when send is clicked
            @Override
            public void onClick(View view) {

                String message = editMessage.getText().toString().trim(); // String to store message when text added to field

                if (message.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please type a message",Toast.LENGTH_SHORT).show();
                    return;
                }

                editMessage.setText("");

                Date date = new Date();

                String encryptionString = encryptString(message); // String to store encrypted message


                SecureMessagesModel secureMessagesModel = new SecureMessagesModel(encryptionString, SenderUid, date.getTime()); // All Strings Sent to firebase

                database = FirebaseDatabase.getInstance(); // Stored under chats and messages
                database.getReference().child("chats")
                        .child(SenderRoom).child("messages").push().setValue(secureMessagesModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                database.getReference().child("chats")
                                        .child(receiverRoom).child("messages").push().setValue(secureMessagesModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {


                                    }
                                });

                            }
                        });

            }
        });

        Imgbackbtn.setOnClickListener(new View.OnClickListener() { // Button to go back to list of chats
            @Override
            public void onClick(View view) {

                finish();

            }
        });





    }


    private String encryptString(String originalStr){ // Original String
        String response = "";

        try {
            // adding a string of 18 characters to be used as key
            byte[] key = "1234567890ABCDEFGH".getBytes("UTF-8"); // Key used for encryption

            MessageDigest sha = MessageDigest.getInstance("SHA-1");  // Instance of Secure Hash Algorithm 1 is applied
            key = sha.digest(key);

            // get a 16 character key from 18 characters
            key = Arrays.copyOf(key, 16);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES"); // AES Functions from developer.android.com
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] input = originalStr.getBytes("UTF-8");

            byte[] cipherTxt = cipher.doFinal(input);

            response = Base64.encodeToString(cipherTxt, Base64.DEFAULT); // Grabs encrypted message from sha function

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String decryptString(String encryptedString){ // Decrypt function
        String response = "";

        try {
            // string of 18 characters
            byte[] key = "1234567890ABCDEFGH".getBytes("UTF-8");

            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);

            // get 16 character from 18 characters
            key = Arrays.copyOf(key, 16);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES"); // Set to Decrypt mode and then sent back to user in plaintext
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] input = encryptedString.getBytes("UTF-8");

            byte[] cipherTxt = cipher.doFinal(Base64.decode(input, Base64.DEFAULT));

            response = new String(cipherTxt);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return response;
    }
}
