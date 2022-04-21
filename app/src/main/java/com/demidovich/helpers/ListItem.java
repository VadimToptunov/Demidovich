package com.demidovich.helpers;

import android.content.Context;

import com.demidovich.database.DatabaseHelper;

import java.util.ArrayList;

public class ListItem {
    private String passwordItem;

    public ListItem(){

    };

    public ListItem(String passwordItem){
        this.passwordItem = passwordItem;
    }

    public String getPasswordItem() {
        return passwordItem;
    }

    public ArrayList<String> getAllPasswords(Context context){
        ArrayList<String> passwords = new ArrayList<>() ;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        ArrayList <ListItem> items = databaseHelper.getAllData();
        for (ListItem pass : items){
            passwords.add(pass.passwordItem);
        }
        return passwords;
    }
}
