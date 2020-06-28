package com.example.datproject.record;

import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.datproject.main.MainActivity;
import com.example.datproject.room.entity.RecordAudio;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Lưu thông tin record vào database
 */
public class SaveRecordToDatabase {
    private String recordName;
    private String recordTime;
    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public SaveRecordToDatabase() {
    }

    public SaveRecordToDatabase(String name, String recordTime) {
        this.recordName = name;
        this.recordTime = recordTime;
    }

    public void saveToDatabase() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateTime = dateFormat.format(c.getTime());

        RecordAudio record = new RecordAudio(recordName, dateTime, recordTime);

        MainActivity.database.recordAudioDao().insertRecord(record);
    }
}
