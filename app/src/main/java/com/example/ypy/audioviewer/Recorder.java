package com.example.ypy.audioviewer;

import android.media.AudioFormat;
import android.media.AudioRecord;

import java.nio.ShortBuffer;

/**
 * Created by ypy on 2017/8/19.
 */

public class Recorder implements Runnable {
    private static final long LISTENER_CALL_INTERVAL = 100; // ms

    private boolean inited = false;
    private AudioRecord record;
    private int bufferSize;
    private OnUpdateListener listener;
    private long lastCallTime; // Last call updated listener times.

    public void setOnUpdatedListener(OnUpdateListener listener) {
        this.listener = listener;
    }

    public void init(int audioSource, int sampleRateInHz, int channelConfig) {
        if (inited)
            return;
        bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, AudioFormat.ENCODING_PCM_16BIT) * 1024;
        record = new AudioRecord(audioSource, sampleRateInHz, channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        inited = true;
        record.startRecording();
        new Thread(this).start();
    }

    public void start() {
        if (!inited) return;
    }

    public void stop() {
        if (!inited) return;
        record.stop();
    }

    public void release() {
        if (!inited) return;
        record.release();
        inited = false;
    }

    @Override
    public void run() {
        short[] buffer = new short[bufferSize];
        int len;
        while (inited) {
            len = record.read(buffer, 0, bufferSize);
            if (len > 0) {
                if (listener != null && System.currentTimeMillis()-lastCallTime > LISTENER_CALL_INTERVAL) {
                    listener.onUpdated(buffer, len);
                    lastCallTime = System.currentTimeMillis();
                }
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    // 数据更新监听器
    public interface OnUpdateListener {
        void onUpdated(short[] data, int length);
    }
}
