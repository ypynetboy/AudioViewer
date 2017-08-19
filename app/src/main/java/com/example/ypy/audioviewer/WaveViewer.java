package com.example.ypy.audioviewer;

import android.content.Context;
import android.view.SurfaceView;

/**
 * Created by ypy on 2017/8/19.
 */

public class WaveViewer extends SurfaceView {
    private int xScale = 5;

    public WaveViewer(Context context) {
        super(context);
    }

    public int getMaxVisableSample() {
        return getWidth() * xScale;
    }

    public void setWaveData(short[] data) {
    }
}
