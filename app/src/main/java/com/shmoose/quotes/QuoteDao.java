package com.shmoose.quotes;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface QuoteDao {
    @Insert
    void addQuote(Quote quote);

    @Query("select * from quote order by year desc, month desc, day desc, added desc")
    List<Quote> getAllQuotes();

    @Query("select * from quote where authorId=:authorId order by year desc, month desc, day desc, added desc")
    List<Quote> getAllQuotesByAuthor(int authorId);

    @Query("select * from quote where id=:id;")
    Quote getQuote(int id);

    @Update
    void updateQuote(Quote quote);

    @Delete
    void deleteQuote(Quote quote);

    

}
