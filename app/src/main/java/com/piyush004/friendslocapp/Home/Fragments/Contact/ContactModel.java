package com.piyush004.friendslocapp.Home.Fragments.Contact;

public class ContactModel {

    private String Name;
    private String Mobile;
    private String PhotoURL;

    public ContactModel() {
    }

    public ContactModel(String name, String mobile, String photoURL) {
        Name = name;
        Mobile = mobile;
        PhotoURL = photoURL;
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
        return "PhoneContact{" +
                "Name='" + Name + '\'' +
                ", Mobile='" + Mobile + '\'' +
                ", PhotoURL='" + PhotoURL + '\'' +
                '}';
    }
}

