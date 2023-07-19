package com.demo.expensetracker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static String DateFormat(String oldstringDate){
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", new Locale(getCountry()));
        try {
            Date date = new SimpleDateFormat("yyyy-MM").parse(oldstringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldstringDate;
        }

        return newDate;
    }

    public static String getMonthYear(String oldstringDate){
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MMM", new Locale(getCountry()));
        try {
            Date date = new SimpleDateFormat("yyyy-MM").parse(oldstringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldstringDate;
        }

        return newDate;
    }

    public static String GetMonth(String oldstringDate){
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM", new Locale(getCountry()));
        try {
            Date date = new SimpleDateFormat("yyyy-MM").parse(oldstringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldstringDate;
        }

        return newDate;
    }

    public static String getCountry(){
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }
}
