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

public class ChattingModel {

    private String Sender;
    private String Receiver;
    private String Message;
    private String Date;
    private String Time;
    private String ProductImg;
    private String ProductName;
    private String ChatID;
    private boolean isSeen;


    public ChattingModel() {
    }

    public ChattingModel(String sender, String receiver, String message, String date, String time, String proid, String proname, String chatid, boolean isseen) {
        this.Sender = sender;
        this.Receiver = receiver;
        this.Message = message;
        this.Date = date;
        this.Time = time;
        ProductImg = proid;
        ProductName = proname;
        ChatID = chatid;
        isSeen = isseen;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getChatID() {
        return ChatID;
    }

    public void setChatID(String chatID) {
        ChatID = chatID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductImg() {
        return ProductImg;
    }

    public void setProductImg(String productImg) {
        ProductImg = productImg;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        this.Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        this.Receiver = receiver;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

}
