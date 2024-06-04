package com.libmpv;

import android.graphics.Color;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import dev.jdtech.mpv.MPVLib;
import java.util.Map;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.common.MapBuilder;

// https://github.com/razorRun/react-native-vlc-media-player/blob/master/android/src/main/java/com/yuanzhou/vlc/vlcplayer/ReactVlcPlayerViewManager.java

public class LibmpvSurfaceViewManager extends SimpleViewManager<SurfaceView> {
  public static final String REACT_CLASS = "LibmpvSurfaceView";
  // TODO This is dumb and dangerous. Pull it out into a view wrapper
  private static ThemedReactContext __eventContext;
  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  @NonNull
  public SurfaceView createViewInstance(ThemedReactContext reactContext) {
    __eventContext = reactContext;
    return new SurfaceView(reactContext);
  }

  @Override
  public Map getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.builder().put(
      "libmpvEvent",
      MapBuilder.of("registrationName","libmpvEvent")
    ).build();
  }

  @ReactProp(name="playUrl")
  public void register(SurfaceView view, String playUrl){
    RCTEventEmitter reactEventEmitter = __eventContext.getJSModule(RCTEventEmitter.class);
    System.out.println("[LIBMPV] Logging ");
    LibmpvWrapper.getInstance().defaultSetup(view);
    WritableMap event = Arguments.createMap();
    event.putString("message", "MyMessage");
    reactEventEmitter.receiveEvent(view.getId(), "libmpvEvent", event);
    LibmpvWrapper.getInstance().addEventObserver(new MPVLib.EventObserver(){
      @Override
      public void eventProperty(@NonNull String property){

      }
      @Override
      public void eventProperty(@NonNull String property, long value){

      }
      @Override
      public void eventProperty(@NonNull String property, double value){

      }
      @Override
      public void eventProperty(@NonNull String property, boolean value){

      }
      @Override
      public void eventProperty(@NonNull String property, @NonNull String value){

      }
      @Override
      public void event(@MPVLib.Event int eventId){

      }
    });
    LibmpvWrapper.getInstance().play(playUrl);
  }
}
