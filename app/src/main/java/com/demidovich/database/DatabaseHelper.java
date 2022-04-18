package com.demidovich.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "generated_passwords";
    public static final String COLUMN_ID = "passwordID";
    public static final String COLUMN_NAME = "password";

    public static final String DB_FILE_NAME = "generated_passwords.db";
    public static final int DB_VERSION = 1; // for database version

    public static final String SQL_CREATE = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT);",
            TABLE_NAME, COLUMN_ID, COLUMN_NAME
    );
    public static final String SQL_DELETE = String.format("DROP TABLE %s;", TABLE_NAME);

    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DB_FILE_NAME,null,DB_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onDelete();
        onCreate(sqLiteDatabase);
    }

    public void onDelete(){
        db.execSQL(SQL_DELETE);
    }

    public ArrayList<String> getAllData(){
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<String> passArrayList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                passArrayList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return passArrayList;
    }

    public void saveToDb(String password) {
        String query = String.format("INSERT INTO artists (%s) VALUES(%s);", TABLE_NAME, password);
        db.execSQL(query);
    }
}
