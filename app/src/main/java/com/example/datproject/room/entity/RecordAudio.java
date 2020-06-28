package com.example.datproject.room.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Class sẽ được generate thành 1 table trong database
 */
@Entity(tableName = "record_audio")
public class RecordAudio {
    @PrimaryKey(autoGenerate = true)
    private long recordId;
    @ColumnInfo(name = "record_name")
    private String recordName;
    @ColumnInfo(name = "date_time")
    private String dateTime;
    @ColumnInfo(name = "record_time")
    private String recordTime;


    public RecordAudio(String recordName, String dateTime, String recordTime) {
        this.recordName = recordName;
        this.dateTime = dateTime;
        this.recordTime = recordTime;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }


    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }
}
