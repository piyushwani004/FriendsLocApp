package com.piyush004.friendslocapp.Home.Fragments.FriendList;

public class FriendModel {

    private String Id;
    private String Date;
    private String ReqType;
    private String Status;

    public FriendModel() {
    }

    public FriendModel(String id, String date, String reqType, String status) {
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
