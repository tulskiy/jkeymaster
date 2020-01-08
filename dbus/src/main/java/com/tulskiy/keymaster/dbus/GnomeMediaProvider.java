package com.tulskiy.keymaster.dbus;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.MediaKey;
import com.tulskiy.keymaster.common.Provider;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusSigHandler;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
import org.gnome.SettingsDaemon.MediaKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * User: tulskiy
 * Date: 1/3/16
 */
public class GnomeMediaProvider extends Provider {
    private static final Logger LOGGER = LoggerFactory.getLogger(GnomeMediaProvider.class);

    private String appName;
    private DBusConnection bus;
    private MediaKeys keys;

    private Map<MediaKey, HotKeyListener> listeners = new HashMap<MediaKey, HotKeyListener>();

    public GnomeMediaProvider(String appName) {
        this.appName = appName;
        init();
    }

    @Override
    protected void init() {
        try {
            bus = DBusConnection.getConnection(DBusConnection.SESSION);
            bus.addSigHandler(MediaKeys.MediaPlayerKeyPressed.class, new Handler());
            keys = bus.getRemoteObject("org.gnome.SettingsDaemon", "/org/gnome/SettingsDaemon/MediaKeys", MediaKeys.class);
            keys.GrabMediaPlayerKeys(appName, new UInt32(0));

            LOGGER.info("dbus listener initialized, app {}", appName);
        } catch (DBusException e) {
            LOGGER.warn("could not initialize dbus", e);
        }
    }

    @Override
    public void reset() {
        listeners.clear();
    }

    @Override
    public void stop() {
        keys.ReleaseMediaPlayerKeys(appName);
        bus.disconnect();

        super.stop();
    }

    @Override
    public void register(KeyStroke keyCode, HotKeyListener listener) {
        throw new UnsupportedOperationException("only media keys are supported");
    }

    @Override
    public void register(MediaKey mediaKey, HotKeyListener listener) {
        listeners.put(mediaKey, listener);
    }

    @Override
    public void unregister(KeyStroke keyCode) {
        throw new UnsupportedOperationException("only media keys are supported");
    }

    @Override
    public void unregister(MediaKey mediaKey) {
        listeners.remove(mediaKey);
    }

    private void fire(MediaKey key) {
        HotKeyListener listener = listeners.get(key);
        if (listener != null) {
            fireEvent(new HotKey(key, listener));
        }
    }

    class Handler implements DBusSigHandler<MediaKeys.MediaPlayerKeyPressed> {
        @Override
        public void handle(MediaKeys.MediaPlayerKeyPressed arg) {
            String key = arg.key;
            LOGGER.info("received media key {}", key);
            if ("Play".equals(key)) {
                fire(MediaKey.MEDIA_PLAY_PAUSE);
            } else if ("Stop".equals(key)) {
                fire(MediaKey.MEDIA_STOP);
            } else if ("Previous".equals(key)) {
                fire(MediaKey.MEDIA_PREV_TRACK);
            } else if ("Next".equals(key)) {
                fire(MediaKey.MEDIA_NEXT_TRACK);
            }
        }
    }
}
