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

public class ChatModel {

    private String ChatId;
    private String MessageCount;
    private Long TimeStamp;

    public ChatModel() {
    }

    public ChatModel(String chatId, String messageCount, Long timeStamp) {
        ChatId = chatId;
        MessageCount = messageCount;
        TimeStamp = timeStamp;
    }

    public String getMessageCount() {
        return MessageCount;
    }

    public void setMessageCount(String messageCount) {
        MessageCount = messageCount;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getChatId() {
        return ChatId;
    }

    public void setChatId(String chatId) {
        ChatId = chatId;
    }


}
