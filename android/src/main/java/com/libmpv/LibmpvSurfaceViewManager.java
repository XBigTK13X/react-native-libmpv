package com.libmpv;

import android.graphics.Color;
import android.view.SurfaceView;

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
public class LibmpvSurfaceViewManager extends SimpleViewManager<SurfaceView> {

    public static final String REACT_CLASS = "LibmpvSurfaceView";

    private static String PLAY_URL = null;
    private static Integer SURFACE_WIDTH = null;
    private static Integer SURFACE_HEIGHT = null;
    private static Integer AUDIO_INDEX = null;
    private static Integer SUBTITLE_INDEX = null;
    private static DeviceEventManagerModule.RCTDeviceEventEmitter _reactEventEmitter = null;

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public SurfaceView createViewInstance(ThemedReactContext reactContext) {
        return new SurfaceView(reactContext);
    }

    private void attemptCreation(SurfaceView view) {
        if (_reactEventEmitter == null) {
            ThemedReactContext reactContext = (ThemedReactContext) view.getContext();
            _reactEventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);

            WritableMap log = Arguments.createMap();
            log.putString("method", "attemptCreation");
            log.putString("argument", "make the event emitter");
            _reactEventEmitter.emit("libmpvLog", log);
        }
        if (PLAY_URL != null
                && SURFACE_WIDTH != null
                && SURFACE_HEIGHT != null
                && AUDIO_INDEX != null
                && SUBTITLE_INDEX != null) {

            WritableMap log = Arguments.createMap();
            log.putString("method", "attemptCreation");
            log.putString("argument", "initialize the MPV instance");
            _reactEventEmitter.emit("libmpvLog", log);

            LibmpvWrapper.getInstance().cleanup();
            view.getHolder().setFixedSize(SURFACE_WIDTH, SURFACE_HEIGHT);
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
            if (AUDIO_INDEX == -1) {
                options += ",aid=no";
            } else {
                options += ",aid=" + (AUDIO_INDEX + 1);
            }
            if (SUBTITLE_INDEX == -1) {
                options += ",sid=no";
            } else {
                options += ",sid=" + (SUBTITLE_INDEX + 1);
            }

            LibmpvWrapper.getInstance().play(PLAY_URL, options);
        }
    }

    @ReactProp(name = "playUrl")
    public void setPlayUrl(SurfaceView view, String playUrl) {
        boolean recreateMpv = false;
        if (PLAY_URL != null && !PLAY_URL.equalsIgnoreCase(playUrl)) {
            recreateMpv = true;
        }
        PLAY_URL = playUrl;
        if (LibmpvWrapper.getInstance().isCreated() && !recreateMpv) {
            LibmpvWrapper.getInstance().play(PLAY_URL);
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
    public void setSurfaceWidth(SurfaceView view, int surfaceWidth) {
        SURFACE_WIDTH = surfaceWidth;
        if (LibmpvWrapper.getInstance().isCreated()) {
            LibmpvWrapper.getInstance().setSurfaceWidth(SURFACE_WIDTH);
        } else {
            attemptCreation(view);
        }
        if (_reactEventEmitter != null) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "setSurfaceWidth");
            log.putString("argument", "" + SURFACE_WIDTH);
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

    @ReactProp(name = "surfaceHeight")
    public void setSurfaceHeight(SurfaceView view, int surfaceHeight
    ) {
        SURFACE_HEIGHT = surfaceHeight;
        if (LibmpvWrapper.getInstance().isCreated()) {
            LibmpvWrapper.getInstance().setSurfaceHeight(SURFACE_HEIGHT);
        } else {
            attemptCreation(view);
        }
        if (_reactEventEmitter != null) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "setSurfaceHeight");
            log.putString("argument", "" + SURFACE_HEIGHT);
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

    @ReactProp(name = "selectedAudioTrack")
    public void selectAudioTrack(SurfaceView view, int audioTrackIndex
    ) {
        AUDIO_INDEX = audioTrackIndex;
        if (LibmpvWrapper.getInstance().isCreated()) {
            String mpvIndex = "no";
            if (AUDIO_INDEX != -1) {
                mpvIndex = "" + (AUDIO_INDEX + 1);
            }
            LibmpvWrapper.getInstance().setOptionString("aid", mpvIndex);
        } else {
            attemptCreation(view);
        }
        if (_reactEventEmitter != null) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "selectAudioTrack");
            log.putString("argument", "" + AUDIO_INDEX);
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

    @ReactProp(name = "selectedSubtitleTrack")
    public void selectSubtitleTrack(SurfaceView view, int subtitleTrackIndex
    ) {
        SUBTITLE_INDEX = subtitleTrackIndex;
        if (LibmpvWrapper.getInstance().isCreated()) {
            String mpvIndex = "no";
            if (SUBTITLE_INDEX != -1) {
                mpvIndex = "" + (SUBTITLE_INDEX + 1);
            }
            LibmpvWrapper.getInstance().setOptionString("sid", mpvIndex);
        } else {
            attemptCreation(view);
        }
        if (_reactEventEmitter != null) {
            WritableMap log = Arguments.createMap();
            log.putString("method", "selectSubtitleTrack");
            log.putString("argument", "" + SUBTITLE_INDEX);
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

    @ReactProp(name = "seekToSeconds")
    public void seekTo(SurfaceView view, int seconds) {
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
    public void setIsPlaying(SurfaceView view, boolean isPlaying) {
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
