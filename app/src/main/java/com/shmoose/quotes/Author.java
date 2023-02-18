package com.shmoose.quotes;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Author {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo
    String name;

    public Author(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
