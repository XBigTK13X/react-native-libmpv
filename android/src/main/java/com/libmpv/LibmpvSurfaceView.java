package com.libmpv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LibmpvSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private boolean _isSurfaceCreated = false;

    public LibmpvSurfaceView(Context context) {
        super(context);
        this.getHolder().addCallback(this);
    }

    public LibmpvSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
    }

    public boolean isSurfaceCreated() {
        return _isSurfaceCreated;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        _isSurfaceCreated = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
