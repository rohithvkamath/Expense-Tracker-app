package com.demo.expensetracker.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "dbexpensetracker";
    private static final int DATABASE_VERSION = 2;
    private static final String SQL_CREATE_TABLE_RECORD = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_RECORD,
            DatabaseContract.RecordColumns._ID,
            DatabaseContract.RecordColumns.DATE,
            DatabaseContract.RecordColumns.DESCRIPTION
    );

    private static final String SQL_CREATE_TABLE_WISHLIST = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT NOT NULL," +
                    " %s TEXT NOT NULL," +
                    " %s BOOLEAN NOT NULL CHECK (%<s IN (0,1))," +
                    " %s BOOLEAN NOT NULL CHECK (%<s IN (0,1))," +
                    " %s BOOLEAN NOT NULL CHECK (%<s IN (0,1)))",
            DatabaseContract.TABLE_WISHLIST,
            DatabaseContract.WishlistColumns._ID,
            DatabaseContract.WishlistColumns.ITEM,
            DatabaseContract.WishlistColumns.PRICE,
            DatabaseContract.WishlistColumns.NEED,
            DatabaseContract.WishlistColumns.WANT,
            DatabaseContract.WishlistColumns.CHECKED
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_RECORD);
        db.execSQL(SQL_CREATE_TABLE_WISHLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_RECORD);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_WISHLIST);
        onCreate(db);
    }
}