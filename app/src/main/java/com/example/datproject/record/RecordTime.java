package com.example.datproject.record;

import android.os.Handler;
import android.os.SystemClock;

/**
 * Class có chức năng đếm thời gian đã chạy của bản ghi âm

 */
public class RecordTime {
    private long startTime, pauseTime, systemTime = 0;

    private Handler handler = new Handler();
    boolean isRun;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            systemTime = SystemClock.uptimeMillis() - startTime;
            long updateTime = pauseTime + systemTime;
            long secs = updateTime/1000;
            long mins = secs/60;
            secs = secs % 60;
            updateTime = updateTime % 1000; //miniseconds
            RecordFragment.binding.lblTimer.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs) + ":" +
                    String.format("%02d", updateTime));
            RecordFragment.binding.lblTimer1.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));
            handler.postDelayed(this, 0);
        }
    };

    public void startTime() {
        if (isRun)
            return;
        isRun = true;
        startTime = SystemClock.uptimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }).start();
    }

    public void pauseTime() {
        if (!isRun)
            return;
        isRun = false;
        pauseTime += systemTime;
        handler.removeCallbacks(runnable);
    }

    public void stopTime() {
        if(!isRun)
            return;
        isRun = false;
        pauseTime = 0;

        handler.removeCallbacks(runnable);
    }

    public long getPauseTime() {
        return pauseTime;
    }
}
