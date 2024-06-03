package com.libmpv;

import dev.jdtech.mpv.MPVLib;

import android.view.Surface;
import android.view.SurfaceView;
import android.content.Context;

public class LibmpvWrapper {
  private static LibmpvWrapper __instance;
  public static LibmpvWrapper getInstance(){
    if(__instance == null){
      __instance = new LibmpvWrapper();
    }
    return __instance;
  }
  private Context _applicationContext;
  private LibmpvWrapper(){

  }

  public void setContext(Context applicationContext){
    _applicationContext = applicationContext;
  }
  public void create(){
    MPVLib.create(_applicationContext);
  }

  public void setOptionString(String option, String setting){
    MPVLib.setOptionString(option, setting);
  }

  public void useDefaultOptions(){
    MPVLib.setOptionString("tls-verify","no");
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

  public void init(){
    MPVLib.init();
  }

  public void command(String[] orders){
    MPVLib.command(orders);
  }

  public void attachSurface(SurfaceView surfaceView){
    MPVLib.attachSurface(surfaceView.getHolder().getSurface());
  }

  public void defaultSetup(SurfaceView surfaceView){
    MPVLib.create(_applicationContext);

    MPVLib.setOptionString("tls-verify","no");
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

    MPVLib.setOptionString("force-window", "no");
    MPVLib.setOptionString("keep-open", "always");
    MPVLib.setOptionString("save-position-on-quit", "no");
    MPVLib.setOptionString("sub-font-provider", "none");
    MPVLib.setOptionString("ytdl", "no");
    MPVLib.setOptionString("msg-level", "all=no");

    MPVLib.init();

    MPVLib.attachSurface(surfaceView.getHolder().getSurface());
    MPVLib.setOptionString("force-window", "yes");
    MPVLib.setOptionString("vo", "gpu-next");
  }

  public void play(String url){
    MPVLib.command(new String[]{"loadfile",url});
  }
}
