package com.example.datproject.record;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Vẽ biên độ âm thanh lên màn hình
 */
public class VisualizerView extends View {
    private static final int LINE_WIDTH = 9;    // khoảng cách giữa 2 lần vẽ biên độ
    private static final int LINE_SCALE = 32767;    // scales visualizer lines
    private static final int LINE_SPACE = 5;    //khoảng trống giữa 2 biên độ
    private List<Float> amplitudes; // mảng các biên độ
    private float width; // width of this View
    private float height; // height of this View
    private Paint audioLine; // specifies line drawing characteristics

    // constructor
    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        audioLine = new Paint(); // create Paint for lines
        audioLine.setColor(Color.RED); // set color to green
        audioLine.setStrokeWidth(LINE_WIDTH - LINE_SPACE); // chiều rộng của 1 biên độ
    }

    // called when the dimensions of the View change
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w; // new width of this View
        height = h; // new height of this View
        Log.d("WIDTH", String.valueOf(width));
        Log.d("HEIGHT", String.valueOf(height));
        amplitudes = new ArrayList<Float>((int) (width / LINE_WIDTH));
    }

    // clear all amplitudes to prepare for a new visualization
    public void clear() {
        amplitudes.clear();
    }

    // add the given amplitude to the amplitudes ArrayList
    public void addAmplitude(float amplitude) {
        amplitudes.add(amplitude); // add newest to the amplitudes ArrayList

        // if the power lines completely fill the VisualizerView
        if (amplitudes.size() * LINE_WIDTH >= width) {
            amplitudes.remove(0); // remove oldest power value
        }
    }

    // draw the visualizer with scaled lines representing the amplitudes
    @Override
    public void onDraw(Canvas canvas) {
        float middle = height / 2; // get the middle of the View
        float curX = 0; // start curX at zero
        // for each item in the amplitudes ArrayList

        for (float power : amplitudes) {
            float scaledHeight = (power / LINE_SCALE) * (height -1); // scale the power
            curX += LINE_WIDTH; // increase X by LINE_WIDTH
            if (scaledHeight >= middle) {
                scaledHeight = (middle - 50) * 2;
            }
            // draw a line representing this item in the amplitudes ArrayList
            canvas.drawLine(curX, middle + scaledHeight / 2, curX, middle
                    - scaledHeight / 2, audioLine);
        }
    }

}

