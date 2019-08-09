package com.example.myassistent.CartDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DBAccess {
    @Insert
    public void addQuestionAnswer(Table table);

    @Query("select * from `MyDb`")
    public List<Table> getAnswers();
}
