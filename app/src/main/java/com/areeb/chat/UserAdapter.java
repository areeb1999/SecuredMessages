package com.areeb.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context homeScreen;
    ArrayList<UserModel> usersArrayList;


    public UserAdapter(HomeScreen homeScreen, ArrayList<UserModel> userArrayList) { // Class represents a an array list used to get all users
        this.homeScreen=homeScreen;
        this.usersArrayList=userArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(homeScreen).inflate(R.layout.item_user_row,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {  // Displays the data at a specified position

        UserModel userModel = usersArrayList.get(position);


        if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userModel.getUid()))
        {
            holder.Namee.setText(userModel.Name);
            holder.Statuss.setText(userModel.status);
            Picasso.get().load(userModel.imageUri).into(holder.User_imagee);

        }

        else
        {
            holder.items.setVisibility(View.GONE);
        }





        holder.itemView.setOnClickListener(new View.OnClickListener() { // Once anywhere on the list is clicked it will pass the users name, profile pic and user id
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeScreen, ChatScreen.class);
                intent.putExtra("name",userModel.getName());
                intent.putExtra("ReciverImage",userModel.getImageUri());
                intent.putExtra("uid",userModel.getUid());
                homeScreen.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    } // Gets count from array list
    class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView User_imagee;
        TextView Namee;
        TextView Statuss;
        LinearLayout items;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            User_imagee = itemView.findViewById(R.id.User_image);
            Namee = itemView.findViewById(R.id.user_Name);
            Statuss = itemView.findViewById(R.id.user_status);
            items = itemView.findViewById(R.id.items);
        }
    }
}
