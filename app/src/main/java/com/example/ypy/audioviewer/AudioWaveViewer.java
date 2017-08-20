package com.example.ypy.audioviewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.nio.ShortBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ypy on 2017/8/19.
 */

public class AudioWaveViewer extends SurfaceView {
    private static final int MAX_SAMPLE_LENGTH = 1024 * 1024;
    private int xScale = 5;
    private int maxShowSamples = 0; // 最多可显示的采样数
    private short[] dataBuffer = new short[MAX_SAMPLE_LENGTH];
    private int dataLength; // 采样数据长度
    private int index; // 当前显示位置
    private AutoFollowThread autoFollowThread;

    public AudioWaveViewer(Context context) {
        super(context);
    }

    public void startAutoFollow() {
        if (autoFollowThread != null)
            return;
        autoFollowThread = new AutoFollowThread(30);
    }

    public void stopAutoFollow() {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        maxShowSamples = w * xScale;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public int getMaxVisableSample() {
        return maxShowSamples;
    }

    public void addWaveData(short[] data, int length) {
        if (dataLength + length < MAX_SAMPLE_LENGTH) {
            System.arraycopy(dataBuffer, maxShowSamples, data, 0, length);
            dataLength += length;
        }
    }

    public void reset() {
        index = 0;
        dataLength = 0;
    }

    protected void setIndex(int newIndex) {
        index = Math.min(newIndex, dataLength - maxShowSamples);
        if (index < 0)
            index = 0;
    }

    private class DrawStaff {
        private Paint paint;

        public DrawStaff() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.GREEN);
        }

        protected void draw() {
            SurfaceHolder holder = getHolder();
            if (holder != null) {
                Canvas canvas = holder.lockCanvas();
                int height = getHeight() / 2;
                int count = Math.min(maxShowSamples, dataLength-index);
                int y, x = (maxShowSamples-count)/xScale;
                int lastY = 0;
                for (int i=0; i<count; i+=xScale) {
                    y = (int) (computeX(i) * height + height);
                    if (i != 0) {
                        canvas.drawLine(x, lastY, ++x, y, paint);
                    }
                    lastY = y;
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }

        private double computeX(int index) {
            int sum = 0;
            for ( ; index < index+xScale; index++)
                sum += dataBuffer[index];
            sum /= xScale;
            if (0 == sum)
                return 0;
            return (double)sum / Short.MAX_VALUE;
        }
    }

    protected class AutoFollowThread implements Runnable {
        private int interval;
        private boolean destoryed = false;

        public AutoFollowThread(int interval) {
            this.interval = interval;
            new Thread(this).start();
        }

        public void destory() {
            destoryed = true;
        }

        @Override
        public void run() {
            while (!destoryed) {
                // 动态调整当前样本位置
                setIndex((int) (index + (maxShowSamples * 0.05)));
                // 调用绘制方法
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
