package com.example.ypy.audioviewer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Recorder.OnUpdateListener {
    private Recorder recorder;
    private WaveViewer waveViewer;
    private int maxShowSamples = 0; // 最多可显示的采样数
    private short[] sampleBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waveViewer = (WaveViewer) findViewById(R.id.wave_viewer);

        recorder = new Recorder();
        recorder.init(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO);
        recorder.setOnUpdatedListener(this);
        recorder.start();
    }

    @Override
    protected void onResume() {
        maxShowSamples = waveViewer.getMaxVisableSample();
        if (null == sampleBuffer || sampleBuffer.length < maxShowSamples)
            sampleBuffer = new short[maxShowSamples];
        super.onResume();
    }

    @Override
    public void onUpdated(int length) {
        // TODO SHOW AUDIO WAVE
//        recorder.getAudioData()
        waveViewer.setWaveData(sampleBuffer);
    }
}
