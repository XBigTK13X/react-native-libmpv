package com.libmpv;

import android.graphics.Color;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.annotations.ReactProp;
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

    private static final boolean DEBUG_VIEW_MANAGER = false;
    private static int CREATE_COUNT = 0;
    private static int REGISTER_COUNT = 0;
    private static int IS_PLAYING_COUNT = 0;

    private DeviceEventManagerModule.RCTDeviceEventEmitter _reactEventEmitter;

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public SurfaceView createViewInstance(ThemedReactContext reactContext) {
        if (DEBUG_VIEW_MANAGER) {
            CREATE_COUNT += 1;
        }
        return new SurfaceView(reactContext);
    }

    @ReactProp(name = "playUrl")
    public void register(SurfaceView view, String playUrl) {
        if (DEBUG_VIEW_MANAGER) {
            REGISTER_COUNT += 1;
        }
        LibmpvWrapper.getInstance().cleanup();
        ThemedReactContext reactContext = (ThemedReactContext) view.getContext();
        _reactEventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
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
                if (DEBUG_VIEW_MANAGER) {
                    event.putString("REGISTER", "" + REGISTER_COUNT);
                    event.putString("CREATE", "" + CREATE_COUNT);
                    event.putString("IS_PLAYING_COUNT", "" + IS_PLAYING_COUNT);
                }
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
                if (DEBUG_VIEW_MANAGER) {
                    log.putString("REGISTER", "" + REGISTER_COUNT);
                    log.putString("CREATE", "" + CREATE_COUNT);
                    log.putString("IS_PLAYING_COUNT", "" + IS_PLAYING_COUNT);
                }
                _reactEventEmitter.emit("libmpvLog", log);
            }
        });
        LibmpvWrapper.getInstance().play(playUrl);
    }

    @ReactProp(name = "isPlaying")
    public void setIsPlaying(SurfaceView view, boolean isPlaying) {
        if (DEBUG_VIEW_MANAGER) {
            IS_PLAYING_COUNT += 1;
        }
        WritableMap log = Arguments.createMap();
        log.putString("method", "setIsPlaying");
        log.putString("argument", isPlaying ? "true" : "false");
        log.putString("playerState", LibmpvWrapper.getInstance().isPlaying() ? "play" : "paused");
        if (LibmpvWrapper.getInstance().isCreated()) {
            if (isPlaying && !LibmpvWrapper.getInstance().isPlaying()) {
                LibmpvWrapper.getInstance().unpause();
                log.putString("path", "Unpausing player");
            } else if (!isPlaying && LibmpvWrapper.getInstance().isPlaying()) {
                LibmpvWrapper.getInstance().pause();
                log.putString("path", "Pausing player");
            }
        } else {
            log.putString("path", "Instance not created");
        }
        if (_reactEventEmitter != null) {
            _reactEventEmitter.emit("libmpvLog", log);
        }
    }

}
