package com.shmoose.quotes;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Author.class, Quote.class}, version=1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AuthorDao authorDao();
    public abstract QuoteDao quoteDao();


}
