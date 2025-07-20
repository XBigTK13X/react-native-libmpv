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

// This exists for the sake of the event emitter
public class LibmpvModule extends ReactContextBaseJavaModule {

    @Override
    public String getName() {
        return "Libmpv";
    }
}
