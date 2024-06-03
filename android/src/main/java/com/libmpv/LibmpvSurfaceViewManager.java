package com.libmpv;

import android.graphics.Color;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import dev.jdtech.mpv.MPVLib;

public class LibmpvSurfaceViewManager extends SimpleViewManager<SurfaceView> {
  public static final String REACT_CLASS = "LibmpvSurfaceView";

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

  @ReactProp(name="playUrl")
  public void register(SurfaceView view, String playUrl){
    LibmpvWrapper.getInstance().defaultSetup(view);
    LibmpvWrapper.getInstance().play(playUrl);
  }
}
