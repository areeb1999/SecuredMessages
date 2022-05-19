package com.areeb.chat;

import static com.areeb.chat.ChatScreen.rImage;
import static com.areeb.chat.ChatScreen.sImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SecureMessageAdapter extends RecyclerView.Adapter {  // Class represents a hard coded adapter to view users

    Context context;
    ArrayList<SecureMessagesModel> securemessagesModelArrayListSecure;
    int ITM_SEND=1;
    int ITM_RECIVE=2;

    public SecureMessageAdapter(Context context, ArrayList<SecureMessagesModel> secureMessagesModelArrayList) {
        this.context = context;
        this.securemessagesModelArrayListSecure = secureMessagesModelArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //View Holder added manually for list of users

        if (viewType==ITM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item,parent,false);
            return  new SenderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout_item,parent,false);
            return  new ReciverViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) { // Displays messages within chat

        SecureMessagesModel secureMessagesModel = securemessagesModelArrayListSecure.get(position);
        if (holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;

            viewHolder.txtmessage.setText(secureMessagesModel.getMessage());
            Picasso.get().load(sImage).into(viewHolder.circleImageView);
        }
        else {

            ReciverViewHolder viewHolder = (ReciverViewHolder) holder;

            viewHolder.txtmessage.setText(secureMessagesModel.getMessage());
            Picasso.get().load(rImage).into(viewHolder.circleImageView);

        }

    }

    @Override
    public int getItemCount() {
        return securemessagesModelArrayListSecure.size();
    }

    @Override
    public int getItemViewType(int position) {

        SecureMessagesModel secureMessagesModel = securemessagesModelArrayListSecure.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(secureMessagesModel.getSenderid()))
        {
            return ITM_SEND;
        }
        else {
            return ITM_RECIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder { // for sent messages

        CircleImageView circleImageView;
        TextView txtmessage;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            // message and profile pic added
            circleImageView = itemView.findViewById(R.id.ProfileImage);
            txtmessage = itemView.findViewById(R.id.SenderMessages);
        }
        }
    }

class ReciverViewHolder extends RecyclerView.ViewHolder { // for received messages

    CircleImageView circleImageView;
    TextView txtmessage;

    public ReciverViewHolder(@NonNull View itemView) {
        super(itemView);
            // message and profile pic added
        circleImageView = itemView.findViewById(R.id.ProfileImage);
        txtmessage = itemView.findViewById(R.id.ReciverMessages);
    }
}

