package com.demo.expensetracker.db;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.demo.expensetracker.entity.WishlistItem;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.demo.expensetracker.db.DatabaseContract.TABLE_WISHLIST;
import static com.demo.expensetracker.db.DatabaseContract.WishlistColumns.CHECKED;
import static com.demo.expensetracker.db.DatabaseContract.WishlistColumns.ITEM;
import static com.demo.expensetracker.db.DatabaseContract.WishlistColumns.NEED;
import static com.demo.expensetracker.db.DatabaseContract.WishlistColumns.PRICE;
import static com.demo.expensetracker.db.DatabaseContract.WishlistColumns.WANT;

public class WishlistHelper {
    private static String DATABASE_TABLE = TABLE_WISHLIST;
    private Context context;
    private DatabaseHelper dataBaseHelper;

    private SQLiteDatabase database;

    public WishlistHelper(Context context){
        this.context = context;
    }

    public WishlistHelper open() throws SQLException {
        dataBaseHelper = new DatabaseHelper(context);
        database = dataBaseHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dataBaseHelper.close();
    }

    public ArrayList<WishlistItem> query(){
        ArrayList<WishlistItem> arrayList = new ArrayList<WishlistItem>();
        Cursor cursor = database.query(DATABASE_TABLE,null,null,null,null,null,_ID +" ASC",null);
        cursor.moveToFirst();
        WishlistItem wishlistItem;
        if (cursor.getCount()>0) {
            do {

                wishlistItem = new WishlistItem();
                wishlistItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                wishlistItem.setItem(cursor.getString(cursor.getColumnIndexOrThrow(ITEM)));
                wishlistItem.setPrice(cursor.getFloat(cursor.getColumnIndexOrThrow(PRICE)));
                wishlistItem.setNeed(cursor.getInt(cursor.getColumnIndexOrThrow(NEED)) > 0);
                wishlistItem.setWant(cursor.getInt(cursor.getColumnIndexOrThrow(WANT)) > 0);
                wishlistItem.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(CHECKED)) > 0);

                arrayList.add(wishlistItem);
                cursor.moveToNext();

            } while (!cursor.isAfterLast());
        }
        cursor.close();

        return arrayList;
    }

    public SQLiteDatabase getDatabase () {
        return getDatabase(false);
    }

    public SQLiteDatabase getDatabase (boolean forceWritable) {
        try {
            return forceWritable ? dataBaseHelper.getWritableDatabase() : dataBaseHelper.getReadableDatabase();
        } catch (IllegalStateException e) {
            return this.database;
        }
    }

    public long insert(WishlistItem wishlistItem){
        ContentValues initialValues =  new ContentValues();
        initialValues.put(ITEM, wishlistItem.getItem());
        initialValues.put(PRICE, wishlistItem.getPrice());
        initialValues.put(NEED, wishlistItem.isNeed());
        initialValues.put(WANT, wishlistItem.isWant());
        initialValues.put(CHECKED, wishlistItem.isChecked());
        return database.insert(DATABASE_TABLE, null, initialValues);
    }

    public int update(WishlistItem wishlistItem){
        ContentValues args = new ContentValues();
        args.put(ITEM, wishlistItem.getItem());
        args.put(PRICE, wishlistItem.getPrice());
        args.put(NEED, wishlistItem.isNeed());
        args.put(WANT, wishlistItem.isWant());
        args.put(CHECKED, wishlistItem.isChecked());
        return database.update(DATABASE_TABLE, args, _ID + "= '" + wishlistItem.getId() + "'", null);
    }

    public int delete(int id){
        return database.delete(TABLE_WISHLIST, _ID + " = '"+id+"'", null);
    }
}