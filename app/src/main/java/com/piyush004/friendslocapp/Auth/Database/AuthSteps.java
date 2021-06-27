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

package com.piyush004.friendslocapp.Auth.Database;

public class AuthSteps {

    private int id;
    private String mobile;
    private String step_one;
    private String step_two;
    private String step_three;

    public AuthSteps() {
    }

    public AuthSteps(int id, String mobile, String step_one, String step_two, String step_three) {
        this.id = id;
        this.mobile = mobile;
        this.step_one = step_one;
        this.step_two = step_two;
        this.step_three = step_three;
    }

    public AuthSteps(String mobile, String step_one, String step_two, String step_three) {
        this.mobile = mobile;
        this.step_one = step_one;
        this.step_two = step_two;
        this.step_three = step_three;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStep_one() {
        return step_one;
    }

    public void setStep_one(String step_one) {
        this.step_one = step_one;
    }

    public String getStep_two() {
        return step_two;
    }

    public void setStep_two(String step_two) {
        this.step_two = step_two;
    }

    public String getStep_three() {
        return step_three;
    }

    public void setStep_three(String step_three) {
        this.step_three = step_three;
    }

    @Override
    public String toString() {
        return "AuthSteps{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                ", step_one='" + step_one + '\'' +
                ", step_two='" + step_two + '\'' +
                ", step_three='" + step_three + '\'' +
                '}';
    }
}
