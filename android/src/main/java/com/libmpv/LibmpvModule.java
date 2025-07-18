package com.libmpv;

import com.libmpv.LibmpvSurfaceView;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import android.util.Log;

import android.view.Surface;
import android.view.SurfaceView;
import android.content.Context;

public class LibmpvModule extends ReactContextBaseJavaModule {

    Context _applicationContext;

    public LibmpvModule(ReactApplicationContext reactContext) {
        super(reactContext);
        LibmpvWrapper.getInstance().setContext(reactContext);
    }

    @Override
    public String getName() {
        return "Libmpv";
    }

    @ReactMethod
    public void addListener(String type) {
    }

    @ReactMethod
    public void removeListeners(Integer type) {
    }

    @ReactMethod
    public void create(Promise promise) {
        LibmpvWrapper.getInstance().create();
        promise.resolve(true);
    }

    @ReactMethod
    public void setOptionString(String option, String setting, Promise promise) {
        LibmpvWrapper.getInstance().setOptionString(option, setting);
        promise.resolve(true);
    }

    @ReactMethod
    public void init(Promise promise) {
        LibmpvWrapper.getInstance().init();
        promise.resolve(true);
    }

    @ReactMethod
    public void command(String orders, Promise promise) {
        String[] splitOrders = orders.split("\\|");
        LibmpvWrapper.getInstance().command(splitOrders);
        promise.resolve(true);
    }

    @ReactMethod
    public void attachSurface(LibmpvSurfaceView surfaceView, Promise promise) {
        LibmpvWrapper.getInstance().attachSurface(surfaceView);
        promise.resolve(true);
    }

    @ReactMethod
    public void defaultSetup(LibmpvSurfaceView surfaceView, Promise promise) {
        LibmpvWrapper.getInstance().defaultSetup(surfaceView);
        promise.resolve(true);
    }

    @ReactMethod
    public void play(String url, Promise promise) {
        LibmpvWrapper.getInstance().play(url);
        promise.resolve(true);
    }

    @ReactMethod
    public void pauseOrUnpause(Promise promise) {
        LibmpvWrapper.getInstance().pauseOrUnpause();
        promise.resolve(true);
    }

    @ReactMethod
    public void pause(Promise promise) {
        LibmpvWrapper.getInstance().pause();
        promise.resolve(true);
    }

    @ReactMethod
    public void unpause(Promise promise) {
        LibmpvWrapper.getInstance().unpause();
        promise.resolve(true);
    }

    @ReactMethod
    public void destroy(Promise promise) {
        LibmpvWrapper.getInstance().destroy();
        promise.resolve(true);
    }

    @ReactMethod
    public void detachSurface(Promise promise) {
        LibmpvWrapper.getInstance().detachSurface();
        promise.resolve(true);
    }

    @ReactMethod
    public void cleanup(Promise promise) {
        LibmpvWrapper.getInstance().cleanup();
        promise.resolve(true);
    }

}
