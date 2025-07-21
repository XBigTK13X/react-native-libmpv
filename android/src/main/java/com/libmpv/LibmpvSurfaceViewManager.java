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
    private static final int COMMAND_SET_OPTION_STRING = 2;
    private static final int COMMAND_RUN_MPV_COMMAND = 3;

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
        return new LibmpvSurfaceView(reactContext, _reactEventEmitter);
    }

    @Override
    public void onDropViewInstance(LibmpvSurfaceView view) {
        super.onDropViewInstance(view);
        view.cleanup();
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "SetPlayUrl", COMMAND_SET_PLAY_URL,
                "SetOptionString", COMMAND_SET_OPTION_STRING,
                "RunMpvCommand", COMMAND_RUN_MPV_COMMAND
        );
    }

    @Override
    public void receiveCommand(LibmpvSurfaceView view, int commandId, @Nullable ReadableArray args) {
        if (args == null) {
            return;
        }
        view.log("receiveCommand", "" + commandId + " - " + args);
        switch (commandId) {
            case COMMAND_SET_PLAY_URL:
                String playUrl = args.getString(0);
                view.log("command - setPlayUrl", playUrl);
                this.setPlayUrl(view, playUrl);
                break;
            case COMMAND_SET_OPTION_STRING:
                String delimitedOptions = args.getString(0);
                String[] options = delimitedOptions.split("\\|");
                view.log("command - setOptionString", delimitedOptions);
                view.getMpv().setOptionString(options[0], options[1]);
                break;
            case COMMAND_RUN_MPV_COMMAND:
                String delimitedCommand = args.getString(0);
                String[] command = delimitedCommand.split("\\|");
                view.log("command - runMpvCommand", delimitedCommand);
                view.getMpv().command(command);
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
            view.log("attemptCreation", "initialize the MPV instance");
            view.createNativePlayer(_playUrl, _surfaceWidth, _surfaceHeight, _audioIndex, _subtitleIndex);
        }
    }

    @ReactProp(name = "playUrl")
    public void setPlayUrl(LibmpvSurfaceView view, String playUrl) {
        _playUrl = playUrl;
        if (view.isSurfaceReady()) {
            view.getMpv().play(_playUrl);
        } else {
            attemptCreation(view);
        }
        view.log("setPlayUrl", "" + playUrl);
    }

    @ReactProp(name = "surfaceWidth")
    public void setSurfaceWidth(LibmpvSurfaceView view, int surfaceWidth) {
        _surfaceWidth = surfaceWidth;
        if (view.isSurfaceReady()) {
            view.getMpv().setSurfaceWidth(_surfaceWidth);
        } else {
            attemptCreation(view);
        }
        view.log("setSurfaceWidth", "" + _surfaceWidth);
    }

    @ReactProp(name = "surfaceHeight")
    public void setSurfaceHeight(LibmpvSurfaceView view, int surfaceHeight
    ) {
        _surfaceHeight = surfaceHeight;
        if (view.isSurfaceReady()) {
            view.getMpv().setSurfaceHeight(_surfaceHeight);
        } else {
            attemptCreation(view);
        }
        view.log("setSurfaceHeight", "" + _surfaceHeight);
    }

    @ReactProp(name = "selectedAudioTrack")
    public void selectAudioTrack(LibmpvSurfaceView view, int audioTrackIndex
    ) {
        _audioIndex = audioTrackIndex;
        if (view.isSurfaceReady()) {
            String mpvIndex = "no";
            if (_audioIndex != -1) {
                mpvIndex = "" + (_audioIndex + 1);
            }
            view.getMpv().setOptionString("aid", mpvIndex);
        } else {
            attemptCreation(view);
        }
        view.log("selectAudioTrack", "" + _audioIndex);
    }

    @ReactProp(name = "selectedSubtitleTrack")
    public void selectSubtitleTrack(LibmpvSurfaceView view, int subtitleTrackIndex
    ) {
        _subtitleIndex = subtitleTrackIndex;
        if (view.isSurfaceReady()) {
            String mpvIndex = "no";
            if (_subtitleIndex != -1) {
                mpvIndex = "" + (_subtitleIndex + 1);
            }
            view.getMpv().setOptionString("sid", mpvIndex);
        } else {
            attemptCreation(view);
        }
        view.log("selectSubtitleTrack", "" + _subtitleIndex);
    }

    @ReactProp(name = "seekToSeconds")
    public void seekTo(LibmpvSurfaceView view, int seconds) {
        if (view.isSurfaceReady()) {
            view.getMpv().seekToSeconds(seconds);
        }
        view.log("seekToSeconds", "" + seconds);
    }

    @ReactProp(name = "isPlaying")
    public void setIsPlaying(LibmpvSurfaceView view, boolean isPlaying) {
        WritableMap log = Arguments.createMap();
        log.putString("method", "setIsPlaying");
        log.putString("argument", isPlaying ? "true" : "false");
        if (view.isSurfaceReady() && view.getMpv().hasPlayedOnce()) {
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
        _reactEventEmitter.emit("libmpvLog", log);
    }
}
