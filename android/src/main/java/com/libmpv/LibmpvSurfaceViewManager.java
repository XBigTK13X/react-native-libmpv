package com.libmpv;

import dev.jdtech.mpv.MPVLib;
import com.libmpv.LibmpvSurfaceView;

import androidx.annotation.NonNull;

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
import androidx.annotation.Nullable;
import java.util.Map;

// Available event constants
// https://github.com/facebook/react-native/blob/main/packages/react-native/ReactAndroid/src/main/java/com/facebook/react/uimanager/UIManagerModuleConstants.java
// An example video player using VLC
// https://github.com/razorRun/react-native-vlc-media-player/blob/master/android/src/main/java/com/yuanzhou/vlc/vlcplayer/ReactVlcPlayerViewManager.java
public class LibmpvSurfaceViewManager extends SimpleViewManager<LibmpvSurfaceView> {

    public static final String REACT_CLASS = "LibmpvSurfaceView";
    private static final int COMMAND_SET_PLAY_URL = 1;
    private static final int COMMAND_CLEANUP = 2;

    private String _playUrl = null;
    private Integer _surfaceWidth = null;
    private Integer _surfaceHeight = null;
    private Integer _audioIndex = null;
    private Integer _subtitleIndex = null;
    private DeviceEventManagerModule.RCTDeviceEventEmitter _reactEventEmitter = null;

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public LibmpvSurfaceView createViewInstance(ThemedReactContext reactContext) {
        _reactEventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        return new LibmpvSurfaceView(reactContext);
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "SetPlayUrl", COMMAND_SET_PLAY_URL,
                "Cleanup", COMMAND_CLEANUP
        );
    }

    @Override
    public void receiveCommand(LibmpvSurfaceView view, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case COMMAND_SET_PLAY_URL:
                String playUrl = args != null ? args.getString(0) : null;
                this.setPlayUrl(view, playUrl);
                break;
            case COMMAND_CLEANUP:
                view.getMpv().cleanup();
                break;
        }
    }

    private void attemptCreation(LibmpvSurfaceView view) {
        boolean allReactPropsHandled = _playUrl != null
                && _surfaceWidth != null
                && _surfaceHeight != null
                && _audioIndex != null
                && _subtitleIndex != null;
        if (allReactPropsHandled) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "attemptCreation");
            log.putString("argument", "initialize the MPV instance");
            _reactEventEmitter.emit("libmpvLog", log);

            view.getMpv().cleanup();
            view.getHolder().setFixedSize(_surfaceWidth, _surfaceHeight);
            view.getMpv().defaultSetup(view);

            view.getMpv().addLogObserver(new MPVLib.LogObserver() {
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

            view.getMpv().play(_playUrl, options);
        }
    }

    @ReactProp(name = "playUrl")
    public void setPlayUrl(LibmpvSurfaceView view, String playUrl) {
        boolean recreateMpv = false;
        if (_playUrl != null && !_playUrl.equalsIgnoreCase(playUrl)) {
            recreateMpv = true;
        }
        _playUrl = playUrl;
        if (view.getMpv().isCreated() && !recreateMpv) {
            view.getMpv().play(_playUrl);
        } else {
            attemptCreation(view);
        }
        if (_reactEventEmitter != null) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "setPlayUrl");
            log.putString("argument", "" + playUrl);
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

    @ReactProp(name = "surfaceWidth")
    public void setSurfaceWidth(LibmpvSurfaceView view, int surfaceWidth) {
        _surfaceWidth = surfaceWidth;
        if (view.getMpv().isCreated()) {
            view.getMpv().setSurfaceWidth(_surfaceWidth);
        } else {
            attemptCreation(view);
        }
        if (_reactEventEmitter != null) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "setSurfaceWidth");
            log.putString("argument", "" + _surfaceWidth);
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

    @ReactProp(name = "surfaceHeight")
    public void setSurfaceHeight(LibmpvSurfaceView view, int surfaceHeight
    ) {
        _surfaceHeight = surfaceHeight;
        if (view.getMpv().isCreated()) {
            view.getMpv().setSurfaceHeight(_surfaceHeight);
        } else {
            attemptCreation(view);
        }
        if (_reactEventEmitter != null) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "setSurfaceHeight");
            log.putString("argument", "" + _surfaceHeight);
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

    @ReactProp(name = "selectedAudioTrack")
    public void selectAudioTrack(LibmpvSurfaceView view, int audioTrackIndex
    ) {
        _audioIndex = audioTrackIndex;
        if (view.getMpv().isCreated()) {
            String mpvIndex = "no";
            if (_audioIndex != -1) {
                mpvIndex = "" + (_audioIndex + 1);
            }
            view.getMpv().setOptionString("aid", mpvIndex);
        } else {
            attemptCreation(view);
        }
        if (_reactEventEmitter != null) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "selectAudioTrack");
            log.putString("argument", "" + _audioIndex);
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

    @ReactProp(name = "selectedSubtitleTrack")
    public void selectSubtitleTrack(LibmpvSurfaceView view, int subtitleTrackIndex
    ) {
        _subtitleIndex = subtitleTrackIndex;
        if (view.getMpv().isCreated()) {
            String mpvIndex = "no";
            if (_subtitleIndex != -1) {
                mpvIndex = "" + (_subtitleIndex + 1);
            }
            view.getMpv().setOptionString("sid", mpvIndex);
        } else {
            attemptCreation(view);
        }
        if (_reactEventEmitter != null) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "selectSubtitleTrack");
            log.putString("argument", "" + _subtitleIndex);
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

    @ReactProp(name = "seekToSeconds")
    public void seekTo(LibmpvSurfaceView view, int seconds) {
        if (view.getMpv().isCreated()) {
            view.getMpv().seekToSeconds(seconds);
        }
        if (_reactEventEmitter != null) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "seekToSeconds");
            log.putString("argument", "" + seconds);
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

    @ReactProp(name = "isPlaying")
    public void setIsPlaying(LibmpvSurfaceView view, boolean isPlaying) {
        WritableMap log = Arguments.createMap();
        log.putString("method", "setIsPlaying");
        log.putString("argument", isPlaying ? "true" : "false");
        if (view.getMpv().isCreated() && view.getMpv().hasPlayedOnce()) {
            log.putString("playerState", view.getMpv().isPlaying() ? "play" : "paused");
            if (isPlaying && !view.getMpv().isPlaying()) {
                view.getMpv().unpause();
                log.putString("path", "Unpausing player");
            } else if (!isPlaying && view.getMpv().isPlaying()) {
                view.getMpv().pause();
                log.putString("path", "Pausing player");
            }
        } else {
            log.putString("path", "Instance not created");
            attemptCreation(view);
        }
        if (_reactEventEmitter != null) {
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }
}
