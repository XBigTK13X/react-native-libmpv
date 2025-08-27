package com.libmpv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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

public class LibmpvSurfaceView extends SurfaceView implements SurfaceHolder.Callback, MPVLib.LogObserver, MPVLib.EventObserver {

    public static final String HWDEC = "mediacodec-copy";
    public static final String HWCODECS = "h264,hevc,mpeg4,mpeg2video,vp8,vp9,av1";

    private boolean _isSurfaceCreated;
    private LibmpvWrapper _mpv;

    private DeviceEventManagerModule.RCTDeviceEventEmitter _reactEventEmitter;
    private String _playUrl = null;
    private Integer _surfaceWidth = null;
    private Integer _surfaceHeight = null;
    private Integer _audioIndex = null;
    private Integer _subtitleIndex = null;
    private Boolean _useHardwareDecoder = null;

    public LibmpvSurfaceView(Context context, DeviceEventManagerModule.RCTDeviceEventEmitter reactEventEmitter) {
        super(context);
        this.getHolder().addCallback(this);
        _isSurfaceCreated = false;
        _reactEventEmitter = reactEventEmitter;
        _mpv = new LibmpvWrapper(context);
    }

    public LibmpvWrapper getMpv() {
        return _mpv;
    }

    public void cleanup() {
        this.getHolder().removeCallback(this);
        _mpv.cleanup();
    }

    boolean isSurfaceReady() {
        return _isSurfaceCreated;
    }

    public void createNativePlayer(
            String playUrl,
            Integer surfaceWidth,
            Integer surfaceHeight,
            Integer audioIndex,
            Integer subtitleIndex,
            Boolean useHardwareDecoder
    ) {
        _playUrl = playUrl;
        _surfaceWidth = surfaceWidth;
        _surfaceHeight = surfaceHeight;
        _audioIndex = audioIndex;
        _subtitleIndex = subtitleIndex;
        _useHardwareDecoder = useHardwareDecoder;
        _mpv.create();
        this.prepareMpvSettings();
        log("LibmpvSurfaceView.createNativePlayer", "mpv settings prepared. Waiting on surface creation.");
    }

    private void prepareMpvSettings() {
        // Disable window interaction until after the surface is attached
        // Otherwise the surface may be accessed my mpv before the surface is ready
        _mpv.addLogObserver(this);
        _mpv.addEventObserver(this);
        _mpv.setOptionString("force-window", "no");

        _mpv.setOptionString("config", "yes");
        _mpv.setOptionString("config-dir", _mpv.getMpvDirectoryPath());
        _mpv.setOptionString("sub-font-dir", _mpv.getMpvDirectoryPath());

        _mpv.setOptionString("keep-open", "always");
        _mpv.setOptionString("save-position-on-quit", "no");
        _mpv.setOptionString("ytdl", "no");
        _mpv.setOptionString("msg-level", "all=no");

        _mpv.setOptionString("profile", "fast");
        _mpv.setOptionString("vo", "gpu-next");
        if (_useHardwareDecoder) {
            // Note that mediacodec-copy works great on Android/TV
            // mediacodec crashes to a purple video frame immediately
            _mpv.setOptionString("hwdec", HWDEC);
            _mpv.setOptionString("hwdec-codecs", HWCODECS);
        } else {
            _mpv.setOptionString("hwdec", "no");
        }
        _mpv.setOptionString("gpu-context", "android");
        _mpv.setOptionString("opengl-es", "yes");
        _mpv.setOptionString("video-sync", "audio");
        //_mpv.setOptionString("override-display-fps", "24");
        //_mpv.setOptionString("vd-lavc-fast", "yes");
        //_mpv.setOptionString("vd-lavc-skiploopfilter", "nonkey");

        _mpv.setOptionString("ao", "audiotrack");
        _mpv.setOptionString("alang", "");

        _mpv.setOptionString("sub-font-provider", "none");
        _mpv.setOptionString("slang", "");
        _mpv.setOptionString("sub-scale-with-window", "yes");
        _mpv.setOptionString("sub-use-margins", "no");

        _mpv.setOptionString("cache", "yes");
        _mpv.setOptionString("cache-pause-initial", "yes");
        _mpv.setOptionString("cache-secs", "5");
        _mpv.setOptionString("demuxer-readahead-secs", "5");

    }

    public void log(String method, String argument) {
        WritableMap log = Arguments.createMap();
        log.putString("method", method);
        log.putString("argument", argument);
        _reactEventEmitter.emit("libmpvLog", log);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        holder.setFixedSize(_surfaceWidth, _surfaceHeight);
        _mpv.setPropertyString("android-surface-size", _surfaceWidth + "x" + _surfaceHeight);
        _mpv.attachSurface(this);
        this.prepareMpvPlayback();
        _isSurfaceCreated = true;
        log("LibmpvSurfaceView.surfaceCreated", "Surface created and MPV should be playing");
    }

    private void prepareMpvPlayback() {
        // Force subtitles to render on the surface view
        _mpv.init();
        _mpv.setOptionString("force-window", "yes");
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
    }

    public void setHardwareDecoder(boolean useHardware) {
        _useHardwareDecoder = useHardware;
        if (_useHardwareDecoder) {
            _mpv.setOptionString("hwdec", HWDEC);
            _mpv.setOptionString("hwdec-codecs", HWCODECS);
        } else {
            _mpv.setOptionString("hwdec", "no");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height
    ) {
        //_mpv.setPropertyString("android-surface-size", width + "x" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder
    ) {
        _mpv.setPropertyString("vo", "null");
        _mpv.setPropertyString("force-window", "no");
        _mpv.detachSurface();
    }

    // MPVLib.LogObserver
    @Override
    public void logMessage(@NonNull String prefix, int level, @NonNull String text) {
        WritableMap log = Arguments.createMap();
        log.putString("prefix", prefix);
        log.putString("level", "" + level);
        log.putString("text", text);
        _reactEventEmitter.emit("libmpvLog", log);
    }

    // MPVLib.EventObserver
    @Override
    public void eventProperty(@NonNull String property) {
        WritableMap event = Arguments.createMap();
        event.putString("property", property);
        event.putString("kind", "none");
        _reactEventEmitter.emit("libmpvEvent", event);
    }

    @Override
    public void eventProperty(@NonNull String property, long value) {
        WritableMap event = Arguments.createMap();
        event.putString("property", property);
        event.putString("kind", "long");
        event.putString("value", "" + value);
        _reactEventEmitter.emit("libmpvEvent", event);
    }

    @Override
    public void eventProperty(@NonNull String property, double value) {
        WritableMap event = Arguments.createMap();
        event.putString("property", property);
        event.putString("kind", "double");
        event.putString("value", "" + value);
        _reactEventEmitter.emit("libmpvEvent", event);
    }

    @Override
    public void eventProperty(@NonNull String property, boolean value) {
        WritableMap event = Arguments.createMap();
        event.putString("property", property);
        event.putString("value", value ? "true" : "false");
        event.putString("kind", "boolean");
        _reactEventEmitter.emit("libmpvEvent", event);
    }

    @Override
    public void eventProperty(@NonNull String property, @NonNull String value) {
        WritableMap event = Arguments.createMap();
        event.putString("property", property);
        event.putString("value", value);
        event.putString("kind", "string");
        _reactEventEmitter.emit("libmpvEvent", event);
    }

    @Override
    public void event(@MPVLib.Event int eventId) {
        WritableMap event = Arguments.createMap();
        event.putString("eventId", "" + eventId);
        event.putString("kind", "eventId");
        _reactEventEmitter.emit("libmpvEvent", event);
    }
}
