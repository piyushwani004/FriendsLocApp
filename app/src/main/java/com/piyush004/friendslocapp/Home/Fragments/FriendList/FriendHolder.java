package com.piyush004.friendslocapp.Home.Fragments.FriendList;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.piyush004.friendslocapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendHolder extends RecyclerView.ViewHolder {

    public TextView Name, MobileNo;
    public CircleImageView circleImageView;
    public Button AcceptButton, DeleteButton;

    public FriendHolder(@NonNull View itemView) {
        super(itemView);

        Name = itemView.findViewById(R.id.friendName);
        MobileNo = itemView.findViewById(R.id.friendNumber);
        circleImageView = itemView.findViewById(R.id.friendImage);
        AcceptButton = itemView.findViewById(R.id.accept_button);
        DeleteButton = itemView.findViewById(R.id.delete_button);

    }

}
