/*
 * <!--
 *   ~ /*******************************************************
 *   ~  * Copyright (C) 2021-2031 {Piyush Wani and  Mayur Sapkale} <{piyushwani04@gmail.com}>
 *   ~  *
 *   ~  * This file is part of {FriendLocatorApp}.
 *   ~  *
 *   ~  * {FriendLocatorApp} can not be copied and/or distributed without the express
 *   ~  * permission of {Piyush Wani and  Mayur Sapkale}
 *   ~  ******************************************************
 *   -->
 */

package com.piyush004.friendslocapp.Home.Fragments.Contact;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.piyush004.friendslocapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactHolder extends RecyclerView.ViewHolder {

    public TextView Name, MobileNo;
    public CircleImageView circleImageView;
    public Button inviteButton;

    public ContactHolder(@NonNull View itemView) {
        super(itemView);

        Name = itemView.findViewById(R.id.contactName);
        MobileNo = itemView.findViewById(R.id.contactNumber);
        circleImageView = itemView.findViewById(R.id.image);
        inviteButton = itemView.findViewById(R.id.invite_button);
    }

}
