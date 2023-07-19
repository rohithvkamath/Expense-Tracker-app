package com.demo.expensetracker.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class WishlistItem implements Parcelable {

    private int id;
    private String item;
    private float price;
    private boolean need;
    private boolean want;
    private boolean checked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isNeed() {
        return need;
    }

    public void setNeed(boolean need) {
        this.need = need;
    }

    public boolean isWant() {
        return want;
    }

    public void setWant(boolean want) {
        this.want = want;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public WishlistItem() {
    }

    protected WishlistItem(Parcel in) {
        id = in.readInt();
        item = in.readString();
        price = in.readFloat();
        need = in.readByte() != 0;
        want = in.readByte() != 0;
        checked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(item);
        dest.writeFloat(price);
        dest.writeByte((byte) (need ? 1 : 0));
        dest.writeByte((byte) (want ? 1 : 0));
        dest.writeByte((byte) (checked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WishlistItem> CREATOR = new Creator<WishlistItem>() {
        @Override
        public WishlistItem createFromParcel(Parcel in) {
            return new WishlistItem(in);
        }

        @Override
        public WishlistItem[] newArray(int size) {
            return new WishlistItem[size];
        }
    };

    @Override
    public String toString() {
        return "WishlistItem{" +
                "id=" + id +
                ", item='" + item + '\'' +
                ", price=" + price +
                ", need=" + need +
                ", want=" + want +
                ", checked=" + checked +
                '}';
    }
}