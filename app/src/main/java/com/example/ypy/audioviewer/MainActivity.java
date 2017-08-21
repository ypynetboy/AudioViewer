package com.example.ypy.audioviewer;

import android.support.v7.app.AppCompatActivity;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements Recorder.OnUpdateListener {
    private Recorder recorder;
    private AudioWaveViewer waveViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        waveViewer = (AudioWaveViewer) findViewById(R.id.wave_viewer);
        waveViewer.startAutoFollow();

        recorder = new Recorder();
        recorder.init(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO);
        recorder.setOnUpdatedListener(this);
        recorder.start();
    }

    @Override
    public void onUpdated(short[] data, int length) {
        waveViewer.addWaveData(data, length);
    }
}
