package com.libmpv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.libmpv.LibmpvWrapper;
import dev.jdtech.mpv.MPVLib;
import java.util.Map;

public class LibmpvSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private boolean _isSurfaceCreated = false;
    private LibmpvWrapper _mpv;
    private DeviceEventManagerModule.RCTDeviceEventEmitter _reactEventEmitter;
    private String _playUrl = null;
    private Integer _surfaceWidth = null;
    private Integer _surfaceHeight = null;
    private Integer _audioIndex = null;
    private Integer _subtitleIndex = null;

    public LibmpvSurfaceView(Context context) {
        super(context);
        this.getHolder().addCallback(this);
        _mpv = new LibmpvWrapper(context);
    }

    public LibmpvSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
    }

    public LibmpvWrapper getMpv() {
        return _mpv;
    }

    public void cleanup() {
        _mpv.cleanup();
    }

    public void createNativePlayer(
            String playUrl,
            Integer surfaceWidth,
            Integer surfaceHeight,
            Integer audioIndex,
            Integer subtitleIndex,
            DeviceEventManagerModule.RCTDeviceEventEmitter eventEmitter
    ) {
        _playUrl = playUrl;
        _surfaceWidth = surfaceWidth;
        _surfaceHeight = surfaceHeight;
        _audioIndex = audioIndex;
        _subtitleIndex = subtitleIndex;
        _reactEventEmitter = eventEmitter;
        _mpv.defaultSetup(this);
        WritableMap log = Arguments.createMap();
        log.putString("method", "LibmpvSurfaceView.createNativePlayer");
        log.putString("argument", "mpv defaultSetup complete. Waiting on surface creation.");
        _reactEventEmitter.emit("libmpvLog", log);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        holder.setFixedSize(_surfaceWidth, _surfaceHeight);
        _mpv.init();
        _mpv.addLogObserver(new MPVLib.LogObserver() {
            @Override
            public void logMessage(@NonNull String prefix, int level, @NonNull String text) {
                WritableMap log = Arguments.createMap();
                log.putString("prefix", prefix);
                log.putString("level", "" + level);
                log.putString("text", text);
                _reactEventEmitter.emit("libmpvLog", log);
            }
        });
        String options = "vid=1";
        if (_audioIndex == -1) {
            options += ",aid=no";
        } else {
            options += ",aid=" + (_audioIndex + 1);
        }
        if (_subtitleIndex == -1) {
            options += ",sid=no";
        } else {
            options += ",sid=" + (_subtitleIndex + 1);
        }

        _mpv.play(_playUrl, options);
        _mpv.setOptionString("pause", "no");
        WritableMap log = Arguments.createMap();
        log.putString("method", "LibmpvSurfaceView.surfaceCreated");
        log.putString("argument", "Surface created and MPV should be playing");
        _reactEventEmitter.emit("libmpvLog", log);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        _mpv.cleanup();
    }

}
