package com.demo.expensetracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Record implements Parcelable {
    private int id;
    private String date;

    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.date);
        dest.writeString(this.description);
    }

    public Record() {
    }

    public Record(String date, String description) {
        this.date = date;
        this.description = description;
    }

    protected Record(Parcel in) {
        this.id = in.readInt();
        this.date = in.readString();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<Record>CREATOR = new Parcelable.Creator<Record>() {

        @Override
        public Record createFromParcel(Parcel source) {
            return new Record(source);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}