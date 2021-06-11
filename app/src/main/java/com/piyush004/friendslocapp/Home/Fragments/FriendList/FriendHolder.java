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

package com.piyush004.friendslocapp.Home.Fragments.FriendList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.piyush004.friendslocapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendHolder extends RecyclerView.ViewHolder {

    public TextView FriendName, FriendMobileNo;
    public CircleImageView FriendImageView;
    public ImageView RequestDelete;

    public FriendHolder(@NonNull View itemView) {
        super(itemView);

        FriendName = itemView.findViewById(R.id.FriendName);
        FriendMobileNo = itemView.findViewById(R.id.FriendNumber);
        FriendImageView = itemView.findViewById(R.id.Friend_image);
        RequestDelete = itemView.findViewById(R.id.request_delete_button);

    }
}
