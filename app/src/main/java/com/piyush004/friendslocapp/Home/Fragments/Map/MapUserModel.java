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

package com.piyush004.friendslocapp.Home.Fragments.Map;

public class MapUserModel {

    private String MUId;

    public MapUserModel() {
    }

    public MapUserModel(String MUId) {
        this.MUId = MUId;
    }

    public String getMUId() {
        return MUId;
    }

    public void setMUId(String MUId) {
        this.MUId = MUId;
    }
}
