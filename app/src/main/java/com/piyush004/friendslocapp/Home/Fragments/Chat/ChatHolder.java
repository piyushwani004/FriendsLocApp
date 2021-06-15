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

package com.piyush004.friendslocapp.Home.Fragments.Chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.piyush004.friendslocapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatHolder extends RecyclerView.ViewHolder {

    public CircleImageView imageVieChat;
    public TextView title_card, new_message_card;
    public ImageView status, new_message_noti;

    public ChatHolder(@NonNull View itemView) {
        super(itemView);

        imageVieChat = itemView.findViewById(R.id.imageVieChat);

        title_card = itemView.findViewById(R.id.title_card);
        new_message_card = itemView.findViewById(R.id.new_message_text);

        status = itemView.findViewById(R.id.status);
        new_message_noti = itemView.findViewById(R.id.new_message_noti);

    }
}
