package com.libmpv;

import dev.jdtech.mpv.MPVLib;

import android.view.Surface;
import android.view.SurfaceView;
import android.content.Context;

// https://github.com/jarnedemeulemeester/findroid/blob/main/player/video/src/main/java/dev/jdtech/jellyfin/mpv/MPVPlayer.kt

// https://github.com/mpv-android/mpv-android/blob/7ae2b0fdc7f5a0948a1327191bf56798884f839b/app/src/main/java/is/xyz/mpv/MPVView.kt#L22

public class LibmpvWrapper {
  private static boolean swallow = true;
  private static LibmpvWrapper __instance;

  public static LibmpvWrapper getInstance() {
    if (__instance == null) {
      __instance = new LibmpvWrapper();
    }
    return __instance;
  }

  private Context _applicationContext;
  private boolean _created;

  private LibmpvWrapper() {
    _created = false;
  }

  public void setContext(Context applicationContext) {
    _applicationContext = applicationContext;
  }

  public boolean create() {
    if (!_created) {
      MPVLib.create(_applicationContext);
      _created = true;
      return true;
    }
    return false;
  }

  public void addEventObserver(MPVLib.EventObserver observer) {
    MPVLib.addObserver(observer);
    MPVLib.observeProperty("track-list", MPVLib.MPV_FORMAT_STRING);
    MPVLib.observeProperty("paused-for-cache", MPVLib.MPV_FORMAT_FLAG);
    MPVLib.observeProperty("eof-reached", MPVLib.MPV_FORMAT_FLAG);
    MPVLib.observeProperty("seekable", MPVLib.MPV_FORMAT_FLAG);
    MPVLib.observeProperty("time-pos", MPVLib.MPV_FORMAT_INT64);
    MPVLib.observeProperty("duration", MPVLib.MPV_FORMAT_INT64);
    MPVLib.observeProperty("demuxer-cache-time", MPVLib.MPV_FORMAT_INT64);
    MPVLib.observeProperty("speed", MPVLib.MPV_FORMAT_DOUBLE);
  }

  public void setOptionString(String option, String setting) {
    MPVLib.setOptionString(option, setting);
  }

  public void useDefaultOptions() {
    MPVLib.setOptionString("tls-verify", "no");
    MPVLib.setOptionString("profile", "fast");
    MPVLib.setOptionString("vo", "gpu-next");
    MPVLib.setOptionString("force-window", "yes");
    MPVLib.setOptionString("ao", "audiotrack");
    MPVLib.setOptionString("gpu-context", "android");
    MPVLib.setOptionString("opengl-es", "yes");
    MPVLib.setOptionString("hwdec", "mediacodec");
    MPVLib.setOptionString("hwdec-codecs", "h264,hevc,mpeg4,mpeg2video,vp8,vp9,av1");

    MPVLib.setOptionString("cache", "yes");
    MPVLib.setOptionString("cache-pause-initial", "yes");
    MPVLib.setOptionString("demuxer-max-bytes", "32MiB");
    MPVLib.setOptionString("demuxer-max-back-bytes", "32MiB");

    MPVLib.setOptionString("sub-scale-with-window", "yes");
    MPVLib.setOptionString("sub-use-margins", "no");

    MPVLib.setOptionString("alang", "");
    MPVLib.setOptionString("slang", "");

    MPVLib.setOptionString("force-window", "no");
    MPVLib.setOptionString("keep-open", "always");
    MPVLib.setOptionString("save-position-on-quit", "no");
    MPVLib.setOptionString("sub-font-provider", "none");
    MPVLib.setOptionString("ytdl", "no");
    MPVLib.setOptionString("msg-level", "all=no");
  }

  public void init() {
    try {
      MPVLib.init();
    } catch (Exception e) {
      if (!swallow) {
        throw e;
      }
    }

  }

  public void command(String[] orders) {
    MPVLib.command(orders);
  }

  public void attachSurface(SurfaceView surfaceView) {
    MPVLib.attachSurface(surfaceView.getHolder().getSurface());
  }

  public void defaultSetup(SurfaceView surfaceView) {
    if (!this.create()) {
      return;
    }

    MPVLib.setOptionString("tls-verify", "no");
    MPVLib.setOptionString("profile", "fast");
    MPVLib.setOptionString("vo", "gpu-next");
    MPVLib.setOptionString("ao", "audiotrack");
    MPVLib.setOptionString("gpu-context", "android");
    MPVLib.setOptionString("opengl-es", "yes");
    MPVLib.setOptionString("hwdec", "mediacodec");
    MPVLib.setOptionString("hwdec-codecs", "h264,hevc,mpeg4,mpeg2video,vp8,vp9,av1");

    MPVLib.setOptionString("cache", "yes");
    MPVLib.setOptionString("cache-pause-initial", "yes");
    MPVLib.setOptionString("demuxer-max-bytes", "32MiB");
    MPVLib.setOptionString("demuxer-max-back-bytes", "32MiB");

    MPVLib.setOptionString("sub-scale-with-window", "yes");
    MPVLib.setOptionString("sub-use-margins", "no");

    MPVLib.setOptionString("alang", "");
    MPVLib.setOptionString("slang", "");

    // from the mpv repo: would crash before the surface is attached
    MPVLib.setOptionString("force-window", "no");

    MPVLib.setOptionString("keep-open", "always");
    MPVLib.setOptionString("save-position-on-quit", "no");
    MPVLib.setOptionString("sub-font-provider", "none");
    MPVLib.setOptionString("ytdl", "no");
    MPVLib.setOptionString("msg-level", "all=no");

    this.init();

    MPVLib.attachSurface(surfaceView.getHolder().getSurface());
    // From the mpv repo: This forces mpv to render subs/osd/whatever into our surface even if it would ordinarily not
    MPVLib.setOptionString("force-window", "yes");
    MPVLib.setOptionString("vo", "gpu-next");
  }

  public void play(String url) {
    MPVLib.command(new String[]{"loadfile", url});
  }

  public void removeObserver(MPVLib.EventObserver observer) {
    MPVLib.removeObserver(observer);
  }

  public void detachSurface() {
    try {
      MPVLib.detachSurface();
    } catch (Exception e) {
      if (!swallow) {
        throw e;
      }
    }
  }

  public void cleanup() {
    try {
      MPVLib.setPropertyString("vo", "null");
    } catch (Exception e) {
      if (!swallow) {
        throw e;
      }
    }
    try {
      MPVLib.setOptionString("force-window", "no");
    } catch (Exception e) {
      if (!swallow) {
        throw e;
      }
    }
    try {
      MPVLib.detachSurface();
    } catch (Exception e) {
      if (!swallow) {
        throw e;
      }
    }
    try {
      this.destroy();
    }
    catch(Exception e){
      if(!swallow){
        throw e;
      }
    }
  }

  public void destroy() {
    try {
      MPVLib.destroy();
      _created = false;
    } catch (Exception e) {
      if (!swallow) {
        throw e;
      }
    }
  }
}
