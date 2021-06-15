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

package com.piyush004.friendslocapp.Home.Fragments.Chat.Chatting;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.piyush004.friendslocapp.R;

public class ChattingHolder extends RecyclerView.ViewHolder {

    public TextView chatMessage, chatTime;
    public ImageView seen;

    public ChattingHolder(@NonNull View itemView) {
        super(itemView);

        this.chatMessage = itemView.findViewById(R.id.contactName);
        this.chatTime = itemView.findViewById(R.id.contactNumber);

        this.seen = itemView.findViewById(R.id.seen);


    }
}
