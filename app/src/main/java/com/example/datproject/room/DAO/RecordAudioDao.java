package com.example.datproject.room.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.datproject.room.entity.RecordAudio;

import java.util.List;
/**
 *  Class có chức năng thực hiện các câu truy vấn dữ liệu từ database
 */

@Dao
public interface RecordAudioDao {
    @Insert()
    public void insertRecord(RecordAudio... record);

    @Update
    public void updateRecord(RecordAudio... record);

    @Delete
    public void deleteRecord(RecordAudio... record);

    @Query("DELETE FROM record_audio")
    public void deleteAllRecord();

    @Query("SELECT * FROM record_audio")
    public List<RecordAudio> getAllRecord();

    @Query("SELECT MAX(recordId) FROM record_audio")
    public int getMaxId();
}
