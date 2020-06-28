package com.example.datproject.room.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.datproject.room.DAO.RecordAudioDao;
import com.example.datproject.room.entity.RecordAudio;

/**
 *  Class có chức năng để khai báo database
 */
@Database(entities = {RecordAudio.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecordAudioDao recordAudioDao();
}
