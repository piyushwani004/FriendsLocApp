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

package com.piyush004.friendslocapp.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FriendLocatorDB";
    private static final String TABLE_CONTACTS = "auth_steps";
    private static final String KEY_ID = "id";
    private static final String KEY_ONE = "step_one";
    private static final String KEY_TWO = "step_two";
    private static final String KEY_THREE = "step_three";
    private static final String KEY_MOBILE = "mobile";
    private static final String SHARE_LOCATION = "share_location";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MOBILE + " TEXT," + KEY_ONE + " TEXT," + KEY_TWO + " TEXT,"
                + KEY_THREE + " TEXT," + SHARE_LOCATION + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        onCreate(db);

    }

    public void addAllData(AuthSteps authSteps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_MOBILE, authSteps.getMobile());
        values.put(KEY_ONE, authSteps.getStep_one());
        values.put(KEY_TWO, authSteps.getStep_two());
        values.put(KEY_THREE, authSteps.getStep_three());
        values.put(SHARE_LOCATION, authSteps.getShare_location());

        db.insert(TABLE_CONTACTS, null, values);
        db.close();

    }

    public void updateStepOne(String s, String s1) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " + TABLE_CONTACTS + " SET  step_one = " + "'" + s + "' " + "WHERE mobile = " + "'" + s1 + "'");
    }

    public void updateStepTwo(String s, String s1) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " + TABLE_CONTACTS + " SET step_two = " + "'" + s + "' " + "WHERE mobile = " + "'" + s1 + "'");
    }

    public void updateStepThree(String s, String s1) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " + TABLE_CONTACTS + " SET step_three = " + "'" + s + "' " + "WHERE mobile = " + "'" + s1 + "'");
    }

    public void updateShareLocation(String s, String s1) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE " + TABLE_CONTACTS + " SET share_location = " + "'" + s + "' " + "WHERE mobile = " + "'" + s1 + "'");
    }

    public AuthSteps getAllSteps(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID, KEY_MOBILE,
                        KEY_ONE, KEY_TWO, KEY_THREE, SHARE_LOCATION}, KEY_MOBILE + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;
        String sid = cursor.getString(0);
        String mob = cursor.getString(1);
        String s1 = cursor.getString(2);
        String s2 = cursor.getString(3);
        String s3 = cursor.getString(4);
        String loc = cursor.getString(5);
        Log.e(TAG, "getAllSteps: " + sid + mob + s1 + s2 + s3 + loc);

        // return AuthSteps
        assert cursor != null;
        return new AuthSteps(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
    }

}
