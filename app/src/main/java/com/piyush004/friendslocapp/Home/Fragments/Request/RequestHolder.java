package com.piyush004.friendslocapp.Home.Fragments.Request;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.piyush004.friendslocapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestHolder extends RecyclerView.ViewHolder {

    public TextView Name, MobileNo;
    public CircleImageView circleImageView;
    public Button inviteButton;

    public RequestHolder(@NonNull View itemView) {
        super(itemView);

        Name = itemView.findViewById(R.id.contactName);
        MobileNo = itemView.findViewById(R.id.contactNumber);
        circleImageView = itemView.findViewById(R.id.image);
        inviteButton = itemView.findViewById(R.id.invite_button);

    }
}
