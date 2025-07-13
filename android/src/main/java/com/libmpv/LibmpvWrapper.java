package com.libmpv;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;

import dev.jdtech.mpv.MPVLib;

import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.Surface;
import android.content.Context;

import android.util.Log;

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
    private boolean _hasPlayedOnce;
    private MPVLib.EventObserver _eventObserver;
    private MPVLib.LogObserver _logObserver;
    private String _mpvDirectory;
    private int _surfaceWidth = -1;
    private int _surfaceHeight = -1;
    private SurfaceView _surfaceView;

    private LibmpvWrapper() {
        _created = false;
        _isPlaying = false;
        _hasPlayedOnce = false;
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

    public boolean hasPlayedOnce() {
        return _hasPlayedOnce;
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
                _surfaceView = surfaceView;
                this.applySurfaceDimensions();
                MPVLib.attachSurface(_surfaceView.getHolder().getSurface());
            }
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }

    }

    public void createMpvDirectory() {
        File mpvDir = new File(_applicationContext.getExternalFilesDir("mpv"), "mpv");
        try {
            _mpvDirectory = mpvDir.getAbsolutePath();
            if (!mpvDir.exists()) {
                if (!mpvDir.mkdirs()) {
                    Log.e("react-native-libmpv", "exception", new IllegalArgumentException("Unable to create " + mpvDir));
                }
            } else {
                return;
            }
            String mpvFontPath = mpvDir + "/subfont.ttf";
            OutputStream fontOut = new FileOutputStream(mpvFontPath);
            final InputStream subfontIn = _applicationContext.getAssets().open("subfont.ttf");
            byte[] subfontBuf = new byte[1024];
            int subfontLen;
            while ((subfontLen = subfontIn.read(subfontBuf)) > 0) {
                fontOut.write(subfontBuf, 0, subfontLen);
            }
            subfontIn.close();
            fontOut.close();

            String mpvConfPath = mpvDir + "/mpv.conf";
            OutputStream confOut = new FileOutputStream(mpvConfPath);
            final InputStream mpvConfIn = _applicationContext.getAssets().open("mpv.conf");
            byte[] confBuf = new byte[1024];
            int confLen;
            while ((confLen = mpvConfIn.read(confBuf)) > 0) {
                confOut.write(confBuf, 0, confLen);
            }
            mpvConfIn.close();
            confOut.close();
        } catch (Exception e) {
            Log.e("react-native-libmpv", "Unable to create the directory " + mpvDir, e);
        }
    }

    // Modified from the Findroid defaults
    public void defaultSetup(SurfaceView surfaceView) {
        if (!this.create()) {
            return;
        }

        this.createMpvDirectory();

        if (_mpvDirectory == null) {
            Log.e("react-native-libmpv", "exception", new IllegalArgumentException("Unable to create the dir!"));
        }

        this.setOptionString("tls-verify", "no");
        this.setOptionString("config", "yes");
        this.setOptionString("config-dir", _mpvDirectory);
        this.setOptionString("vo", "gpu-next");
        this.setOptionString("ao", "audiotrack");
        this.setOptionString("vf", "no");
        this.setOptionString("af", "no");
        this.setOptionString("profile", "fast");
        this.setOptionString("gpu-context", "android");
        this.setOptionString("opengl-es", "yes");
        this.setOptionString("hwdec", "auto");
        this.setOptionString("hwdec-codecs", "all");
        this.setOptionString("cache", "yes");
        this.setOptionString("cache-pause-initial", "yes");
        this.setOptionString("demuxer-max-bytes", "256MiB");
        this.setOptionString("demuxer-max-back-bytes", "256MiB");
        this.setOptionString("cache-secs", "5");
        this.setOptionString("demuxer-readahead-secs", "5");

        this.setOptionString("sub-scale-with-window", "yes");
        this.setOptionString("sub-use-margins", "no");

        this.setOptionString("alang", "");
        this.setOptionString("slang", "");

        // from the mpv repo: Without this mpv would crash before the surface is attached
        this.setOptionString("force-window", "no");

        this.setOptionString("keep-open", "always");
        this.setOptionString("save-position-on-quit", "no");
        this.setOptionString("sub-font-provider", "none");
        this.setOptionString("sub-font-dir", _mpvDirectory);

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
            this.command(new String[]{"set", "pause", "no"});
            _hasPlayedOnce = true;
            _isPlaying = true;
        }
    }

    public void play(String url, String options) {
        if (!_isPlaying) {
            this.command(new String[]{"loadfile", url, "replace", "0", options});
            this.command(new String[]{"set", "pause", "no"});
            _hasPlayedOnce = true;
            _isPlaying = true;
        }
    }

    public void pauseOrUnpause() {
        if (!hasPlayedOnce()) {
            return;
        }
        if (_isPlaying) {
            this.pause();
        } else {
            this.unpause();
        }
    }

    public void pause() {
        if (!hasPlayedOnce()) {
            return;
        }
        if (_isPlaying) {
            this.command(new String[]{"set", "pause", "yes"});
            _isPlaying = false;
        }
    }

    public void unpause() {
        if (!hasPlayedOnce()) {
            return;
        }
        if (!_isPlaying) {
            this.command(new String[]{"set", "pause", "no"});
            _isPlaying = true;
        }
    }

    public void seekToSeconds(Integer seconds) {
        if (_created) {
            this.command(new String[]{"seek", seconds + "", "absolute"});
        }
    }

    private void applySurfaceDimensions() {
        if (_surfaceHeight != -1
                && _surfaceWidth != -1
                && _surfaceView != null) {
            _surfaceView.getHolder().setFixedSize(_surfaceWidth, _surfaceHeight);
        }
    }

    public void setSurfaceWidth(int width) {
        _surfaceWidth = width;
        this.applySurfaceDimensions();
    }

    public void setSurfaceHeight(int height) {
        _surfaceHeight = height;
        this.applySurfaceDimensions();
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
