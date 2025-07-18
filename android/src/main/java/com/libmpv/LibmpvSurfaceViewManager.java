package com.libmpv;

import com.libmpv.LibmpvSurfaceView;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.annotations.ReactPropGroup;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import dev.jdtech.mpv.MPVLib;
import java.util.Map;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;

// Available event constants
// https://github.com/facebook/react-native/blob/main/packages/react-native/ReactAndroid/src/main/java/com/facebook/react/uimanager/UIManagerModuleConstants.java
// An example video player using VLC
// https://github.com/razorRun/react-native-vlc-media-player/blob/master/android/src/main/java/com/yuanzhou/vlc/vlcplayer/ReactVlcPlayerViewManager.java
public class LibmpvSurfaceViewManager extends SimpleViewManager<LibmpvSurfaceView> {

    public static final String REACT_CLASS = "LibmpvSurfaceView";

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

            LibmpvWrapper.getInstance().cleanup();
            view.getHolder().setFixedSize(_surfaceWidth, _surfaceHeight);
            LibmpvWrapper.getInstance().defaultSetup(view);
            LibmpvWrapper.getInstance().addEventObserver(new MPVLib.EventObserver() {
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
            });
            LibmpvWrapper.getInstance().addLogObserver(new MPVLib.LogObserver() {
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

            LibmpvWrapper.getInstance().play(_playUrl, options);
        }
    }

    @ReactProp(name = "playUrl")
    public void setPlayUrl(LibmpvSurfaceView view, String playUrl) {
        boolean recreateMpv = false;
        if (_playUrl != null && !_playUrl.equalsIgnoreCase(playUrl)) {
            recreateMpv = true;
        }
        _playUrl = playUrl;
        if (LibmpvWrapper.getInstance().isCreated() && !recreateMpv) {
            LibmpvWrapper.getInstance().play(_playUrl);
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
        if (LibmpvWrapper.getInstance().isCreated()) {
            LibmpvWrapper.getInstance().setSurfaceWidth(_surfaceWidth);
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
        if (LibmpvWrapper.getInstance().isCreated()) {
            LibmpvWrapper.getInstance().setSurfaceHeight(_surfaceHeight);
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
        if (LibmpvWrapper.getInstance().isCreated()) {
            String mpvIndex = "no";
            if (_audioIndex != -1) {
                mpvIndex = "" + (_audioIndex + 1);
            }
            LibmpvWrapper.getInstance().setOptionString("aid", mpvIndex);
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
        if (LibmpvWrapper.getInstance().isCreated()) {
            String mpvIndex = "no";
            if (_subtitleIndex != -1) {
                mpvIndex = "" + (_subtitleIndex + 1);
            }
            LibmpvWrapper.getInstance().setOptionString("sid", mpvIndex);
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
        if (LibmpvWrapper.getInstance().isCreated()) {
            LibmpvWrapper.getInstance().seekToSeconds(seconds);
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
        if (LibmpvWrapper.getInstance().isCreated() && LibmpvWrapper.getInstance().hasPlayedOnce()) {
            log.putString("playerState", LibmpvWrapper.getInstance().isPlaying() ? "play" : "paused");
            if (isPlaying && !LibmpvWrapper.getInstance().isPlaying()) {
                LibmpvWrapper.getInstance().unpause();
                log.putString("path", "Unpausing player");
            } else if (!isPlaying && LibmpvWrapper.getInstance().isPlaying()) {
                LibmpvWrapper.getInstance().pause();
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
