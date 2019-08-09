package com.example.myassistent.CartDatabase;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Table.class},version = 1)
public abstract class QuestionAnswerDB extends RoomDatabase {
    public abstract DBAccess MyDao();
}
