package com.example.datproject.record;

import android.os.Handler;


/**
 * Control visualizer
 */
public class VisualizerRecord {
    public static final int REPEAT_INTERVAL = 40;
    private Handler handler;
    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {

                // get the current amplitude
                int x = RecordFragment.mediaRecorder.getMaxAmplitude();
                RecordFragment.binding.visualizerView.addAmplitude(x); // update the VisualizeView
                RecordFragment.binding.visualizerView.invalidate(); // refresh the VisualizerView

                // update in 40 milliseconds
                handler.postDelayed(this, REPEAT_INTERVAL);
        }
    };

    public VisualizerRecord() {
        handler = new Handler();
    }


    public void pauseVisualize() {
        handler.removeCallbacks(updateVisualizer);
    }

    public void stopVisualize() {
        pauseVisualize();
        RecordFragment.binding.visualizerView.clear();
        RecordFragment.binding.visualizerView.invalidate();
    }

    public void startVisualize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(updateVisualizer);
            }
        }).start();
    }

}
