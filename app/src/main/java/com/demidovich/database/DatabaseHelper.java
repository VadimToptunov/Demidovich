package com.demidovich.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.demidovich.helpers.ListItem;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "generated_passwords";
    public static final String COLUMN_ID = "passwordID";
    public static final String COLUMN_NAME = "password";

    public static final String DB_NAME = "generated_passwords";
    public static final int DB_VERSION = 1;

    public static final String SQL_CREATE = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT);",
            TABLE_NAME, COLUMN_ID, COLUMN_NAME
    );
    public static final String SQL_DELETE = String.format("DROP TABLE %s;", TABLE_NAME);

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onDelete(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    public void onDelete(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE);
    }

    public ArrayList<ListItem> getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_NAME + " FROM " + TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<ListItem> passArrayList = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {

            if (cursor.moveToFirst()) {
                do {
                    passArrayList.add(new ListItem(cursor.getString(0)));
                } while (cursor.moveToNext());
            }
        }
        assert cursor != null;
        cursor.close();
        return passArrayList;
    }

    public void saveToDb(String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("INSERT INTO %s VALUES(NULL, \"%s\");", TABLE_NAME, password);
        db.execSQL(query);
    }

    public void deletePassword(String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("DELETE FROM %s WHERE password = \"%s\";", TABLE_NAME, password);
        db.execSQL(query);
    }
}
