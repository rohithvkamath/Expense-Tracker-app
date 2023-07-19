package com.demo.expensetracker.db;
import android.provider.BaseColumns;

public class DatabaseContract {

    public static String TABLE_RECORD = "record";
    public static final class RecordColumns implements BaseColumns {
        // Record date
        public static String DATE = "date";
        // Record description
        public static String DESCRIPTION = "description";
    }

    public static String TABLE_WISHLIST = "wishlist";
    public static final class WishlistColumns implements BaseColumns {
        // Wishlist detail
        public static String ITEM = "item";
        // Wishlist price
        public static String PRICE = "price";
        // Wishlist need
        public static String NEED = "need";
        // Wishlist want
        public static String WANT = "want";
        // Wishlist is checked
        public static String CHECKED = "checked";
    }
}