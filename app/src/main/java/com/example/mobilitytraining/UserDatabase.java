package com.example.mobilitytraining;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UserDatabase extends SQLiteOpenHelper {

    public UserDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query1 = "create table users(username text, email text, password text)";
        sqLiteDatabase.execSQL(query1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public void register(String username, String email, String password) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("password", password);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert("users", null, contentValues);
        sqLiteDatabase.close();
    }

    public int login(String username, String password) {

        int result = 0;
        String str[] = new String[2];
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        str[0] = username;
        str[1] = password;
        Cursor cursor = sqLiteDatabase.rawQuery("select * from users where username=? and password=?", str);
        if (cursor.moveToFirst()) {

            result = 1;
        }
        return result;
    }
}
