package com.piyush004.friendslocapp.Home.Fragments.Request;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.piyush004.friendslocapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestHolder extends RecyclerView.ViewHolder {

    public TextView Name, MobileNo;
    public CircleImageView circleImageView;
    public MaterialButton AcceptButton, DeleteButton;
    public MaterialButton StatusButton;
    public RelativeLayout ButtonLayout;

    public FriendRequestHolder(@NonNull View itemView) {
        super(itemView);

        Name = itemView.findViewById(R.id.friendName);
        MobileNo = itemView.findViewById(R.id.friendNumber);
        circleImageView = itemView.findViewById(R.id.friendImage);
        AcceptButton = itemView.findViewById(R.id.accept_button);
        DeleteButton = itemView.findViewById(R.id.delete_button);

        StatusButton = itemView.findViewById(R.id.StatusButton);
        ButtonLayout = itemView.findViewById(R.id.buttonsLayout);

    }

}
