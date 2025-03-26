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
    private boolean _isPlaying;
    private MPVLib.EventObserver _eventObserver;
    private MPVLib.LogObserver _logObserver;

    private LibmpvWrapper() {
        _created = false;
        _isPlaying = false;
    }

    public void setContext(Context applicationContext) {
        _applicationContext = applicationContext;
    }

    public boolean create() {
        if (_created) {
            try {
                this.destroy();
                _created = false;
            } catch (Exception e) {
                if (!swallow) {
                    throw e;
                }
            }

        }
        MPVLib.create(_applicationContext);
        _created = true;
        return true;
    }

    public boolean isCreated() {
        return _created;
    }

    public boolean isPlaying() {
        return _isPlaying;
    }

    private void logException(Exception exception) {
        try {
            if (_logObserver != null) {
                _logObserver.logMessage("RNLE", 20, exception.getMessage());
            }
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }
    }

    public void addEventObserver(MPVLib.EventObserver observer) {
        try {
            if (!_created) {
                return;
            }
            MPVLib.removeObservers();
            _eventObserver = observer;
            MPVLib.addObserver(_eventObserver);
            MPVLib.observeProperty("demuxer-cache-time", MPVLib.MPV_FORMAT_INT64);
            MPVLib.observeProperty("duration", MPVLib.MPV_FORMAT_INT64);
            MPVLib.observeProperty("eof-reached", MPVLib.MPV_FORMAT_FLAG);
            MPVLib.observeProperty("paused-for-cache", MPVLib.MPV_FORMAT_FLAG);
            MPVLib.observeProperty("seekable", MPVLib.MPV_FORMAT_FLAG);
            MPVLib.observeProperty("speed", MPVLib.MPV_FORMAT_DOUBLE);
            MPVLib.observeProperty("time-pos", MPVLib.MPV_FORMAT_INT64);
            MPVLib.observeProperty("track-list", MPVLib.MPV_FORMAT_STRING);
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
    }

    public void addLogObserver(MPVLib.LogObserver observer) {
        try {
            if (!_created) {
                return;
            }
            MPVLib.removeLogObservers();
            _logObserver = observer;
            MPVLib.addLogObserver(_logObserver);
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }

    }

    public void setOptionString(String option, String setting) {
        try {
            if (_created) {
                MPVLib.setOptionString(option, setting);
            }
        } catch (Exception e) {
            logException(e);
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
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
    }

    public void init() {
        try {
            if (_created) {
                MPVLib.init();
            }
        } catch (Exception e) {
            logException(e);
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
            logException(e);

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
            logException(e);
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
        //this.setOptionString("hwdec", "mediacodec");
        //this.setOptionString("hwdec-codecs", "h264,hevc,mpeg4,mpeg2video,vp8,vp9,av1");
        this.setOptionString("hwdec", "mediacodec-copy");
        this.setOptionString("hwdec-codecs", "all");

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
        if (!_isPlaying) {
            this.command(new String[]{"loadfile", url});
            _isPlaying = true;
        }
    }

    public void pauseOrUnpause() {
        if (_isPlaying) {
            this.pause();
        } else {
            this.unpause();
        }
    }

    public void pause() {
        if (_isPlaying) {
            this.command(new String[]{"set", "pause", "yes"});
            _isPlaying = false;
        }
    }

    public void unpause() {
        if (!_isPlaying) {
            this.command(new String[]{"set", "pause", "no"});
            _isPlaying = true;
        }
    }

    public void detachSurface() {
        try {
            MPVLib.detachSurface();
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
    }

    public void destroy() {
        try {
            MPVLib.removeObservers();
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
        try {
            MPVLib.removeLogObservers();
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }
        try {
            MPVLib.destroy();
            _created = false;
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
    }

    public void cleanup() {
        try {
            if (_created) {
                this.pause();
                this.setPropertyString("vo", "null");
                this.setPropertyString("ao", "null");
            }
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
        try {
            if (_created) {
                this.setOptionString("force-window", "no");
            }
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
        try {
            if (_created) {
                this.detachSurface();
            }
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
        try {
            if (_created) {
                this.destroy();
            }
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
    }
}
