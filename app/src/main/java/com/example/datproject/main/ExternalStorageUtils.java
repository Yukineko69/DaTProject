package com.example.datproject.main;

import android.os.Environment;

/**
 * Class có các method dùng để kiểm tra tính available của bộ nhớ ngoài
 */
public class ExternalStorageUtils {

    /*
     * Kiểm tra xem device có bộ nhớ ngoài không
     */
    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState))
            return true;

        return false;
    }

    /*
     * Kiểm tra xe bộ nhớ ngoài SDCard có readonly không vì nếu là readonly thì
     * không thể tạo file trên đó được
     */
    public static boolean isExternalStorageReadOnly() {

        String dirState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(dirState))
            return true;

        return false;

    }

}
