package com.example.androidproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DB extends SQLiteOpenHelper {
    public static final String DBName = "login.db";
    public DB( Context context) {

        super(context, "login.db", null, 1);
    }

    //onCreate method is used to create the db Database that has a table called users.
    //username is made primary key as it will be unique identifier
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table users(username TEXT primary key, password TEXT)");

    }


    @Override
    //onUpgrade is a required by SQLiteOpenHelper class. It is used to upgrade the database (e.x. - new columns are added in table)
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
       db.execSQL("drop table if exists user");
    }
    // To insert username and password in table when user registers
    public Boolean insertData(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("username",username);
        values.put("password", password);
        long result = db.insert("users", null,values);
        if(result ==-1) return false;
        else
            return true;
    }
    // To check if Username exists in table. This is prevent duplicate usernames
    public Boolean checkUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where username = ?", new String[] {username});
        if(cursor.getCount()>0) return true;
        else
            return false;

    }
    //To check if username and password exists in table
    public Boolean checkLogin(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where username = ? and password = ?", new String[] {username,password});
        if(cursor.getCount()>0) return true;
        else
            return false;

    }
}
