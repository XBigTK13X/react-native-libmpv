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
        if (!_created) {
            return;
        }
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
        try {
            if (_created) {
                MPVLib.setOptionString(option, setting);
            }
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }
    }

    public void setPropertyString(String property, String setting) {
        try {
            if (_created) {
                MPVLib.setPropertyString(property, setting);
            }
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }
    }

    public void useDefaultOptions() {
        this.setOptionString("tls-verify", "no");
        this.setOptionString("profile", "fast");
        this.setOptionString("vo", "gpu-next");
        this.setOptionString("force-window", "yes");
        this.setOptionString("ao", "audiotrack");
        this.setOptionString("gpu-context", "android");
        this.setOptionString("opengl-es", "yes");
        this.setOptionString("hwdec", "mediacodec");
        this.setOptionString("hwdec-codecs", "h264,hevc,mpeg4,mpeg2video,vp8,vp9,av1");

        this.setOptionString("cache", "yes");
        this.setOptionString("cache-pause-initial", "yes");
        this.setOptionString("demuxer-max-bytes", "32MiB");
        this.setOptionString("demuxer-max-back-bytes", "32MiB");

        this.setOptionString("sub-scale-with-window", "yes");
        this.setOptionString("sub-use-margins", "no");

        this.setOptionString("alang", "");
        this.setOptionString("slang", "");

        this.setOptionString("force-window", "no");
        this.setOptionString("keep-open", "always");
        this.setOptionString("save-position-on-quit", "no");
        this.setOptionString("sub-font-provider", "none");
        this.setOptionString("ytdl", "no");
        this.setOptionString("msg-level", "all=no");
    }

    public void init() {
        try {
            if (_created) {
                MPVLib.init();
            }
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }

    }

    public void command(String[] orders) {
        try {
            if (_created) {
                MPVLib.command(orders);
            }
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }
    }

    public void attachSurface(SurfaceView surfaceView) {
        try {
            if (_created) {
                MPVLib.attachSurface(surfaceView.getHolder().getSurface());
            }
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }

    }

    public void defaultSetup(SurfaceView surfaceView) {
        if (!this.create()) {
            return;
        }

        this.setOptionString("tls-verify", "no");
        this.setOptionString("profile", "fast");
        this.setOptionString("vo", "gpu-next");
        this.setOptionString("ao", "audiotrack");
        this.setOptionString("gpu-context", "android");
        this.setOptionString("opengl-es", "yes");
        this.setOptionString("hwdec", "mediacodec");
        this.setOptionString("hwdec-codecs", "h264,hevc,mpeg4,mpeg2video,vp8,vp9,av1");

        this.setOptionString("cache", "yes");
        this.setOptionString("cache-pause-initial", "yes");
        this.setOptionString("demuxer-max-bytes", "32MiB");
        this.setOptionString("demuxer-max-back-bytes", "32MiB");

        this.setOptionString("sub-scale-with-window", "yes");
        this.setOptionString("sub-use-margins", "no");

        this.setOptionString("alang", "");
        this.setOptionString("slang", "");

        // from the mpv repo: would crash before the surface is attached
        this.setOptionString("force-window", "no");

        this.setOptionString("keep-open", "always");
        this.setOptionString("save-position-on-quit", "no");
        this.setOptionString("sub-font-provider", "none");
        this.setOptionString("ytdl", "no");
        this.setOptionString("msg-level", "all=no");

        this.init();

        this.attachSurface(surfaceView);
        // From the mpv repo: This forces mpv to render subs/osd/whatever into our surface even if it would ordinarily not
        this.setOptionString("force-window", "yes");
        this.setOptionString("vo", "gpu-next");
    }

    public void play(String url) {
        this.command(new String[]{"loadfile", url});
    }

    public void pauseOrUnpause() {
        this.command(new String[]{"cycle", "pause"});
    }

    public void pause() {
        this.command(new String[]{"set_property", "pause", "true"});
    }

    public void unpause() {
        this.command(new String[]{"set_property", "pause", "false"});
    }

    public void removeObserver(MPVLib.EventObserver observer) {
        try {
            MPVLib.removeObserver(observer);
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }

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

    public void cleanup() {
        try {
            if (_created) {
                this.setPropertyString("vo", "null");
            }
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }
        try {
            if (_created) {
                this.setOptionString("force-window", "no");
            }
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }
        try {
            if (_created) {
                this.detachSurface();
            }
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }
        try {
            if (_created) {
                this.destroy();
            }
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }
    }
}
