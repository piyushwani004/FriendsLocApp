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

package com.piyush004.friendslocapp.Home.Fragments.Request;

public class FriendRequestModel {

    private String Id;
    private String Date;
    private String ReqType;
    private String Status;

    public FriendRequestModel() {
    }

    public FriendRequestModel(String id, String date, String reqType, String status) {
        Id = id;
        Date = date;
        ReqType = reqType;
        Status = status;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getReqType() {
        return ReqType;
    }

    public void setReqType(String reqType) {
        ReqType = reqType;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Override
    public String toString() {
        return "FriendModel{" +
                "Id='" + Id + '\'' +
                ", Date='" + Date + '\'' +
                ", ReqType='" + ReqType + '\'' +
                ", Status='" + Status + '\'' +
                '}';
    }

}
