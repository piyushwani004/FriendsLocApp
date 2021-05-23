package com.piyush004.friendslocapp.Home.Fragments.Contact;

public class ContactModel {

    private String ID;
    private String Name;
    private String Mobile;
    private String PhotoURL;
    private boolean IsCommon;

    public ContactModel() {
    }

    public ContactModel(String ID, String name, String mobile, String photoURL, boolean isCommon) {
        this.ID = ID;
        Name = name;
        Mobile = mobile;
        PhotoURL = photoURL;
        IsCommon = isCommon;
    }

    public ContactModel(String ID, String name, String mobile, String photoURL) {
        this.ID = ID;
        Name = name;
        Mobile = mobile;
        PhotoURL = photoURL;
    }

    public ContactModel(String name, String mobile, String photoURL) {
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

    public boolean isCommon() {
        return IsCommon;
    }

    public void setCommon(boolean common) {
        IsCommon = common;
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
        return "ContactModel{" +
                "ID='" + ID + '\'' +
                ", Name='" + Name + '\'' +
                ", Mobile='" + Mobile + '\'' +
                ", PhotoURL='" + PhotoURL + '\'' +
                ", IsCommon=" + IsCommon +
                '}';
    }
}
