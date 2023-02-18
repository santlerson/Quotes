package com.shmoose.quotes;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AuthorDao {

    @Insert
    long addAuthor(Author author);


    @Query("select * from author;")
    List<Author> listAllAuthors();





    @Query("select name from author where id=:id")
    String getAuthorName(int id);

    @Query("select * from author where name like '%' || :name || '%'")
    Author searchAuthors(String name);

    @Query("select name from author;")
    List<String> listAllAuthorNames();

    @Query("select * from author where id=:id")
    Author getAuthor(int id);

    @Query("select * from author order by name;")
    List<Author> getSortedAuthorList();
}
