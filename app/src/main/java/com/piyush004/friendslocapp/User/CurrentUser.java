package com.piyush004.friendslocapp.User;

public class CurrentUser {

    private String ID;
    private String Name;
    private String Mobile;
    private String PhotoURL;

    public CurrentUser() {
    }

    public CurrentUser(String ID, String name, String mobile, String photoURL) {
        this.ID = ID;
        Name = name;
        Mobile = mobile;
        PhotoURL = photoURL;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPhotoURL() {
        return PhotoURL;
    }

    public void setPhotoURL(String photoURL) {
        PhotoURL = photoURL;
    }

    @Override
    public String toString() {
        return "CurrentUser{" +
                "ID='" + ID + '\'' +
                ", Name='" + Name + '\'' +
                ", Mobile='" + Mobile + '\'' +
                ", PhotoURL='" + PhotoURL + '\'' +
                '}';
    }
}
