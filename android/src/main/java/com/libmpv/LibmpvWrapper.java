package com.libmpv;

import dev.jdtech.mpv.MPVLib;

import com.libmpv.LibmpvSurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

// https://mpv.io/manual/master/#property-manipulation
// https://github.com/jarnedemeulemeester/findroid/blob/main/player/video/src/main/java/dev/jdtech/jellyfin/mpv/MPVPlayer.kt
// https://github.com/mpv-android/mpv-android/blob/7ae2b0fdc7f5a0948a1327191bf56798884f839b/app/src/main/java/is/xyz/mpv/MPVView.kt#L22
// https://github.com/mpv-android/mpv-android/blob/b37f1564de1832efec43962787855c09b077c714/app/src/main/java/is/xyz/mpv/BaseMPVView.kt#L85
public class LibmpvWrapper {

    private static boolean swallow = true;

    private Context _applicationContext;
    private boolean _created;
    private boolean _isPlaying;
    private boolean _hasPlayedOnce;
    private MPVLib.EventObserver _eventObserver;
    private MPVLib.LogObserver _logObserver;
    private String _mpvDirectory;
    private int _surfaceWidth = -1;
    private int _surfaceHeight = -1;
    private LibmpvSurfaceView _surfaceView;
    private MPVLib _mpv;

    public LibmpvWrapper(Context context) {
        _created = false;
        _isPlaying = false;
        _hasPlayedOnce = false;
        _applicationContext = context;
        _mpv = new MPVLib();
    }

    public boolean create() {
        _mpv.create(_applicationContext);
        this.createMpvDirectory();
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

    public String getMpvDirectoryPath() {
        return _mpvDirectory;
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
            _mpv.removeObservers();
            _eventObserver = observer;
            _mpv.addObserver(_eventObserver);
            _mpv.observeProperty("demuxer-cache-time", MPVLib.MPV_FORMAT_INT64);
            _mpv.observeProperty("duration", MPVLib.MPV_FORMAT_INT64);
            _mpv.observeProperty("eof-reached", MPVLib.MPV_FORMAT_FLAG);
            _mpv.observeProperty("paused-for-cache", MPVLib.MPV_FORMAT_FLAG);
            _mpv.observeProperty("seekable", MPVLib.MPV_FORMAT_FLAG);
            _mpv.observeProperty("speed", MPVLib.MPV_FORMAT_DOUBLE);
            _mpv.observeProperty("time-pos", MPVLib.MPV_FORMAT_INT64);
            _mpv.observeProperty("track-list", MPVLib.MPV_FORMAT_STRING);
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
            _mpv.removeLogObservers();
            _logObserver = observer;
            _mpv.addLogObserver(_logObserver);
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
                _mpv.setOptionString(option, setting);
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
                _mpv.setPropertyString(property, setting);
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
                _mpv.init();
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
                _mpv.command(orders);
            }
        } catch (Exception e) {
            logException(e);

            if (!swallow) {
                throw e;
            }
        }
    }

    public void attachSurface(LibmpvSurfaceView surfaceView) {
        try {
            if (_created) {
                _surfaceView = surfaceView;
                this.applySurfaceDimensions();
                _mpv.attachSurface(_surfaceView.getHolder().getSurface());
            }
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }

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
            _mpv.detachSurface();
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
    }

    public void destroy() {
        try {
            _mpv.removeObservers();
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
        try {
            _mpv.removeLogObservers();
        } catch (Exception e) {
            if (!swallow) {
                throw e;
            }
        }
        try {
            _mpv.destroy();
            _created = false;
        } catch (Exception e) {
            logException(e);
            if (!swallow) {
                throw e;
            }
        }
    }

    public void cleanup() {
        if (_created) {
            try {
                this.pause();
                this.setPropertyString("vo", "null");
                this.setPropertyString("ao", "null");
            } catch (Exception e) {
                logException(e);
                if (!swallow) {
                    throw e;
                }
            }
            try {
                this.setOptionString("force-window", "no");
            } catch (Exception e) {
                logException(e);
                if (!swallow) {
                    throw e;
                }
            }
            try {
                this.detachSurface();
            } catch (Exception e) {
                logException(e);
                if (!swallow) {
                    throw e;
                }
            }
            try {
                this.destroy();
            } catch (Exception e) {
                logException(e);
                if (!swallow) {
                    throw e;
                }
            }
        }
    }
}
