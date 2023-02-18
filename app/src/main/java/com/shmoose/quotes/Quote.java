package com.shmoose.quotes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Quote implements Parcelable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo
    String content;

    @ColumnInfo
    int year;

    @ColumnInfo
    int month;

    @ColumnInfo
    int day;

    @ColumnInfo
    long added;

    @ColumnInfo
    int authorId;

    public Quote(String content, int year, int month, int day, long added, int authorId) {
        this.content = content;
        this.year = year;
        this.month = month;
        this.day = day;
        this.added = added;
        this.authorId = authorId;
    }
    @Ignore
    public Quote(String content, int year, int month, int day, int authorId) {
        this.content = content;
        this.year = year;
        this.month = month;
        this.day = day;
        this.added = System.currentTimeMillis();
        this.authorId = authorId;
    }

    protected Quote(Parcel in) {
        id = in.readInt();
        content = in.readString();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        added = in.readLong();
        authorId = in.readInt();
    }

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(content);
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
        parcel.writeLong(added);
        parcel.writeInt(authorId);
    }
    public String dateString(){
        return String.format("%s/%s/%s", day, month, year);
    }
}
