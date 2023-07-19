package com.demo.expensetracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.demo.expensetracker.entity.Record;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.demo.expensetracker.db.DatabaseContract.RecordColumns.DATE;
import static com.demo.expensetracker.db.DatabaseContract.RecordColumns.DESCRIPTION;
import static com.demo.expensetracker.db.DatabaseContract.TABLE_RECORD;

public class RecordHelper {
    private static String DATABASE_TABLE = TABLE_RECORD;
    private Context context;
    private DatabaseHelper dataBaseHelper;

    private SQLiteDatabase database;

    public RecordHelper(Context context){
        this.context = context;
    }

    public RecordHelper open() throws SQLException {
        dataBaseHelper = new DatabaseHelper(context);
        dataBaseHelper.onOpen(database);
        database = dataBaseHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dataBaseHelper.close();
    }

    public ArrayList<Record> query(){
        ArrayList<Record> arrayList = new ArrayList<Record>();
        Cursor cursor = database.query(DATABASE_TABLE,null,null,null,null,null,DATE +" DESC",null);
        cursor.moveToFirst();
        Record record;
        if (cursor.getCount()>0) {
            do {
                record = new Record();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                record.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DATE)));
                record.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));

                arrayList.add(record);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<Record> getRecords (String whereCondition) {
        ArrayList<Record> recordList = new ArrayList<>();

        String query = "SELECT * "
                + " FROM " + TABLE_RECORD
                + whereCondition;

        try (Cursor cursor = getDatabase().rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                do {
                    int i = 0;
                    Record record = new Record();

                    record.setId(cursor.getInt(i++));
                    record.setDate(cursor.getString(i++));
                    record.setDescription(cursor.getString(i++));
                    recordList.add(record);
                } while (cursor.moveToNext());
            }
        }

        return recordList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<String> getMonthYearWithRecords(){
        ArrayList<String> monthYearList = new ArrayList<>();

        String query = "SELECT DISTINCT substr (DATE,1,7) FROM record ORDER BY DATE ASC";

        try (Cursor cursor = getDatabase().rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                do {
                    int i = 0;
                    monthYearList.add(cursor.getString(i++));
                } while (cursor.moveToNext());
            }
        }

        return monthYearList;
    }

    private float countDayExpense(String str){
        float sum = 0;
        String newString = str.replaceAll("[^0-9.]+", " ");
        newString = newString.trim();
        newString = newString.replaceAll(" +", ",");
        String[] numbers = newString.split(",");

        for (int i = 0; i < numbers.length; i++) {
            if (!numbers[i].isEmpty()){
                sum += Float.parseFloat(numbers[i]);
            }
        }
        return sum;
    }

    public String getTotalAmountSpent (ArrayList<Record> recordList){
        float sum = 0;
        for (int i = 0; i < recordList.size(); i++) {
            sum += countDayExpense(recordList.get(i).getDescription());
        }
        if(sum == (long) sum)
            return String.format("%d",(long)sum);
        else
            return String.format("%.1f", sum);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<Record> queryByMonthYear(String month, String year) {
        String whereCondition = " WHERE "
                + "strftime('%m'," + DATE + ") = '" + month + "' AND strftime('%Y'," + DATE + ") = '" + year + "'" + " ORDER BY DATE DESC";
        return getRecords(whereCondition);
    }

    public long insert(Record record){
        ContentValues initialValues =  new ContentValues();
        initialValues.put(DATE, record.getDate());
        initialValues.put(DESCRIPTION, record.getDescription());
        return database.insert(DATABASE_TABLE, null, initialValues);
    }

    public int update(Record record){
        ContentValues args = new ContentValues();
        args.put(DATE, record.getDate());
        args.put(DESCRIPTION, record.getDescription());
        return database.update(DATABASE_TABLE, args, _ID + "= '" + record.getId() + "'", null);
    }

    public int delete(int id){
        return database.delete(TABLE_RECORD, _ID + " = '"+id+"'", null);
    }
}